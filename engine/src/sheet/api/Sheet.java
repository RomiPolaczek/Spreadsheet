package sheet.api;

import sheet.cell.api.Cell;
import sheet.coordinate.api.Coordinate;
import sheet.impl.SheetImpl;
import sheet.layout.api.Layout;

import java.util.Map;

public interface Sheet extends SheetReadActions, SheetUpdateActions {

}
