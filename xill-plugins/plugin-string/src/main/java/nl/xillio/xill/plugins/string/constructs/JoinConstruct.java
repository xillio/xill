package nl.xillio.xill.plugins.string.constructs;

import java.util.List;
import java.util.Map;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.plugins.system.services.regex.RegexService;

import com.google.inject.Inject;

/**
 * Concatenates a list of elements using a delimiter
 */
public class JoinConstruct extends Construct {
	@Inject
	private RegexService regexService;

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(
			(list, delimiter) -> process(list, delimiter, regexService),
			new Argument("list"),
			new Argument("delimiter", fromValue(""), ATOMIC));
	}

	@SuppressWarnings("unchecked")
	private static MetaExpression process(final MetaExpression list, final MetaExpression delimiter, final RegexService regexService) {
		String output = "";

		switch (list.getType()) {
			case ATOMIC:
				output = list.getStringValue();
				break;
			case LIST:
				String[] stringList = ((List<MetaExpression>) list.getValue()).stream().map(MetaExpression::getStringValue).toArray(i -> new String[i]);
				output = regexService.join(stringList, delimiter.getStringValue());
				break;
			case OBJECT:
				String[] stringObject = ((Map<String, MetaExpression>) list.getValue()).values().stream().map(MetaExpression::getStringValue).toArray(i -> new String[i]);
				output = regexService.join(stringObject, delimiter.getStringValue());
				break;
		}

		return fromValue(output);
	}
}
