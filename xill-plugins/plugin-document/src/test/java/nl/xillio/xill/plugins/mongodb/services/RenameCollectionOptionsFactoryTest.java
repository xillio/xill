package nl.xillio.xill.plugins.mongodb.services;

import com.mongodb.client.model.RenameCollectionOptions;
import nl.xillio.xill.TestUtils;
import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class RenameCollectionOptionsFactoryTest extends TestUtils {
    @Test
    public void testBuildOptions() {
        MongoConverter mongoConverter = new MongoConverter(null);
        RenameCollectionOptionsFactory RenameCollectionOptionsFactory = new RenameCollectionOptionsFactory(mongoConverter);

        RenameCollectionOptions options = RenameCollectionOptionsFactory.build(FALSE);
        assertFalse(options.isDropTarget());

        options = RenameCollectionOptionsFactory.build(TRUE);
        assertTrue(options.isDropTarget());
    }
}
