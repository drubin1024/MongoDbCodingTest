package drubin.mongodb;

import java.util.TreeMap;
import java.util.Map;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for the flattener utility.
 */
public class JsonFlattenerTest {

    // From github. Its unfortunate Java does not allow multi-line String literals sub version 9
    public static final String EXAMPLE_INPUT = "{\n" +
            "\"a\": 1,\n" +
            "\"b\": true,\n" +
            "\"c\": {\n" +
            "\"d\": 3,\n" +
            "\"e\": \"test\"\n" +
            "}\n" +
            "}";

    // Also from gitgub
    public static final String EXAMPLE_OUTPUT = "{\n" +
            "  \"a\": 1,\n" +
            "  \"b\": true,\n" +
            "  \"c.d\": 3,\n" +
            "  \"c.e\": \"test\"\n" +
            "}";

    @Test
    /**
     * Checks flattening was done correctly by analyzing the output JsonObject
     */
    public void testJsonFlattening() {
        // Build expected Map holding flattened keys and values. We use JsonPrimitive for our value type so we can
        // directly compare to what is returned by Gson and because it provides a type-agnostic way to do the comparison
        Map<String, JsonPrimitive> expected = new TreeMap<>();
        expected.put("a", new JsonPrimitive(1));
        expected.put("b", new JsonPrimitive(true));
        expected.put("c.d", new JsonPrimitive(3));
        expected.put("c.e", new JsonPrimitive("test"));

        // Flatten into JsonObject using our flattener
        JsonFlattener flattener = JsonFlattener.getDefaultFlattener();
        JsonObject result = flattener.flattenAsJsonObject(EXAMPLE_INPUT);

        // Check the results.
        // The output JsonObject should essentially be a 1-deep K=V map so first check the size is as expected
        Assert.assertEquals(expected.size(), result.entrySet().size());
        // Now check all the values of the output map.
        // Each entry should 1) be in our expected map 2) Be a primitive and 3) be of correct value and type
        for (Map.Entry<String, JsonElement> outputEntry : result.entrySet()) {
            Assert.assertTrue(expected.containsKey(outputEntry.getKey()));
            Assert.assertTrue(outputEntry.getValue() instanceof JsonPrimitive);
            // We rely on JsonPrimitive's comparator which handles the different types gracefully already.
            Assert.assertEquals(
                    expected.get(outputEntry.getKey()),
                    outputEntry.getValue()
            );
        }
    }

    @Test
    /**
     * Tests null, empty, etc, input. One could make these separate tests for more granularity.
     */
    public void testWeirdInput() {
        JsonFlattener flattener = JsonFlattener.getDefaultFlattener();
        assertFlattenFailure(flattener, null);
        assertFlattenFailure(flattener, "");
        assertFlattenFailure(flattener, "    ");
        Assert.assertEquals("{}", flattener.flattenAsJsonString("{}"));
    }

    /**
     * Attempts to flatten the given input String with the given flattener. Throws an Exception if the flattening
     * succeeds, otherwise does nothing.
     * @param flattener Flattener to use
     * @param input Input string to attempt to flatten
     */
    private void assertFlattenFailure(JsonFlattener flattener, String input) {
        boolean failed = false;
        try {
            flattener.flattenAsJsonString(input);
        } catch (Exception e) {
            failed = true;
        }
        Assert.assertTrue(failed);
    }


    @Test
    /**
     * This test checks the actual output String matches what is expected for the default output format.
     */
    public void testDefaultOutputFormat() {
        JsonFlattener flattener = JsonFlattener.getDefaultFlattener();
        String result = flattener.flattenAsJsonString(EXAMPLE_INPUT);
        Assert.assertEquals(EXAMPLE_OUTPUT, result);
    }

}
