package nl.xillio.xill.services.json;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.services.XillService;

/**
 * This service is capable of parsing objects to and from json
 */
public interface JsonParser extends XillService {
	/**
	 * Parse an object to json
	 * 
	 * @param object
	 *        the object to parse
	 * @return a json string
	 */
	public String toJson(Object object);
	
	/**
	 * Parse a {@link MetaExpression} to a json {@link String}
	 * @param metaExpression the expression
	 * @return a json string
	 */
	public String toJson(MetaExpression metaExpression);

	/**
	 * Parse a json string to an object
	 * 
	 * @param <T>
	 *        the type of object to build
	 * @param json
	 *        the json string
	 * @param type
	 *        the type of object to build
	 * @return the object
	 */
	public <T> T fromJson(String json, Class<T> type);
}
