package sheet.layout.api;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import sheet.coordinate.api.Coordinate;
import sheet.coordinate.impl.CoordinateImpl;
import sheet.layout.impl.LayoutImpl;

import java.lang.reflect.Type;

public class LayoutDeserializer implements JsonDeserializer<Layout> {

    @Override
    public Layout deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json == null || !json.isJsonObject()) {
            throw new JsonParseException("Invalid JSON for Layout");
        }

        try {
            // Extract the fields from the JSON
            int rowsHeightUnits = json.getAsJsonObject().get("rowsHeightUnits").getAsInt();
            int columnsWidthUnits = json.getAsJsonObject().get("columnsWidthUnits").getAsInt();
            int rows = json.getAsJsonObject().get("rows").getAsInt();
            int columns = json.getAsJsonObject().get("columns").getAsInt();

            // Create and return the LayoutImpl object
            LayoutImpl layout = new LayoutImpl();
            layout.setRowsHeightUnits(rowsHeightUnits);
            layout.setColumnsWidthUnits(columnsWidthUnits);
            layout.setRows(rows);
            layout.setColumns(columns);

            return layout;
        } catch (Exception e) {
            throw new JsonParseException("Error deserializing Layout", e);
        }
    }

}