package nl.xillio.xill.plugins.document.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.errors.RobotRuntimeException;

/**
 * Utility methods for the document plugin.
 * 
 * @author Geert Konijnendijk
 *
 */
public class DocumentUtil {

	private DocumentUtil() {}

	/**
	 * Convert an object {@link MetaExpression} containing objects to a map contianing maps.
	 * 
	 * @param body
	 *        {@link MetaExpression} to convert
	 * @return A map containing maps representing the given {@link MetaExpression}
	 * @throws RobotRuntimeException
	 *         When the given {@link MetaExpression} is not an object containing objects
	 */
	public static Map<String, Map<String, Object>> expressionBodyToMap(MetaExpression body) {
		// Convert the MetaExpression to a map
		Map<String, Object> bodyMap = (Map<String, Object>) MetaExpression.extractValue(body);

		// Convert the map of objects to a map of maps
		Map<String, Map<String, Object>> parsedBody = new HashMap<>();
		for (Entry<String, Object> e : bodyMap.entrySet()) {
			Object value = e.getValue();
			// Verify that all values in the body are objects themselves
			if (value instanceof Map)
			{
				parsedBody.put(e.getKey(), (Map<String, Object>) value);
			}
			else {
				throw new RobotRuntimeException("Body should be an object containing objects");
			}
		}
		return parsedBody;
	}

}
