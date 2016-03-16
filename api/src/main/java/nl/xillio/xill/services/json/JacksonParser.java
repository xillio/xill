package nl.xillio.xill.services.json;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import nl.xillio.xill.api.components.MetaExpression;

import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * This implementation of the JsonParser uses Jackson.
 *
 * @author Thomas Biesaart
 */
public class JacksonParser implements PrettyJsonParser {
    private final ObjectMapper mapper = new ObjectMapper();

    public JacksonParser(boolean pretty) {
        if (pretty) {
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
        }
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"));
    }

    @Override
    @SuppressWarnings("squid:S1166") // Ignore StackOverflowError not being handled since it is too large due to the circular reference.
    public String toJson(Object object) throws JsonException {
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new JsonException("Failed to parse json: " + e.getMessage(), e);
        } catch (StackOverflowError e) {
            throw new JsonException("A stack overflow error occurred while parsing the JSON, this is likely due a circular reference.");
        }
    }

    @Override
    public String toJson(MetaExpression metaExpression) throws JsonException {
        return toJson((Object) MetaExpression.extractValue(metaExpression));
    }

    @Override
    public <T> T fromJson(String json, Class<T> type) throws JsonException {
        ObjectMapper newMapper = new ObjectMapper();
        try {
            return newMapper.readValue(json, type);
        } catch (IOException e) {
            throw new JsonException("Failed to parse json: " + e.getMessage(), e);
        }
    }
}
