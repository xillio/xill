package nl.xillio.xill.plugins.document.services;

import java.util.Map;

import nl.xillio.udm.builders.DocumentRevisionBuilder;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.services.XillService;

/**
 * Service for conversion from Xill objects to the UDM layer and back.
 * 
 * @author Geert Konijnendijk
 * @author Luca Scalzotto
 *
 */
public interface ConversionService extends XillService {

	/**
	 * Convert all decorators from a {@link DocumentRevisionBuilder} to a map suitable for parsing to a Xill object.
	 * 
	 * @param builder
	 *        Decorators to be converted
	 * @return A Map suited for conversion to a Xill object
	 * @see MetaExpression#parseObject(Object)
	 */
	Map<String, MetaExpression> udmToMap(DocumentRevisionBuilder builder);

	/**
	 * Convert a map parsed from a Xill object and input all converted decorators into a {@link DocumentRevisionBuilder}
	 * 
	 * @param object
	 *        Map to be converted
	 * @param builder
	 *        Builder to add the decorators to
	 * @see MetaExpression#extractValue(MetaExpression)
	 */
	void mapToUdm(Map<String, Map<String, Object>> object, DocumentRevisionBuilder builder);
}
