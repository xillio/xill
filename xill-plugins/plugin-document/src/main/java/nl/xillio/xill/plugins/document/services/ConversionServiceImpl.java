package nl.xillio.xill.plugins.document.services;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import nl.xillio.udm.builders.DecoratorBuilder;
import nl.xillio.udm.builders.DocumentRevisionBuilder;

/**
 * Implementation of the {@link ConversionService}.
 *
 * @author Geert Konijnendijk
 * @author Luca Scalzotto
 *
 */
public class ConversionServiceImpl implements ConversionService {
	@Override
	public void mapToUdm(final Map<String, Map<String, Object>> object, final DocumentRevisionBuilder builder) {
		// Get all decorators from the map.
		for (Entry<String, Map<String, Object>> entry : object.entrySet()) {
			DecoratorBuilder decorator = builder.decorator(entry.getKey());

			// Get all fields from the decorator.
			for (Entry<String, Object> field : entry.getValue().entrySet()) {
				decorator.field(field.getKey(), field.getValue());
			}
		}

		// commit changes
		builder.commit();
	}

	@Override
	public Map<String, Map<String, Object>> udmToMap(final DocumentRevisionBuilder builder) {
		Map<String, Map<String, Object>> result = new HashMap<>();

		// Get all decorators from the document.
		for (String decName : builder.decorators()) {
			DecoratorBuilder decorator = builder.decorator(decName);

			// Parse all fields from the decorator into MetaExpressions.
			Map<String, Object> fields = new HashMap<>();
			for (String fieldName : decorator.fields()) {
				fields.put(fieldName, decorator.field(fieldName));
			}

			result.put(decName, fields);
		}

		return result;
	}
}
