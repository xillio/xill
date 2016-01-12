package nl.xillio.xill.plugins.mongodb.constructs;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.ConstructContext;
import org.bson.Document;

/**
 * This construct represents the findOne method on MongoDB using the findConstruct.
 * It extends the findConstruct since the arguments and the services are identical.
 *
 * @author Thomas Biesaart
 * @author Pieter Dirk Soels
 * @see FindConstruct
 * @see <a href="https://docs.mongodb.org/v3.0/reference/method/db.collection.findOne/#db.collection.findOne">db.collection.findOne</a>
 */
public class FindOneConstruct extends FindConstruct {

    @Override
    MetaExpression process(MetaExpression[] arguments, MongoCollection<Document> collection, ConstructContext context) {
        FindIterable<Document> mongoResult = findIterableBuilder.getIterable(collection, arguments);
        return toExpression(mongoResult.first());
    }
}
