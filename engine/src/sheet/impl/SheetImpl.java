package sheet.impl;

import sheet.api.CellType;
import sheet.api.EffectiveValue;
import sheet.api.Sheet;
import sheet.cell.api.Cell;
import sheet.cell.impl.CellImpl;
import sheet.coordinate.api.Coordinate;
import sheet.coordinate.impl.CoordinateFactory;
import sheet.coordinate.impl.CoordinateImpl;
import sheet.layout.api.Layout;
import sheet.range.Range;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SheetImpl implements Sheet, Serializable {

    private Map<Coordinate, Cell> activeCells;
    private Layout layout;
    private String name;
    private int version = 1;
    private int numberCellsThatHaveChanged;
    private List<Cell> cellsThatHaveChanged;
    private Map<String, Range> stringToRange;

    public SheetImpl(){
        this.activeCells = new HashMap<>();
        this.stringToRange = new HashMap<>();
    }

    @Override
    public void setLayout(Layout layout) {
        this.layout = layout;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int getVersion() {
        return version;
    }

    @Override
    public String getName(){
        return name;
    }

    @Override
    public Layout getLayout() {
        return layout;
    }

    @Override
    public Cell getCell(int row, int column) {
        return activeCells.get(CoordinateFactory.createCoordinate(row, column));
    }

    @Override
    public Cell getCell(Coordinate coordinate) { return this.activeCells.get(coordinate); }

    @Override
    public Map<Coordinate, Cell> getActiveCells() {
        return activeCells;
    }

    @Override
    public int getNumberCellsThatHaveChanged() { return numberCellsThatHaveChanged; }

    @Override
    public List<Cell> getCellsThatHaveChanged(){
        return cellsThatHaveChanged;
    }


    @Override
    public void setCell(int row, int column, String value) {
        if(row > layout.getRows())
            throw new IndexOutOfBoundsException("Row " + row + " out of bounds");
        if(column > layout.getColumns())
            throw new IndexOutOfBoundsException("Column " + CoordinateImpl.convertNumberToAlphabetString(column) + " out of bounds");

        Coordinate coordinate = CoordinateFactory.createCoordinate(row, column);
        Cell cell = activeCells.get(coordinate);
        cell.setCellOriginalValue(value);
        cell.calculateEffectiveValue();
        updateInfluenceAndDepends();
        numberCellsThatHaveChanged++;
    }

    @Override
    public Sheet updateCellValueAndCalculate(int row, int column, String value) {
        numberCellsThatHaveChanged = 0;
        Coordinate coordinate = CoordinateFactory.createCoordinate(row, column);
        SheetImpl newSheetVersion = copySheet();

        Cell newCell = new CellImpl(row, column, value, newSheetVersion.getVersion() + 1, newSheetVersion);
        newSheetVersion.activeCells.put(coordinate, newCell);

        try {
            newSheetVersion.updateInfluenceAndDepends();
            List<Cell> orderedCells = newSheetVersion
                    .orderCellsForCalculation();
            cellsThatHaveChanged = orderedCells
                                    .stream()
                                    .filter(Cell::calculateEffectiveValue)
                                    .collect(Collectors.toList());


            // successful calculation. update sheet and relevant cells version
            newSheetVersion.IncreaseVersion();
            cellsThatHaveChanged.forEach(cell -> cell.updateVersion(newSheetVersion.getVersion()));
            newSheetVersion.numberCellsThatHaveChanged = cellsThatHaveChanged.size();
            return newSheetVersion;
        }
        catch (Exception e) {
            // deal with the runtime error that was discovered as part of invocation
            throw new RuntimeException(e.getMessage());
        }
    }

    private List<Cell> orderCellsForCalculation() {
        // Initialize the in-degree map and adjacency list for the dependency graph
        Map<Cell, Integer> inDegree = new HashMap<>();
        Map<Cell, List<Cell>> adjList = new HashMap<>();

        // Initialize in-degree and adjacency list for all active cells
        for (Cell cell : activeCells.values()) {
            inDegree.put(cell, 0); // Start with 0 in-degree for all cells
            adjList.put(cell, new ArrayList<>()); // Initialize adjacency list
        }

        // Build the graph by populating adjacency list and in-degree map
        for (Cell cell : activeCells.values()) {
            List<Coordinate> dependencies = cell.getDependsOn();

            for (Coordinate coord : dependencies) {
                Cell dependencyCell = activeCells.get(coord);
                if (dependencyCell != null) {
                    adjList.get(dependencyCell).add(cell); // dependencyCell -> cell
                    inDegree.put(cell, inDegree.get(cell) + 1); // Increment in-degree of cell
                }
            }
        }

        // Initialize a queue for cells with no dependencies (in-degree 0)
        Queue<Cell> queue = new LinkedList<>();
        for (Map.Entry<Cell, Integer> entry : inDegree.entrySet()) {
            if (entry.getValue() == 0) {
                queue.add(entry.getKey());
            }
        }

        // Perform topological sorting
        List<Cell> sortedCells = new ArrayList<>();
        while (!queue.isEmpty()) {
            Cell currentCell = queue.poll();
            sortedCells.add(currentCell);

            // Reduce in-degree of all cells that depend on the current cell
            for (Cell dependentCell : adjList.get(currentCell)) {
                inDegree.put(dependentCell, inDegree.get(dependentCell) - 1);
                if (inDegree.get(dependentCell) == 0) {
                    queue.add(dependentCell);
                }
            }
        }

        // Check if a valid topological order exists (i.e., no cycles)
        if (sortedCells.size() != activeCells.size()) {
            throw new RuntimeException("Circular dependency detected in cells - a cell can not reference itself");
        }

        return sortedCells;
}

    @Override
    public SheetImpl copySheet() {
        Boolean success = false;

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(this);
            oos.close();

            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
            success = true;
            return (SheetImpl) ois.readObject();
        }
        catch (Exception e) {
            // deal with the runtime error that was discovered as part of invocation
            throw new RuntimeException("Copy sheet failed.");
        } finally {
            if (!success) {
                // If an error occurred, return the original sheet
                return this;
            }
        }
    }

//    private SheetImpl newCopySheet() {
//
//    }

    @Override
    public void IncreaseVersion () {
        version++;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SheetImpl sheet = (SheetImpl) o;
        return version == sheet.version && Objects.equals(activeCells, sheet.activeCells) && Objects.equals(layout, sheet.layout) && Objects.equals(name, sheet.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(activeCells, layout, name, version);
    }

    private void updateInfluenceAndDepends(){
        List<String> refCells;
        List<String> sumCells;
        List<String> avgCells;

        for (Cell cell : this.activeCells.values()) {
            cell.getInfluencingOn().clear();
            cell.getDependsOn().clear();
        }
        for (Cell cell : this.activeCells.values()) {
            refCells = extractRefCells(cell.getOriginalValue());
            sumCells = extractSumCells(cell.getOriginalValue());
            avgCells = extractAverageCells(cell.getOriginalValue());
            for(String refCell : refCells) {
                Coordinate coordinate = CoordinateFactory.from(refCell);
                if(getCell(coordinate) != null)
                {
                    CoordinateFactory.isValidCoordinate(coordinate, this.layout);
                    cell.getDependsOn().add(coordinate);
                    this.getCell(coordinate).getInfluencingOn().add(cell.getCoordinate());
                }
            }
            for(String sumCell : sumCells)
            {
                Range range = stringToRange.get(sumCell);
                if(range != null)
                {
                    for (Coordinate coordinate : range.getCells()) {
                        CoordinateFactory.isValidCoordinate(coordinate, this.layout);
                        cell.getDependsOn().add(coordinate);
                        this.getCell(coordinate).getInfluencingOn().add(cell.getCoordinate());
                    }
                }
            }
            for(String sumCell : avgCells)
            {
                Range range = stringToRange.get(sumCell);
                if(range != null)
                {
                    for (Coordinate coordinate : range.getCells()) {
                        CoordinateFactory.isValidCoordinate(coordinate, this.layout);
                        cell.getDependsOn().add(coordinate);
                        this.getCell(coordinate).getInfluencingOn().add(cell.getCoordinate());
                    }
                }
            }
        }
    }

    private List<String> extractRefCells(String input) {
        Set<String> refCellsSet = new HashSet<>();

        // Regular expression to match "REF" (case-insensitive) followed by a cell reference with unlimited letters
        Pattern pattern = Pattern.compile("\\bREF\\s*,\\s*([A-Z]+[0-9]+)\\b", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(input);

        // Find all matches and add the cell references to the set (which avoids duplicates)
        while (matcher.find()) {
            refCellsSet.add(matcher.group(1));
        }

        // Convert the set back to a list before returning (if you need a list)
        return new ArrayList<>(refCellsSet);
    }

    private List<String> extractSumCells(String input) {
    Set<String> sumStringsSet = new HashSet<>();

    // Regular expression to match "SUM" (case-insensitive) followed by any string inside curly braces
    Pattern pattern = Pattern.compile("\\{\\s*SUM\\s*,\\s*([^\\}]+)\\s*\\}", Pattern.CASE_INSENSITIVE);
    Matcher matcher = pattern.matcher(input);

    // Find all matches and add the extracted strings to the set (which avoids duplicates)
    while (matcher.find()) {
        sumStringsSet.add(matcher.group(1).trim());  // Trim to remove extra spaces around the string
    }

    // Convert the set back to a list before returning (if you need a list)
    return new ArrayList<>(sumStringsSet);
    }

    private List<String> extractAverageCells(String input) {
        Set<String> averageStringsSet = new HashSet<>();

        // Regular expression to match "SUM" (case-insensitive) followed by any string inside curly braces
        Pattern pattern = Pattern.compile("\\{\\s*AVERAGE\\s*,\\s*([^\\}]+)\\s*\\}", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(input);

        // Find all matches and add the extracted strings to the set (which avoids duplicates)
        while (matcher.find()) {
            averageStringsSet.add(matcher.group(1).trim());  // Trim to remove extra spaces around the string
        }

        // Convert the set back to a list before returning (if you need a list)
        return new ArrayList<>(averageStringsSet);
    }

    @Override
    public void setEmptyCell(int row, int column){
        Coordinate coordinate = CoordinateFactory.createCoordinate(row, column);
        CellImpl newCell = new CellImpl(coordinate.getRow(), coordinate.getColumn(), "", 1, this);
        newCell.calculateEffectiveValue();
        activeCells.put(coordinate, newCell);
        updateInfluenceAndDepends();
    }

    @Override
    public Map<String, Range> getStringToRange() {
        return stringToRange;
    }

    @Override
    public void addRange(String name, String rangeStr) {
        if(stringToRange.containsKey(name)) {
            throw new RuntimeException("The name of the range " + name + " already exists and can not be used again");
        }
        Range range = new Range(name,layout);
        range.parseRange(rangeStr);
        stringToRange.put(name,range);
    }

    @Override
    public void removeRange(String name) {
        //check if there is a usage in the range in some function
        stringToRange.remove(name);
    }

    @Override
    public List<Double> getNumericalValuesFromRange(String range) throws IllegalArgumentException {
        // Create a new Range object and parse the provided range string
        Range rangeObj = new Range("temp", layout);
        rangeObj.parseRange(range); // Parses the input range (e.g., A1..A5)

        List<Double> numericalValues = new ArrayList<>();

        // Iterate over the coordinates within the parsed range
        for (Coordinate coordinate : rangeObj.getCells()) {
            // Retrieve the cell at the given coordinate
            Cell cell = getCell(coordinate);

            // Check if the cell contains a numerical value
            if (cell != null && CellType.isNumeric(cell.getEffectiveValue().getValue().toString()))
                numericalValues.add((Double) cell.getEffectiveValue().getValue());
            else
                throw new IllegalArgumentException("One or more cells in the range " + rangeObj.toString()+" do not contain valid numerical values.");
        }

        return numericalValues;
    }

    @Override
    public List<String> createListOfValuesForFilter(String column) {
        List<String> values = new ArrayList<>();
        int col = CoordinateImpl.convertStringColumnToNumber(column);
        for(Cell cell : activeCells.values()) {
            if (cell.getCoordinate().getColumn() == col) {
                if (!cell.getEffectiveValue().getValue().equals("")) {
                    values.add(cell.getEffectiveValue().getValue().toString());
                }
            }
        }
        return values;
    }

    @Override
    public void copyRow(int selectedRow, int startColumn, int endColumn, int startRow, int endRow, Sheet originalSheet) {
        //List<Cell> cells = getCellsByRow(selectedRow, startColumn, endColumn);
        for(int col = startColumn; col <= endColumn; col++) {
            for (int row = startRow; row <= endRow; row++) {
                Coordinate coordinate = CoordinateFactory.createCoordinate(row, col);
                String value = originalSheet.getActiveCells().get(coordinate).getEffectiveValue().getValue().toString();
                Cell newCell = new CellImpl(selectedRow,col, value, getVersion(), this);
                EffectiveValue effectiveValue = new EffectiveValueImpl(CellType.STRING, value);
                newCell.setEffectiveValueForDisplay(effectiveValue);
                activeCells.put(coordinate, newCell);
            }
        }
    }
}
