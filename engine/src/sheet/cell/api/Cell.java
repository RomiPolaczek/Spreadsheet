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
    void updateVersion(int version);
    int getVersion();
    List<Coordinate> getDependsOn();
    List<Coordinate> getInfluencingOn();
    void setEffectiveValueForDisplay(EffectiveValue effectiveValue);
}