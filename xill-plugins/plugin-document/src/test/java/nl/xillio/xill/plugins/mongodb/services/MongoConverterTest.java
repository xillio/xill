package nl.xillio.xill.plugins.mongodb.services;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.services.json.JacksonParser;
import nl.xillio.xill.services.json.JsonException;
import nl.xillio.xill.services.json.JsonParser;
import org.bson.Document;
import org.testng.annotations.Test;

import java.util.Map;

import static org.testng.Assert.*;

public class MongoConverterTest {
    private JsonParser parser = new JacksonParser(false);

    @Test
    public void testParseEmptyMetaExpression() throws JsonException {
        MongoConverter mongoConverter = new MongoConverter();
        MetaExpression expression = parse("{}");
        Document document = mongoConverter.parse(expression);

        assertEquals(document.size(), 0);
    }

    @Test
    public void testParseMetaExpression() throws JsonException {
        MongoConverter mongoConverter = new MongoConverter();
        MetaExpression expression = parse("{ \"$set\": { \"test\" : 2 } }");
        Document document = mongoConverter.parse(expression);

        assertEquals(document.size(), 1);
        assertTrue(document.containsKey("$set"));
        assertEquals(document.get("$set").toString(), "{test=2}");
    }

    @Test
    public void testParseBsonDocument() throws JsonException {
        MongoConverter mongoConverter = new MongoConverter();
        Document document = new Document("$set", new Document("test", 2));
        MetaExpression expression = mongoConverter.parse(document);

        assertEquals(expression.toString(parser), "{\"$set\":{\"test\":2}}");
    }

    private MetaExpression parse(String json) throws JsonException {
        Map<?, ?> value = parser.fromJson(json, Map.class);
        return MetaExpression.parseObject(value);
    }
}
