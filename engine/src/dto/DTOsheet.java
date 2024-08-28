package dto;

import sheet.coordinate.api.Coordinate;
import sheet.coordinate.impl.CoordinateFactory;
import java.io.Serializable;
import java.util.Map;

public class DTOsheet implements Serializable {
    private String name;
    private int version;
    private Map<Coordinate,DTOcell> cells;
    private DTOlayout layout;

    public DTOsheet(String name, int version, Map<Coordinate ,DTOcell> cells, DTOlayout layout) {
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

    public Map<Coordinate ,DTOcell> getCells() {
        return cells;
    }

    public DTOlayout getLayout() {
        return  layout;
    }

    public DTOcell getCell(int row, int col) {
        return cells.get(CoordinateFactory.createCoordinate(row, col));
    }

    public DTOcell getCell(Coordinate coordinate) { return cells.get(coordinate); }
}
