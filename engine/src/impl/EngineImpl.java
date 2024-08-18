package impl;

import api.Engine;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import sheet.cell.impl.CellImpl;
import sheet.coordinate.CoordinateImpl;
import sheet.impl.SheetImpl;
import sheet.layout.api.Layout;
import sheet.layout.impl.LayoutImpl;
import xmlGenerated.STLCell;
import xmlGenerated.STLSheet;

import java.io.File;
import java.util.List;

public class EngineImpl implements Engine {

    private SheetImpl sheet;
    private STLSheet stlSheet;
    private File file;

    public void LoadFile(String fileName) throws Exception {
        setFile(fileName);
        fromXmlFileToObject();
        fromStlSheetToOurSheet();
    }

    private void setFile(String fileName) throws Exception { ///לבדוק אקספשין
        checkFileValidation(fileName);
    }

    public SheetImpl getSheet(){
        return sheet;
    }

    private void checkFileValidation(String fileName) throws Exception {
        file = new File(fileName);

        if (!file.exists()) {
            throw new Exception("File does not exist at path: " + fileName);
        }
        if (!file.canRead()) {
            throw new Exception("File cannot be read at path: " + fileName);
        }
        // Check if file has .xml extension
        if (!fileName.toLowerCase().endsWith(".xml")) {
            throw new Exception("The file does not have an XML extension.");
        }
    }

    private void fromXmlFileToObject() {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(STLSheet.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            stlSheet = (STLSheet) jaxbUnmarshaller.unmarshal(file);

        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    private void fromStlSheetToOurSheet() {
        sheet = new SheetImpl();

        LayoutImpl layout = new LayoutImpl();
        layout.setRowsHeightUnits(stlSheet.getSTLLayout().getSTLSize().getRowsHeightUnits());
        layout.setColumnsWidthUnits(stlSheet.getSTLLayout().getSTLSize().getColumnWidthUnits());
        layout.setRows(stlSheet.getSTLLayout().getRows());
        layout.setColumns(stlSheet.getSTLLayout().getColumns());
        sheet.setLayout(layout);

        sheet.setName(stlSheet.getName());

        for(STLCell stlCell : stlSheet.getSTLCells().getSTLCell())
        {
            int row = stlCell.getRow() - 1;
            int col = CoordinateImpl.convertStringColumnToNumber(stlCell.getColumn());
            sheet.setCell(row, col, stlCell.getSTLOriginalValue());
        }
    }
}
