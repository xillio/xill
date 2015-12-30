package nl.xillio.xill.plugins.document.services.xill;

import nl.xillio.xill.TestUtils;
import nl.xillio.xill.api.components.MetaExpression;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.*;

@SuppressWarnings("unchecked")
public class UDMDocumentFactoryTest extends TestUtils{

    @Test
    public void testBuildStructure() throws Exception {
        MetaExpression versionBody = buildBody();
        String contentType = "Agenda";

        MetaExpression output = new UDMDocumentFactory().buildStructure(
                contentType,
                versionBody,
                fromValue(Arrays.asList(versionBody, versionBody))
        );

        assertEquals(get(output, "contenttype").getStringValue(), contentType);
        assertEquals(get(output, "target", "current"), versionBody);

        int counter = 0;
        for(MetaExpression version : (List<MetaExpression>)get(output, "target", "versions").getValue()) {
            assertEquals(version, versionBody);
            counter++;
        }
        assertEquals(counter, 2);

    }

    private MetaExpression get(MetaExpression output, String... path) {
        if(path.length == 0) {
            return output;
        }

        Map<String, MetaExpression> map = (Map<String, MetaExpression>) output.getValue();
        return get(map.get(path[0]), Arrays.copyOfRange(path,1,path.length));
    }

    public static MetaExpression buildBody() {
        LinkedHashMap<String, MetaExpression> doc = new LinkedHashMap<>();

        doc.put("version", fromValue(5425));

        LinkedHashMap<String, MetaExpression> decorator = new LinkedHashMap<>();
        decorator.put("field", fromValue(432));

        doc.put("decorator", fromValue(decorator));

        return fromValue(doc);
    }
}