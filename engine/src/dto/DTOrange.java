package dto;

import sheet.coordinate.api.Coordinate;

import java.io.Serializable;
import java.util.List;

public class DTOrange implements Serializable {
    private List<Coordinate> cells;
    private String name;

    public DTOrange (List<Coordinate> cells, String name) {
        this.cells = cells;
        this.name = name;
    }

    public List<Coordinate> getCells() {return cells; }

    public String getName() {return name; }
}
