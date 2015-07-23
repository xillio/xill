package nl.xillio.xill.plugins.string.constructs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;

/**
 *
 *
 * Splits the provided value into a list of strings, based on the provided
 * delimiter. </br>
 * Optionally you can set keepempty to true to keep empty entries.
 *
 * @author Sander
 *
 */
public class SplitConstruct extends Construct {

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(
			SplitConstruct::process, 
			new Argument("string", ATOMIC), 
			new Argument("delimiter", ATOMIC), 
			new Argument("keepempty", FALSE, ATOMIC));
	}

	private static MetaExpression process(final MetaExpression string, final MetaExpression delimiter, final MetaExpression keepempty) {
		assertNotNull(string, "string");
		assertNotNull(delimiter, "delimiter");

		boolean keepEmpty = keepempty.getBooleanValue();

		String[] stringArray = string.getStringValue().split(delimiter.getStringValue());

		List<MetaExpression> list = new ArrayList<>();

		Arrays.stream(stringArray).forEach(str -> {
			if (keepEmpty || !str.equals("")) {
				list.add(fromValue(str));
			}
		});

		return fromValue(list);
	}
}
