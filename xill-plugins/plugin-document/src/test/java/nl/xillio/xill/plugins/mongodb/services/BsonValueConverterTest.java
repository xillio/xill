package nl.xillio.xill.plugins.mongodb.services;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.data.Date;
import nl.xillio.xill.api.data.DateFactory;
import nl.xillio.xill.plugins.mongodb.data.MongoObjectId;
import nl.xillio.xill.services.json.JacksonParser;
import org.bson.*;
import org.bson.types.ObjectId;
import org.testng.annotations.Test;

import java.time.Instant;
import java.util.Collections;

import static nl.xillio.xill.api.components.ExpressionBuilder.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;


public class BsonValueConverterTest {

    @Test
    public void testConvert() throws Exception {
        BsonValueConverter converter = new BsonValueConverter(null, null);
        JacksonParser parser = new JacksonParser(false);

        assertEquals(converter.convert(new BsonString("Hello")), fromValue("Hello"));
        assertEquals(converter.convert(new BsonDouble(6.3)), fromValue(6.3));
        assertEquals(converter.convert(new BsonInt32(13423)), fromValue(13423));
        assertEquals(converter.convert(new BsonInt64(13423L)), fromValue(13423L));
        assertEquals(converter.convert(new BsonBoolean(false)), FALSE);
        assertEquals(converter.convert(new BsonNull()), NULL);
        assertEquals(converter.convert(new BsonDocument("key", new BsonString("value"))).toString(parser), "{\"key\":\"value\"}");
        assertEquals(converter.convert(new BsonArray(Collections.singletonList(new BsonString("hello world")))).toString(parser), "[\"hello world\"]");
    }


    @Test
    public void testConvertDates() throws Exception {
        DateFactory dateFactory = mock(DateFactory.class, RETURNS_DEEP_STUBS);
        BsonValueConverter converter = new BsonValueConverter(dateFactory, null);

        MetaExpression timestampDate = converter.convert(new BsonTimestamp(1000, 0));
        assertNotNull(timestampDate.getMeta(Date.class));
        verify(dateFactory).from(eq(Instant.ofEpochSecond(1000)));

        MetaExpression dateTimeDate = converter.convert(new BsonDateTime(120));
        assertNotNull(dateTimeDate.getMeta(Date.class));
        verify(dateFactory).from(eq(Instant.ofEpochSecond(120)));
    }

    @Test
    public void testConvertObjectId() throws Exception {
        ObjectIdSerializer objectIdSerializer = new ObjectIdSerializer();
        BsonValueConverter converter = new BsonValueConverter(null, objectIdSerializer);
        ObjectId objectId = new ObjectId();
        MetaExpression expression = converter.convert(new BsonObjectId(objectId));
        assertNotNull(expression.getMeta(MongoObjectId.class));
        assertEquals(expression.getMeta(MongoObjectId.class).getObjectId(), objectId);
    }

}