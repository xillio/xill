package nl.xillio.xill.services.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import nl.xillio.xill.api.components.MetaExpression;

/**
 * This {@link JsonParser} uses the {@link Gson} library to parse json strings
 */
public class GsonParser implements JsonParser {
	private static final Gson gson = new GsonBuilder()
		.create();

	@Override
	public String toJson(final Object object) {
		return getGson().toJson(object);
	}

	@Override
	public <T> T fromJson(final String json, final Class<T> type) {
		return getGson().fromJson(json, type);
	}

	/**
	 * @return the {@link Gson} parser
	 */
	protected Gson getGson() {
		return gson;
	}

	@Override
	public String toJson(MetaExpression metaExpression) {
		return metaExpression.toString(getGson());
	}

}
