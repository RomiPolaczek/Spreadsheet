package dto;

import java.util.List;

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
}
