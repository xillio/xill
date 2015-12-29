package nl.xillio.xill.plugins.mongodb.services;

import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import nl.xillio.xill.TestUtils;
import nl.xillio.xill.api.components.MetaExpression;
import org.testng.annotations.Test;

import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;

import static org.testng.Assert.*;

public class FindOneAndUpdateOptionsFactoryTest extends TestUtils {
    @Test
    public void testBuildOptions () {
        MongoConverter mongoConverter = new MongoConverter(new ObjectIdSerializer());
        FindOneAndUpdateOptionsFactory findOneAndUpdateOptionsFactory = new FindOneAndUpdateOptionsFactory(mongoConverter);
        LinkedHashMap<String, MetaExpression> object = new LinkedHashMap<>();

        FindOneAndUpdateOptions options = findOneAndUpdateOptionsFactory.build(fromValue(object));
        LinkedHashMap<String, MetaExpression> projection = new LinkedHashMap<>();

        assertNull(options.getProjection());
        projection.put("_id", FALSE);
        projection.put("target", TRUE);
        object.put("projection", fromValue(projection));
        options = findOneAndUpdateOptionsFactory.build(fromValue(object));
        assertNotNull(options.getProjection());
        assertEquals(MetaExpression.parseObject(options.getProjection()), object.get("projection"));

        assertNull(options.getSort());
        LinkedHashMap<String, MetaExpression> sort = new LinkedHashMap<>();
        sort.put("target.timestamp", fromValue(-1));
        object.put("sortBy", fromValue(sort));
        options = findOneAndUpdateOptionsFactory.build(fromValue(object));
        assertNotNull(options.getSort());
        assertEquals(MetaExpression.parseObject(options.getSort()), object.get("sortBy"));

        assertFalse(options.isUpsert());
        object.put("upsert", TRUE);
        options = findOneAndUpdateOptionsFactory.build(fromValue(object));
        assertTrue(options.isUpsert());

        assertEquals(options.getReturnDocument(), ReturnDocument.BEFORE);
        object.put("returnNew", TRUE);
        options = findOneAndUpdateOptionsFactory.build(fromValue(object));
        assertEquals(options.getReturnDocument(), ReturnDocument.AFTER);

        assertEquals(options.getMaxTime(TimeUnit.MILLISECONDS), 0);
        object.put("maxTime", fromValue(2000));
        options = findOneAndUpdateOptionsFactory.build(fromValue(object));
        assertNotNull(options.getMaxTime(TimeUnit.MILLISECONDS));
        assertEquals(MetaExpression.parseObject(options.getMaxTime(TimeUnit.MILLISECONDS)), object.get("maxTime"));
    }
}
