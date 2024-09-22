package dto;

import sheet.coordinate.api.Coordinate;

import java.util.List;

public class DTOrange {
    private List<Coordinate> cells;
    private String name;

    public DTOrange (List<Coordinate> cells, String name) {
        this.cells = cells;
        this.name = name;
    }

    public List<Coordinate> getCells() {return cells; }

    public String getName() {return name; }
}
