package nl.xillio.xill.plugins.mongodb.services;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.data.Date;
import nl.xillio.xill.api.data.DateFactory;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import org.bson.BsonDocument;
import org.bson.BsonValue;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

import static nl.xillio.xill.api.components.ExpressionBuilder.NULL;
import static nl.xillio.xill.api.components.ExpressionBuilder.fromValue;

@Singleton
public class BsonValueConverter {

    private final DateFactory dateFactory;

    @Inject
    public BsonValueConverter(DateFactory dateFactory) {
        this.dateFactory = dateFactory;
    }

    public MetaExpression convert(BsonValue value) {
        if (value.isArray()) {
            return fromValue(value.asArray().stream().map(this::convert).collect(Collectors.toList()));
        }

        if (value.isDocument()) {
            LinkedHashMap<String, MetaExpression> result = new LinkedHashMap<>();
            BsonDocument doc = value.asDocument();
            doc.keySet().stream().forEach(key -> result.put(key, convert(doc.get(key))));
            return fromValue(result);
        }

        if (value.isNull()) {
            return NULL;
        }

        if (value.isBoolean()) {
            return fromValue(value.asBoolean().getValue());
        }

        if (value.isString()) {
            return fromValue(value.asString().getValue());
        }

        if (value.isInt64()) {
            return fromValue(value.asInt64().getValue());
        }

        if (value.isInt32()) {
            return fromValue(value.asInt32().getValue());
        }

        if (value.isDouble()) {
            return fromValue(value.asDouble().getValue());
        }

        if (value.isTimestamp()) {
            Instant instant = Instant.ofEpochSecond(value.asTimestamp().getTime());
            return parseDate(instant);
        }

        if (value.isDateTime()) {
            Instant instant = Instant.ofEpochMilli(value.asDateTime().getValue());
            return parseDate(instant);
        }

        throw new RobotRuntimeException("No conversion codex found for bson type " + value.getClass().getSimpleName());
    }

    private MetaExpression parseDate(Instant instant) {
        Date date = dateFactory.from(instant);
        MetaExpression result = fromValue(date.toString());
        result.storeMeta(Date.class, date);
        return result;
    }
}
