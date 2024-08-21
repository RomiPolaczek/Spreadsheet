package dto;

import sheet.coordinate.CoordinateFactory;

import java.util.List;
import java.util.Map;

public class DTOsheet {
    private String name;
    private int version;
    private List<DTOcell> cells;
    private DTOlayout layout;

    public DTOsheet(String name, int version, List<DTOcell> cells, DTOlayout layout) {
        this.name = name;
        this.version = version;
        this.cells = cells;
        this.layout = layout;
    }

    public String getName() {
        return name;
    }

    public int getVersion() {
        return version;
    }

    public List<DTOcell> getCells() {
        return cells;
    }

    public DTOlayout getLayout() {
        return  layout;
    }

    //public DTOcell getCell(int row, int col) {
     //   return cells.get(CoordinateFactory.createCoordinate(row, col));
   // }
}
