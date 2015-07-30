package nl.xillio.xill.plugins.string.constructs;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.PatternSyntaxException;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.string.exceptions.FailedToGetMatcherException;
import nl.xillio.xill.plugins.string.services.string.RegexService;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * <p>
 * Returns a list of matches of the specified regex on the provide string.
 * </p>
 *
 * @author Sander
 */
@Singleton
public class RegexConstruct extends Construct {

	@Inject
	private RegexService regexService;

	/**
	 * The default timeout for regular expressions.
	 */
	public final static int REGEX_TIMEOUT = 5;

	/**
	 * Create a new {@link RegexConstruct} and start the regexTimer {@link Thread}
	 */
	public RegexConstruct() {

	}

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(
			(string, regex, timeout) -> process(string, regex, timeout, regexService),
			new Argument("string", ATOMIC),
			new Argument("regex", ATOMIC),
			new Argument("timeout", fromValue(REGEX_TIMEOUT), ATOMIC));
	}

	static MetaExpression process(final MetaExpression valueVar, final MetaExpression regexVar, final MetaExpression timeoutVar, final RegexService regexService) {

		String regex = regexVar.getStringValue();
		int timeout = (int) timeoutVar.getNumberValue().doubleValue() * 1000;

		try {
			Matcher matcher = regexService.getMatcher(regex, valueVar.getStringValue(), timeout);

			if (regexService.matches(matcher)) {
				List<MetaExpression> list = new ArrayList<>();
				List<String> listAsStrings = regexService.tryMatchElseNull(matcher);
				for (String s : listAsStrings) {
					if (s != null) {
						list.add(fromValue(s));
					} else {
						list.add(NULL);
					}
				}
				return fromValue(list);
			}
			return NULL;
		} catch (PatternSyntaxException e) {
			throw new RobotRuntimeException("Invalid pattern in regex()");
		} catch (IllegalArgumentException | FailedToGetMatcherException e) {
			throw new RobotRuntimeException("Error while executing the regex");
		}
	}

}
