package dto;

import sheet.coordinate.api.Coordinate;
import sheet.coordinate.impl.CoordinateFactory;
import sheet.range.Range;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DTOsheet implements Serializable {
    private String name;
    private int version;
    private Map<Coordinate,DTOcell> cells;
    private DTOlayout layout;
    private Map<String, DTOrange> stringToRange;

    public DTOsheet(String name, int version, Map<Coordinate ,DTOcell> cells, DTOlayout layout, Map<String, DTOrange> ranges) {
        this.name = name;
        this.version = version;
        this.cells = cells;
        this.layout = layout;
        this.stringToRange = ranges;
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

    public DTOcell getCell(String cellID) { return cells.get(CoordinateFactory.from(cellID)); }

    public List<String> getExistingRangeNames() {
         return stringToRange.keySet().stream().collect(Collectors.toList());
    }

    public List<String> getRangeCellsList(String name) {
        DTOrange range = stringToRange.get(name);
        List<String> rangeCellsList = new ArrayList<>();
        for(Coordinate coordinate: range.getCells())
        {
            rangeCellsList.add(coordinate.toString());
        }
        return rangeCellsList;
    }
}
