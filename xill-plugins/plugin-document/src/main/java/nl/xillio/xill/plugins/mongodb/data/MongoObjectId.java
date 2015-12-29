package nl.xillio.xill.plugins.mongodb.data;

import nl.xillio.xill.api.data.MetadataExpression;
import org.bson.types.ObjectId;

/**
 * Represents a Mongo ObjectId
 *
 * @author Titus Nachbauer
 */
public class MongoObjectId implements MetadataExpression{
    private final ObjectId objectId;

    /**
     * Creates a new ObjectId
     */
    public MongoObjectId() {
        objectId = new ObjectId();
    }

    /**
     * Creates a new ObjectId based on a 24 character hexadecimal string
     * @param objectIdHex the id string
     */
    public MongoObjectId(String objectIdHex) {
        objectId = new ObjectId(objectIdHex);
    }

    public String toString () {
        return objectId.toString();
    }

    public ObjectId getObjectId() {
        return objectId;
    }
}
