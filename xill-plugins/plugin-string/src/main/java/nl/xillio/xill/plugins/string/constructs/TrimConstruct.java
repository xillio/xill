package nl.xillio.xill.plugins.string.constructs;

import java.util.ArrayList;
import java.util.List;

import nl.xillio.xill.api.components.ExpressionDataType;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.plugins.string.services.string.StringService;

import com.google.inject.Inject;

/**
 *
 * <p>
 * Returns a trimmed string.
 * </p>
 * <p>
 * If the input is a list it returns a list where every element is trimmed.
 * </p>
 * <p>
 * If optional parameter 'internal' is set to true the routine will also replace slack whitespace inside the string with a single space.
 * </p>
 *
 * @author Sander
 *
 */
public class TrimConstruct extends Construct {
	@Inject
	StringService stringService;

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(
			(string, internal) -> process(string, internal, stringService),
			new Argument("string", ATOMIC, LIST),
			new Argument("internal", FALSE, ATOMIC));
	}

	private static MetaExpression process(final MetaExpression string, final MetaExpression internal, final StringService stringService) {
		assertNotNull(string, "string");

		if (string.getType() == ExpressionDataType.LIST) {

			List<MetaExpression> stringList = new ArrayList<>();

			@SuppressWarnings("unchecked")
			List<MetaExpression> list = (List<MetaExpression>) string.getValue();

			list.forEach(str -> {
				if (!str.isNull()) {
					stringList.add(doTrimming(str, internal, stringService));

				}
			});
			return fromValue(stringList);

		}
		return fromValue(doTrimming(string, internal, stringService).getStringValue());
	}

	private static MetaExpression doTrimming(final MetaExpression string, final MetaExpression internal, final StringService stringService) {
		String text = string.getStringValue();

		text = stringService.replaceAll(text, "\u00A0", " ");
		text = stringService.trim(text);

		if (internal.getBooleanValue()) {
			text = stringService.replaceAll(text, "[\\s]+", " ");
		}
		return fromValue(text);
	}
}
