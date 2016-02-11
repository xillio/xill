package nl.xillio.xill.services.json;

import nl.xillio.xill.api.components.MetaExpression;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collections;

import static nl.xillio.xill.api.components.ExpressionBuilderHelper.fromValue;
import static org.testng.Assert.*;

public class JacksonParserTest {

    /**
     * Test if this json parser parses an integer to an integer and not a double.
     */
    @Test
    public void testFromJsonParsesIntegerToInteger() throws Exception {
        MetaExpression listWithInteger = fromValue(Collections.singletonList(fromValue(42)));
        JsonParser parser = new JacksonParser(false);

        String json = parser.toJson(listWithInteger);

        assertFalse(json.contains("."));

        ArrayList<?> result = parser.fromJson(json, ArrayList.class);

        assertEquals(result.size(), 1);
        assertEquals(result.get(0), 42);
        assertTrue(result.get(0) instanceof Integer);
    }

    @Test(expectedExceptions = {JsonException.class})
    public void testParserCircularReference() throws Exception {
        MetaExpression list = fromValue(new ArrayList<>());
        list.<ArrayList>getValue().add(list);

        JsonParser parser = new JacksonParser(false);
        parser.toJson(list);
    }
}
