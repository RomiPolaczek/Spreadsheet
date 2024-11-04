package spreadsheet.client.component.mainSheet.header;

import spreadsheet.client.component.mainSheet.MainSheetController;
import dto.DTOcell;
import expression.parser.FunctionArgument;
import expression.parser.Operation;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import spreadsheet.client.util.ShowAlert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UpdateCellFormat {

    private String coord;
    private String inputType;
    private Operation selectedOperation;
    private List<FunctionArgument> functionArguments;
    private DTOcell selectedCell;
    private String generatedString;
    private boolean confirmed = false;
    MainSheetController mainController;
    SimpleStringProperty originalCellValueProperty;
    SimpleStringProperty lastUpdateVersionCellProperty;

    public UpdateCellFormat(DTOcell dtoCell, String coord, MainSheetController mainController,
                            SimpleStringProperty originalCellValueProperty, SimpleStringProperty lastUpdateVersionCellProperty) {
        this.selectedCell = dtoCell;
        this.coord = coord;
        this.mainController = mainController;
        this.originalCellValueProperty = originalCellValueProperty;
        this.lastUpdateVersionCellProperty = lastUpdateVersionCellProperty;
    }

    public void display() {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Update Value");
        window.setWidth(500);
        window.setHeight(400);

        Label cellIdLabel = createLabel("Cell ID: ", coord);
        Label originalValueLabel = createLabel("Original Value: ", selectedCell != null ? selectedCell.getOriginalValue() : "empty cell");
        Label effectiveValueLabel = createLabel(
                "Effective Value: ",
                (selectedCell != null && selectedCell.getEffectiveValue() != null)
                        ? selectedCell.getEffectiveValue()
                        : "empty cell"
        );

        ComboBox<String> inputTypeComboBox = new ComboBox<>();
        inputTypeComboBox.getItems().addAll("Number", "Text", "Function");
        inputTypeComboBox.setValue("Number");

        ScrollPane scrollPane = new ScrollPane();

        VBox dynamicContentArea = new VBox(10);
        dynamicContentArea.setPadding(new Insets(10));
        dynamicContentArea.getStyleClass().add("vbox");

        updateDynamicContent(dynamicContentArea, inputTypeComboBox.getValue());

        inputTypeComboBox.setOnAction(e -> updateDynamicContent(dynamicContentArea, inputTypeComboBox.getValue()));

        Button submitButton = new Button("Submit");
        submitButton.getStyleClass().add("button");
        submitButton.setOnAction(e -> {
            confirmed = true;  // User confirmed the action
            handleSubmit(inputTypeComboBox.getValue(), dynamicContentArea, window);
            mainController.updateCellValue(coord, generatedString);
        });

        // Add a Cancel button
        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(e -> {
            confirmed = false;  // User canceled the action
            window.close();
        });

        HBox buttonBox = new HBox(10, submitButton, cancelButton);
        buttonBox.setPadding(new Insets(10));
        buttonBox.setSpacing(10);

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));
        layout.getStyleClass().add("window");
        layout.getChildren().addAll(cellIdLabel,originalValueLabel, effectiveValueLabel, new Label("Choose input type:"), inputTypeComboBox, dynamicContentArea, buttonBox);

        scrollPane.setContent(layout);
        Scene scene = new Scene(scrollPane);
        mainController.setTheme(scene);
        window.setScene(scene);

        window.setOnCloseRequest(e -> {
            confirmed = false;
        });

        window.showAndWait();
    }
//
//    private void updateCellValue(String cellID, String newValue) {
//        // Parse the cell ID (e.g., "A1", "B2") to get row and column coordinates
//        Coordinate coordinate = mainController.getEngine().checkAndConvertInputToCoordinate(cellID);
//
//        // Call the engine's EditCell function to update the cell value
//        mainController.getEngine().EditCell(coordinate, newValue);
//
//        // Refresh the sheet display
//        DTOsheet dtoSheet = mainController.getEngine().createDTOSheetForDisplay(mainController.getEngine().getSheet());
//        mainController.setSheet(dtoSheet, true);
//
//        originalCellValueProperty.set(newValue);
//        lastUpdateVersionCellProperty.set(String.valueOf(dtoSheet.getCell(coordinate).getVersion()));
//    }

    public String getGeneratedString() {
        return generatedString;
    }


    private Label createLabel(String prefix, String value) {
        return new Label(prefix + value);
    }

    private void updateDynamicContent(VBox dynamicContentArea, String inputType) {
        dynamicContentArea.getChildren().clear();

        switch (inputType) {
            case "Number":
                TextField numberField = new TextField();
                numberField.setPromptText("Enter a number");

                numberField.setTextFormatter(new TextFormatter<>(change -> {
                    String newText = change.getControlNewText();
                    if (newText.matches("\\d*\\.?\\d*")) {
                        return change;
                    }
                    return null; // Reject invalid input
                }));

                dynamicContentArea.getChildren().add(numberField);
                break;
            case "Text":
                TextField textField = new TextField();
                textField.setPromptText("Enter text");

                textField.setTextFormatter(new TextFormatter<>(change -> {
                    String newText = change.getControlNewText();
                    if (newText.isEmpty() || newText.matches(".*[a-zA-Z].*") || !newText.matches("\\d*")) {
                        return change;
                    }
                    return null;
                }));

                dynamicContentArea.getChildren().add(textField);
                break;
            case "Function":
                ComboBox<String> functionChoiceBox = new ComboBox<>();
                functionChoiceBox.getItems().addAll(Arrays.stream(Operation.values()).map(Operation::name).toList());
                functionChoiceBox.setPromptText("Operation");

                VBox functionArgumentsContainer = new VBox(5);
                functionChoiceBox.setOnAction(e -> updateFunctionArgumentsContainer(functionArgumentsContainer, functionChoiceBox.getValue()));

                dynamicContentArea.getChildren().addAll(functionChoiceBox, functionArgumentsContainer);
                break;
        }
    }

    private Node createFunctionArgumentComponent(String promptText, String operation) {
        HBox argumentBox = new HBox(5);

        ComboBox<String> argumentTypeComboBox = new ComboBox<>();
        argumentTypeComboBox.getItems().addAll("Number", "Text", "Function");
        argumentTypeComboBox.setValue("Number");

        TextField argumentField = new TextField();
        argumentField.setPromptText(promptText);

        argumentBox.getChildren().add(argumentTypeComboBox);
        argumentBox.getChildren().add(argumentField);

        argumentTypeComboBox.setOnAction(e -> updateArgumentBox(argumentBox, argumentTypeComboBox, argumentField));


        return argumentBox;
    }

    private void updateFunctionArgumentsContainer(VBox functionArgumentsContainer, String functionName) {
        functionArgumentsContainer.getChildren().clear();
        selectedOperation = Operation.valueOf(functionName);
        if (functionName != null) {
            Operation operation = Operation.valueOf(functionName);
            for (int i = 0; i < operation.getNumArgs(); i++) {
                functionArgumentsContainer.getChildren().add(createFunctionArgumentComponent("Argument " + (i + 1), functionName));
            }
        }
    }

    private void updateArgumentBox(HBox argumentBox, ComboBox<String> argumentTypeComboBox, TextField argumentField) {
        String selectedType = argumentTypeComboBox.getValue();

        argumentBox.getChildren().clear();
        argumentBox.getChildren().add(argumentTypeComboBox);

        if ("Function".equals(selectedType)){
            ComboBox<String> nestedFunctionChoiceBox = new ComboBox<>();
            nestedFunctionChoiceBox.getItems().addAll(Arrays.stream(Operation.values()).map(Operation::name).toList());
            nestedFunctionChoiceBox.setValue(Operation.values()[0].name());

            VBox nestedFunctionContainer = new VBox(5);
            nestedFunctionChoiceBox.setOnAction(e -> updateFunctionArgumentsContainer(nestedFunctionContainer, nestedFunctionChoiceBox.getValue()));
            argumentBox.getChildren().addAll(nestedFunctionChoiceBox, nestedFunctionContainer);
        } else {
            argumentBox.getChildren().add(argumentField);
        }
    }

    private FunctionArgument createFunctionArgument(HBox argumentBox) {
        ComboBox<String> argumentTypeComboBox = (ComboBox<String>) argumentBox.getChildren().get(0);
        String argumentType = argumentTypeComboBox.getValue();
        String argumentValue;
        if ("Function".equals(argumentType)) {
            ComboBox<String> nestedFunctionChoiceBox = (ComboBox<String>) argumentBox.getChildren().get(1);
            Operation nestedOperation = Operation.valueOf(nestedFunctionChoiceBox.getValue());
            List<FunctionArgument> nestedArgs = new ArrayList<>();

            VBox nestedFunctionArgumentsContainer = (VBox) argumentBox.getChildren().get(2);
            for (var nestedChild : nestedFunctionArgumentsContainer.getChildren()) {
                if (nestedChild instanceof HBox) {
                    nestedArgs.add(createFunctionArgument((HBox) nestedChild));
                }
            }
            return new FunctionArgument(nestedOperation, nestedArgs);
        }  else {
            TextField argumentField = (TextField) argumentBox.getChildren().get(1);
            argumentValue = argumentField.getText();
        }
        return new FunctionArgument(argumentValue);
    }

    private String formatNonFunctionArgument(String value) {
        return value;
    }

    private void handleSubmit(String inputType, VBox dynamicContentArea, Stage window) {
        this.inputType = inputType;
        generatedString = "";

        if ("Function".equals(inputType)) {
            ComboBox<String> functionChoiceBox = (ComboBox<String>) dynamicContentArea.getChildren().get(0);
            selectedOperation = Operation.valueOf(functionChoiceBox.getValue());

            functionArguments = new ArrayList<>();
            VBox functionArgumentsContainer = (VBox) dynamicContentArea.getChildren().get(1);
            for (Node child : functionArgumentsContainer.getChildren()) {
                if (child instanceof HBox) {
                    functionArguments.add(createFunctionArgument((HBox) child));
                }
            }

            if (!functionArguments.isEmpty()) {
                generatedString = formatOperation(selectedOperation, functionArguments);
            }

            System.out.println("Formatted String: " + generatedString);
        } else {
            selectedOperation = null;
            functionArguments = null;
            TextField inputField = (TextField) dynamicContentArea.getChildren().get(0);
            String inputValue = inputField.getText();

            if ("Number".equals(inputType)) {
                try {
                    Double.parseDouble(inputValue);
                    generatedString = inputValue;
                } catch (NumberFormatException e) {
                    ShowAlert.showAlert("Invalid Input", "Please enter a valid number.", "The value entered is not a valid number.", Alert.AlertType.ERROR);
                    return;
                }
            } else {
                generatedString = inputValue;
            }
        }
        window.close();
    }

    private String formatOperation(Operation operation, List<FunctionArgument> functionArguments) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append(operation.name());
        sb.append(",");
        sb.append(generateFormattedString(functionArguments));
        sb.append("}");
        return sb.toString();
    }

    private String generateFormattedString(List<FunctionArgument> functionArguments) {
        if (functionArguments == null || functionArguments.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (FunctionArgument argument : functionArguments) {
            sb.append(formatArgument(argument));
            sb.append(",");
        }

        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }

        return sb.toString();
    }

    private String formatArgument(FunctionArgument argument) {
        if (argument.isFunction()) {
            StringBuilder sb = new StringBuilder();

            sb.append("{");
            sb.append(argument.getOperation().name());
            List<FunctionArgument> nestedArgs = argument.getNestedArguments();
            if (nestedArgs != null && !nestedArgs.isEmpty()) {
                sb.append(",");
                sb.append(generateFormattedString(nestedArgs));
            }
            sb.append("}");

            return sb.toString();
        } else {
            return formatNonFunctionArgument(argument.getValue());
        }
    }

}