package nl.xillio.xill.plugins.string.constructs;

import java.util.List;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.plugins.system.services.regex.MatchService;

import com.google.inject.Inject;

/**
 * <p>Returns true when the first value contains the second value. </p>
 *
 *
 * @author Sander
 */
public class ContainsConstruct extends Construct {

	@Inject
	private MatchService matchService;

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(
			(haystack, needle) ->	process(haystack, needle, matchService),
			new Argument("haystack", ATOMIC, LIST),
			new Argument("needle", ATOMIC));
	}

	@SuppressWarnings("unchecked")
	private static MetaExpression process(final MetaExpression haystack, final MetaExpression needle, final MatchService matchService) {
		// If either is null then false.
		if (haystack == NULL || needle == NULL) {
			return fromValue(false);
		}

		// Compare lists
		if (haystack.getType() == LIST) {
			List<Object> list = (List<Object>) haystack.getValue();
			return fromValue(matchService.contains(list, needle));
		}

		// Compare strings
		String value1 = haystack.getStringValue();
		String value2 = needle.getStringValue();
		return fromValue(matchService.contains(value1, value2));
	}

}
