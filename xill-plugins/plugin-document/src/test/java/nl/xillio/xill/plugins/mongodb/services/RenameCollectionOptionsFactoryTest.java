package nl.xillio.xill.plugins.mongodb.services;

import com.mongodb.client.model.RenameCollectionOptions;
import nl.xillio.xill.TestUtils;
import nl.xillio.xill.api.components.MetaExpression;
import org.testng.annotations.Test;

import java.util.LinkedHashMap;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class RenameCollectionOptionsFactoryTest extends TestUtils {
    @Test
    public void testBuildOptions () {
        MongoConverter mongoConverter = new MongoConverter(null);
        RenameCollectionOptionsFactory RenameCollectionOptionsFactory = new RenameCollectionOptionsFactory(mongoConverter);
        LinkedHashMap<String, MetaExpression> object = new LinkedHashMap<>();

        RenameCollectionOptions options = RenameCollectionOptionsFactory.build(fromValue(object));

        assertFalse(options.isDropTarget());
        object.put("dropTarget", TRUE);
        options = RenameCollectionOptionsFactory.build(fromValue(object));
        assertTrue(options.isDropTarget());
    }
}
