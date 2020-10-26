package drubin.mongodb;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import sun.misc.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * Simple utility class which will flatten json but does not support Json arrays.
 *
 * Flattening a Json eliminates any nesting structure to generate a simple json K=V dictionary.
 * Keys are combined with the "." character in cases where flattening occurs to produce a single key representing the
 * entire hierarchy. Types are maintained during flattening.
 */
public class JsonFlattener {

    // String glued between pieces of the key's hierarchy when flattening.
    private static final String HIERARCHY_SEPARATOR = ".";

    /**
     * @return Returns a new JsonFlattener with default Gson reading capabilities and pretty output printing
     */
    public static JsonFlattener getDefaultFlattener() {
        Gson defaultWithPretty = new GsonBuilder().setPrettyPrinting().create();
        return new JsonFlattener(defaultWithPretty, defaultWithPretty);
    }

    // Used to read input Strings.
    private final Gson inputParser;

    // Used to format any output Strings
    private final Gson outputFormatter;

    /**
     * Creates a new JsonFlattener with the given Gsons for reading and writing Json strings
     * @param inputParser Gson used for reading input Json Strings
     * @param outputFormatter Gson used to format any output Strings
     */
    public JsonFlattener(Gson inputParser, Gson outputFormatter) {
        this.inputParser = inputParser;
        this.outputFormatter = outputFormatter;
    }

    /**
     * Flattens the a given Json String such that the returned output Json consists of flat K=V dictionary.
     * @param jsonString Input String to flatten
     * @return Flattened output String
     */
    public String flattenAsJsonString(String jsonString) {
        if (jsonString == null || jsonString.trim().isEmpty()) {
            throw new IllegalArgumentException("Can not flatten a null or empty String");
        }
        JsonObject output = flattenAsJsonObject(jsonString);
        return formatOutput(output);
    }

    /**
     * Flattens the given Json String such that the returned output consists of a flat K=V dictionary.
     * The output is returned as a JsonObject with keys combines across hierarchies using "." and the values being
     * JsonPrimitives holding leaf values from the given input
     * @param jsonString String to flatten
     * @return JsonObject holding the flattened equivalent
     */
    public JsonObject flattenAsJsonObject(String jsonString) {
        // We create an object to hold our flattened output
        JsonObject output = new JsonObject();

        JsonObject inputJsonObject = this.inputParser.fromJson(jsonString, JsonObject.class);
        traverseAndFlattenJsonObject(inputJsonObject, "", output);
        return output;
    }

    /**
     * Formats
     * @param output
     * @return
     */
    protected String formatOutput(JsonObject output) {
        return this.outputFormatter.toJson(output);
    }

    /**
     * Builds a new key for a flattened hierarchy
     * @param existing Existing key of our current Json node
     * @param toAppend Key of the child that we want to append
     * @return The new key
     */
    protected String buildKey(String existing, String toAppend) {
        if (existing != null && existing.length() > 0) {
            return existing + "." + toAppend;
        } else {
            return toAppend;
        }
    }

    /**
     * Traverses a JsonObject recursively to produce its flattened form in the given output object.
     *
     * The JsonObject essentially works like a tree node with its children stored in a Map where the map's keys
     * are the names of the children and the values are either other nodes (JsonObjects) or leaf values (JsonPrimitives)
     * @param obj Object to traverse and flatten.
     * @param prefix A key prefix to use when flattening keys.
     * @param output Output JsonObject to write the flattened data into
     */
    private void traverseAndFlattenJsonObject(JsonObject obj, String prefix, JsonObject output) {
        for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
            String childKey = buildKey(prefix, entry.getKey());
            handleJsonElement(childKey, entry.getValue(), output);
        }
    }

    /**
     * Acts upon a given K=V.
     *
     * For primitives, the key and value are copied to the output JsonObject.
     * For other JsonObjects, the object is traversed
     * @param key Key for our value
     * @param value JsonElement representing our value.
     * @param output JsonObject to write output into
     */
    protected void handleJsonElement(String key, JsonElement value, JsonObject output) {
        if (value.isJsonPrimitive()) {
            output.add(key, value);
        } else if (value.isJsonObject()) {
            traverseAndFlattenJsonObject(value.getAsJsonObject(), key, output);
        }
    }
}
