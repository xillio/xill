package nl.xillio.xill.plugins.document.services.xill;

import nl.xillio.xill.TestUtils;
import nl.xillio.xill.api.components.MetaExpression;
import org.bson.Document;
import org.testng.annotations.Test;

import java.util.LinkedHashMap;

import static org.testng.Assert.*;


public class UDMQueryBuilderTest extends TestUtils{

    /**
     * This is just a dumb test that will run i/o once.
     * @throws Exception
     */
    @Test
    public void testParseQuery() throws Exception {
        LinkedHashMap<String,MetaExpression> putList = new LinkedHashMap<>();
        putList.put("someKey", fromValue("someValue"));
        LinkedHashMap<String, MetaExpression> list = new LinkedHashMap<>();
        list.put("$set", fromValue(putList));

        MetaExpression input = fromValue(list);

        Document output = new UDMQueryBuilder().parseQuery(input);

        Document expected = new Document("$set", new Document("someKey", "someValue"));
        assertEquals(output, expected);
    }
}