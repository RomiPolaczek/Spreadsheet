package sheet.cell.api;

import sheet.api.EffectiveValue;
import sheet.coordinate.api.Coordinate;
import java.util.List;

public interface Cell {
    Coordinate getCoordinate();
    String getOriginalValue();
    void setCellOriginalValue(String value);
    EffectiveValue getEffectiveValue();
    boolean calculateEffectiveValue();
    int getVersion();
    List<Cell> getDependsOn();
    List<Cell> getInfluencingOn();
}