package nl.xillio.xill.plugins.system.constructs;

import com.google.inject.Inject;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.plugins.system.services.properties.SystemPropertiesService;

/**
 * Gets a list or a single system property
 */
public class PropertiesConstruct extends Construct {

	@Inject
	SystemPropertiesService propertiesService;

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(
			prop -> process(prop, propertiesService),
			new Argument("property", NULL, ATOMIC));
	}

	static MetaExpression process(final MetaExpression property, final SystemPropertiesService properties) {

		if (property.isNull()) {
			return parseObject(properties.getProperties());
		}

		return fromValue(properties.getProperty(property.getStringValue()));
	}
}
