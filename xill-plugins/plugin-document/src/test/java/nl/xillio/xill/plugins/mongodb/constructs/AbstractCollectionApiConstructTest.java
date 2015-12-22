package nl.xillio.xill.plugins.mongodb.constructs;

import com.mongodb.client.MongoCollection;
import nl.xillio.xill.TestUtils;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.plugins.mongodb.NoSuchConnectionException;
import nl.xillio.xill.plugins.mongodb.services.ConnectionManager;
import org.bson.Document;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;


public class AbstractCollectionApiConstructTest extends TestUtils {

    @Test
    public void testProcessCollection() throws NoSuchConnectionException {
        String collectionName = "myCollection";
        MongoCollection collection = mock(MongoCollection.class);

        ConnectionManager manager = mock(ConnectionManager.class, RETURNS_DEEP_STUBS);
        CollectionApiConstruct construct = new CollectionApiConstruct(collection);
        ConstructContext context = mock(ConstructContext.class, RETURNS_DEEP_STUBS);

        construct.setConnectionManager(manager);

        when(manager.getConnection(context).getDatabase().getCollection(collectionName)).thenReturn(collection);


        ConstructProcessor processor = construct.prepareProcess(context);

        ConstructProcessor.process(processor, fromValue(collectionName), fromValue("Some API Argument"), NULL);
    }


    static class CollectionApiConstruct extends AbstractCollectionApiConstruct {

        private final MongoCollection<Document> expectedCollection;

        CollectionApiConstruct(MongoCollection<Document> expectedCollection) {
            this.expectedCollection = expectedCollection;
        }

        @Override
        protected Argument[] getApiArguments() {
            return new Argument[]{
                    new Argument("test")
            };
        }

        @Override
        MetaExpression process(MetaExpression[] arguments, MongoCollection<Document> collection, ConstructContext context) {
            Argument[] args = getApiArguments();

            assertEquals(args.length, arguments.length);
            assertSame(collection, expectedCollection);

            return NULL;
        }
    }
}