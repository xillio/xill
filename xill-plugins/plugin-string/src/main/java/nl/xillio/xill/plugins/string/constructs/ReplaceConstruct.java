package nl.xillio.xill.plugins.string.constructs;

import java.io.InputStream;
import java.util.regex.Matcher;

import org.apache.commons.lang3.StringUtils;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.construct.HelpComponent;

/**
 * Returns a new string in which occurrences of regex needle found in the text
 * have been replaced by the replacement string. If the parameter 'replaceall'
 * is set to false, the routine will only replace the first occurrence. If the
 * parameter 'useregex' is set to false, the routine will not use a regex.
 *
 * @author Sander
 *
 */
public class ReplaceConstruct extends Construct implements HelpComponent {

	private final RegexConstruct regexConstruct;

	/**
	 * Create a new {@link ReplaceConstruct}
	 * 
	 * @param regexConstruct
	 *        the construct used to find matches
	 */
	public ReplaceConstruct(final RegexConstruct regexConstruct) {
		this.regexConstruct = regexConstruct;
	}

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		Argument args[] = {new Argument("text"), new Argument("needle"), new Argument("replacement"), new Argument("useregex", TRUE), new Argument("replaceall", TRUE),
						new Argument("timeout", fromValue(RegexConstruct.REGEX_TIMEOUT))};

		return new ConstructProcessor((a) -> process(regexConstruct, a), args);

	}

	private static MetaExpression process(final RegexConstruct regexConstruct, final MetaExpression[] input) {

		for (int i = 0; i < 5; i++) {
			assertType(input[i], "input", ATOMIC);
			assertNotNull(input[i], "input");
		}

		String text = input[0].getStringValue();
		String needle = input[1].getStringValue();
		String replacement = input[2].getStringValue();
		boolean useregex = input[3].getBooleanValue();
		boolean replaceall = input[4].getBooleanValue();
		int timeout = (int) input[5].getNumberValue().doubleValue() * 1000;

		if (useregex) {
			Matcher m = regexConstruct.getMatcher(needle, text, timeout);
			if (replaceall) {
				return fromValue(m.replaceAll(replacement));
			}
			return fromValue(m.replaceFirst(replacement));
		}
		if (replaceall) {
			return fromValue(text.replace(needle, replacement));
		}
		return fromValue(StringUtils.replaceOnce(text, needle, replacement));

	}

	@Override
	public InputStream openDocumentationStream() {
		return getClass().getResourceAsStream("/helpfiles/replace.xml");
	}
}
