package nl.xillio.xill.plugins.string.constructs;

import java.util.ArrayList;
import java.util.List;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.string.services.string.RegexService;

import com.google.inject.Inject;

/**
 * <p>
 * Extracts all matching substrings of text into a list.
 * </p>
 *
 * @author Sander
 *
 */
public class AllMatchesConstruct extends Construct {

	private final RegexConstruct regexConstruct;

	@Inject
	private RegexService regexService;

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
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(
			(valueVar, regexVar, timeout) -> process(regexConstruct, valueVar, regexVar, timeout, regexService),
			new Argument("value", ATOMIC),
			new Argument("regex", ATOMIC),
			new Argument("timeout", fromValue(RegexConstruct.REGEX_TIMEOUT), ATOMIC));
	}

	@SuppressWarnings("javadoc")
	public static MetaExpression process(final RegexConstruct regexConstruct, final MetaExpression textVar, final MetaExpression regexVar, final MetaExpression timeoutVar,
			final RegexService regexService) {

		List<MetaExpression> list = new ArrayList<>();

		String text = textVar.getStringValue();
		String regex = regexVar.getStringValue();
		int timeout = (int) timeoutVar.getNumberValue().doubleValue() * 1000;

		try {
			List<String> results = regexService.tryMatch(regex, text, timeout, regexConstruct);
			for (String s : results) {
				list.add(fromValue(s));
			}
		} catch (Exception e) {
			throw new RobotRuntimeException("Invalid pattern.");
		}
		return fromValue(list);
	}
}
