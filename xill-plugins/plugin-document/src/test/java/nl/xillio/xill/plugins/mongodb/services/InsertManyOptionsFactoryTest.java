package nl.xillio.xill.plugins.mongodb.services;

import com.mongodb.client.model.InsertManyOptions;
import nl.xillio.xill.TestUtils;
import nl.xillio.xill.api.components.MetaExpression;
import org.testng.annotations.Test;

import java.util.LinkedHashMap;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class InsertManyOptionsFactoryTest extends TestUtils {
    @Test
    public void testBuildOptions () {
        MongoConverter mongoConverter = new MongoConverter(null);
        InsertManyOptionsFactory insertManyOptionsFactory = new InsertManyOptionsFactory(mongoConverter);
        LinkedHashMap<String, MetaExpression> object = new LinkedHashMap<>();

        InsertManyOptions options = insertManyOptionsFactory.build(fromValue(object));

        assertTrue(options.isOrdered());
        object.put("ordered", FALSE);
        options = insertManyOptionsFactory.build(fromValue(object));
        assertFalse(options.isOrdered());
    }
}
