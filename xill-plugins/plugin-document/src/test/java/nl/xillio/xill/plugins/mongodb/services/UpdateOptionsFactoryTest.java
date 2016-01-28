package nl.xillio.xill.plugins.mongodb.services;

import com.mongodb.client.model.UpdateOptions;
import nl.xillio.xill.TestUtils;
import nl.xillio.xill.api.components.MetaExpression;
import org.testng.annotations.Test;

import java.util.LinkedHashMap;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class UpdateOptionsFactoryTest extends TestUtils {
    @Test
    public void testBuildOptions() {
        MongoConverter mongoConverter = new MongoConverter(null);
        UpdateOptionsFactory UpdateOptionsFactory = new UpdateOptionsFactory(mongoConverter);
        LinkedHashMap<String, MetaExpression> object = new LinkedHashMap<>();

        UpdateOptions options = UpdateOptionsFactory.build(fromValue(object));

        assertFalse(options.isUpsert());
        object.put("upsert", TRUE);
        options = UpdateOptionsFactory.build(fromValue(object));
        assertTrue(options.isUpsert());
    }
}
