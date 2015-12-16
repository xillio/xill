package nl.xillio.xill.plugins.mongodb.constructs;


import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import org.bson.Document;

import java.util.List;
import java.util.stream.Collectors;

/**
 * This construct represents the aggregate method on MongoDB.
 *
 * @author Thomas Biesaart
 * @see <a href="https://docs.mongodb.org/v3.0/reference/method/db.collection.aggregate/#db.collection.aggregate">db.collection.aggregate</a>
 */
public class AggregateConstruct extends AbstractCollectionApiConstruct {
    @Override
    protected Argument[] getApiArguments() {
        return new Argument[]{
                new Argument("pipeline", LIST)
        };
    }

    @Override
    MetaExpression process(MetaExpression[] arguments, MongoCollection<Document> collection) {
        List<Document> pipeline = arguments[0].<List<MetaExpression>>getValue().stream()
                .map(this::toDocument)
                .collect(Collectors.toList());

        AggregateIterable<Document> mongoResult = collection.aggregate(pipeline);

        return fromValue(mongoResult, collection, arguments[0]);
    }
}
