package impl;

import SingleSheetManager.api.SingleSheetManager;
import SingleSheetManager.impl.SingleSheetManagerImpl;
import SingleSheetManager.impl.VersionManager;
import api.Engine;
import dto.DTOcell;
import dto.DTOlayout;
import dto.DTOrange;
import dto.DTOsheet;
import expression.parser.Operation;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import sheet.api.EffectiveValue;
import sheet.api.Sheet;
import sheet.cell.api.Cell;
import sheet.coordinate.api.Coordinate;
import sheet.coordinate.impl.CoordinateFactory;
import sheet.coordinate.impl.CoordinateImpl;
import sheet.impl.SheetImpl;
import sheet.layout.impl.LayoutImpl;
import sheet.range.Range;
import users.UserManager;
import xmlGenerated.STLCell;
import xmlGenerated.STLRange;
import xmlGenerated.STLSheet;

import java.io.*;
import java.util.*;

public class EngineImpl implements Engine, Serializable {
    private Map<String, SingleSheetManager> sheetNameToSheet;
    private UserManager userManager;

    public EngineImpl() {
        sheetNameToSheet = new HashMap<>();
    }

    public Map<String, SingleSheetManager> getSheetNameToSheet() {
        return sheetNameToSheet;
    }

    public void LoadFile(String fileName) throws Exception {
        SingleSheetManager singleSheetManager = new SingleSheetManagerImpl();
        singleSheetManager.LoadFile(fileName);
        String sheetName = singleSheetManager.getSheet().getName();

        if(sheetNameToSheet.containsKey(sheetName)){
            throw new RuntimeException("The sheet " + sheetName + " already exists");
        }

        sheetNameToSheet.put(sheetName, singleSheetManager);
    }

}