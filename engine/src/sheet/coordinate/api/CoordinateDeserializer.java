package sheet.coordinate.api;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import sheet.coordinate.impl.CoordinateImpl;

import java.lang.reflect.Type;

public class CoordinateDeserializer implements JsonDeserializer<Coordinate> {
    @Override
    public Coordinate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json.isJsonPrimitive()) {  // Handle as a simple string
            String coordinateString = json.getAsString();
            return new CoordinateImpl(coordinateString);  // Assuming CoordinateImpl can parse from string
        }
        // Otherwise, handle as a normal JSON object (if your JSON has Coordinate as an object)
        return context.deserialize(json, CoordinateImpl.class);
    }
}