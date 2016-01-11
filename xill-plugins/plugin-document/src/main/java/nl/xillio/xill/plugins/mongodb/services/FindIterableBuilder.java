package nl.xillio.xill.plugins.mongodb.services;

import com.google.inject.Inject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import nl.xillio.xill.api.components.MetaExpression;
import org.bson.Document;

/**
 * This class is responsible for wrapping the functionality of parsing and getting an FindIterable.
 *
 * @author Pieter Dirk Soels
 */
public class FindIterableBuilder {

    private MongoConverter mongoConverter;

    @Inject
    void setMongoConverter(MongoConverter mongoConverter) {
        this.mongoConverter = mongoConverter;
    }

    /**
     * This function parses the given arguments to BSON-documents and runs the query on the collection
     *
     * @param collection The collection of data to do the query on.
     * @param arguments  The given parts of the query (filter, projection, sort)
     * @return FindIterable<Document>, an iterable containing the result of the query.
     */
    public FindIterable<Document> getIterable(
            MongoCollection<Document> collection,
            MetaExpression[] arguments) {

        Document filter = mongoConverter.parse(arguments[0]);
        Document projection = mongoConverter.parse(arguments[1]);
        Document sort = mongoConverter.parse(arguments[2]);

        return collection.find(filter).projection(projection).sort(sort);
    }
}
