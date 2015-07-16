package nl.xillio.xill.plugins.string.constructs;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;

/**
 * Extracts all matching substrings of text into a list.
 *
 * @author Sander
 *
 */
public class AllMatchesConstruct extends Construct {

	private final RegexConstruct regexConstruct;

	/**
	 * Create a new {@link AllMatchesConstruct}
	 *
	 * @param regexConstruct
	 *        the construct used to perform the matching
	 */
	public AllMatchesConstruct(final RegexConstruct regexConstruct) {
		this.regexConstruct = regexConstruct;
	}

	@Override
	public String getName() {

		return "allMatches";
	}

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor((valueVar, regexVar, timeout) -> process(regexConstruct, valueVar, regexVar, timeout), new Argument("valueVar"), new Argument("regexVar"),
			new Argument("timeoutVar", fromValue(RegexConstruct.REGEX_TIMEOUT)));
	}

	private static MetaExpression process(final RegexConstruct regexConstruct, final MetaExpression textVar, final MetaExpression regexVar, final MetaExpression timeoutVar) {

		assertType(textVar, "text", ATOMIC);
		assertType(regexVar, "regex", ATOMIC);

		List<MetaExpression> list = new ArrayList<>();

		String text = textVar.getStringValue();
		String regex = regexVar.getStringValue();
		int timeout = (int) timeoutVar.getNumberValue().doubleValue() * 1000;

		try {
			Matcher matcher = regexConstruct.getMatcher(regex, text, timeout);
			int i = 0;
			while (matcher.find()) {
				list.add(i, fromValue(matcher.group()));
			}
		} catch (Exception e) {
			throw new RobotRuntimeException("Invalid pattern.");
		}
		return fromValue(list);

	}

}
