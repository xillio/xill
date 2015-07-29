package nl.xillio.xill.plugins.string.constructs;

import java.util.regex.Matcher;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.plugins.string.services.string.RegexService;
import nl.xillio.xill.plugins.string.services.string.StringService;

import com.google.inject.Inject;

/**
 * Returns a new string in which occurrences of regex needle found in the text
 * have been replaced by the replacement string. If the parameter 'replaceall'
 * is set to false, the routine will only replace the first occurrence. If the
 * parameter 'useregex' is set to false, the routine will not use a regex.
 *
 * @author Sander
 *
 */
public class ReplaceConstruct extends Construct {
	@Inject
	private RegexService regexService;
	@Inject
	private StringService stringService;

	/**
	 * Create a new {@link ReplaceConstruct}
	 *
	 * @param regexConstruct
	 *        the construct used to find matches
	 */
	public ReplaceConstruct() {}

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		Argument args[] = {
				new Argument("text", ATOMIC),
				new Argument("needle", ATOMIC),
				new Argument("replacement", ATOMIC),
				new Argument("useregex", TRUE, ATOMIC),
				new Argument("replaceall", TRUE, ATOMIC),
				new Argument("timeout", fromValue(RegexConstruct.REGEX_TIMEOUT), ATOMIC)};

		return new ConstructProcessor((a) -> process(a, regexService, stringService), args);

	}

	private static MetaExpression process(final MetaExpression[] input, final RegexService regexService, final StringService stringService) {

		for (int i = 0; i < 5; i++) {
			assertNotNull(input[i], "input");
		}

		String text = input[0].getStringValue();
		String needle = input[1].getStringValue();
		String replacement = input[2].getStringValue();
		boolean useregex = input[3].getBooleanValue();
		boolean replaceall = input[4].getBooleanValue();
		int timeout = (int) input[5].getNumberValue().doubleValue() * 1000;

		if (useregex) {
			Matcher m = regexService.getMatcher(needle, text, timeout);
			if (replaceall) {
				return fromValue(regexService.replaceAll(m, replacement));
			}
			return fromValue(regexService.replaceFirst(m, replacement));
		}
		if (replaceall) {
			return fromValue(stringService.replaceAll(text, needle, replacement));
		}
		return fromValue(stringService.replaceFirst(text, needle, replacement));

	}
}
