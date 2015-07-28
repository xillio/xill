package nl.xillio.xill.plugins.string.constructs;

import java.util.List;
import java.util.Map;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.plugins.string.services.string.StringService;

import com.google.inject.Inject;

/**
 * Concatenates a list of elements using a delimiter
 */
public class JoinConstruct extends Construct {
	@Inject
	private StringService stringService;

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(
			(list, delimiter) -> process(list, delimiter, stringService),
			new Argument("list"),
			new Argument("delimiter", fromValue(""), ATOMIC));
	}

	@SuppressWarnings({"unchecked", "javadoc"})
	public
	static MetaExpression process(final MetaExpression list, final MetaExpression delimiter, final StringService stringService) {
		String output = "";

		switch (list.getType()) {
			case ATOMIC:
				output = list.getStringValue();
				break;
			case LIST:
				String[] stringList = ((List<MetaExpression>) list.getValue()).stream().map(MetaExpression::getStringValue).toArray(i -> new String[i]);
				output = stringService.join(stringList, delimiter.getStringValue());
				break;
			case OBJECT:
				String[] stringObject = ((Map<String, MetaExpression>) list.getValue()).values().stream().map(MetaExpression::getStringValue).toArray(i -> new String[i]);
				output = stringService.join(stringObject, delimiter.getStringValue());
				break;
		}

		return fromValue(output);
	}
}
