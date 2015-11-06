package nl.xillio.xill.services.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * This {@link JsonParser} uses the {@link Gson} library to parse json strings
 */
public class PrettyGsonParser extends GsonParser implements PrettyJsonParser {
    private static final Gson gson = new GsonBuilder()
            .enableComplexMapKeySerialization()
            .serializeNulls()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .serializeSpecialFloatingPointValues()
            .create();

    /**
     * @return the {@link Gson} parser
     */
    @Override
    protected Gson getGson() {
        return gson;
    }

}
