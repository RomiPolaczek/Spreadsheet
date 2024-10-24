package api;

import dto.DTOsheet;
import sheet.api.EffectiveValue;
import sheet.api.Sheet;
import sheet.coordinate.api.Coordinate;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface Engine {
    void LoadFile(String fileName) throws Exception;
}
