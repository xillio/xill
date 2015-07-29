package nl.xillio.xill.plugins.string.constructs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.inject.Inject;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.plugins.string.services.string.StringService;

/**
 *
 *
 * <p>Splits the provided value into a list of strings, based on the provided delimiter.</p>
 * <p>Optionally you can set keepempty to true to keep empty entries.</p>
 *
 * @author Sander
 *
 */
public class SplitConstruct extends Construct {
	@Inject
	private StringService stringService;

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(
			(string, delimiter, keepEmpty) -> process(string, delimiter, keepEmpty, stringService),
			new Argument("string", ATOMIC), 
			new Argument("delimiter", ATOMIC), 
			new Argument("keepEmpty", FALSE, ATOMIC));
	}

	private static MetaExpression process(final MetaExpression string, final MetaExpression delimiter, final MetaExpression keepempty, StringService stringService) {
		assertNotNull(string, "string");
		assertNotNull(delimiter, "delimiter");

		boolean keepEmpty = keepempty.getBooleanValue();

		String[] stringArray = stringService.split(string.getStringValue(), delimiter.getStringValue());

		List<MetaExpression> list = new ArrayList<>();

		Arrays.stream(stringArray).forEach(str -> {
			if (keepEmpty || !str.equals("")) {
				list.add(fromValue(str));
			}
		});

		return fromValue(list);
	}
}
