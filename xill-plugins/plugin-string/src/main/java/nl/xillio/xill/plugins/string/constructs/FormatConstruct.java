package nl.xillio.xill.plugins.string.constructs;

import java.util.ArrayList;
import java.util.List;
import java.util.MissingFormatArgumentException;
import java.util.regex.Matcher;
import java.util.regex.PatternSyntaxException;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.string.services.string.RegexService;
import nl.xillio.xill.plugins.string.services.string.StringService;

import com.google.inject.Inject;

/**
 *
 * <p>
 * Formats the string with the provided values.
 * </p>
 * <p>
 * Does not support Time/Date.
 * </p>
 *
 * @author Sander
 *
 */
public class FormatConstruct extends Construct {
	@Inject
	private RegexService regexService;
	@Inject
	private StringService stringService;

	/**
	 * Create a new {@link FormatConstruct}
	 */
	public FormatConstruct() {}

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(
			(textVar, valueVar) -> process(textVar, valueVar, regexService, stringService),
			new Argument("text", ATOMIC),
			new Argument("value", LIST));
	}

	static MetaExpression process(final MetaExpression textVar, final MetaExpression valueVar, final RegexService regexService, final StringService stringService) {
		assertNotNull(textVar, "text");

		List<MetaExpression> formatList = new ArrayList<>();
		List<Object> list = new ArrayList<>();
		@SuppressWarnings("unchecked")
		List<MetaExpression> numberList = (List<MetaExpression>) valueVar.getValue();

		try {
			Matcher matcher = regexService.getMatcher("%[[^a-zA-Z%]]*([a-zA-Z]|[%])", textVar.getStringValue(), RegexConstruct.REGEX_TIMEOUT);
			List<String> tryFormat = regexService.tryMatch(matcher);
			for (String s : tryFormat) {
				formatList.add(fromValue(s));
			}
		} catch (PatternSyntaxException e) {
			throw new RobotRuntimeException("SyntaxError in the by the system provided pattern.");
		} catch (IllegalArgumentException e) {
			throw new RobotRuntimeException("Illegal argument handed when trying to match.");
		}

		// Cast the MetaExpressions to the right type.
		int count = 0;
		String typeString;
		for (int j = 0; j < numberList.size() - count; j++) {
			try {
				typeString = formatList.get(j).getStringValue();
			} catch (IndexOutOfBoundsException e) {
				break;
			}
			switch (typeString.charAt(typeString.length() - 1)) {
				case 'd':
				case 'o':
				case 'x':
				case 'X':
				case 'h':
				case 'H':
					list.add(numberList.get(j + count).getNumberValue().intValue());
					break;
				case 'e':
				case 'E':
				case 'f':
				case 'g':
				case 'G':
				case 'a':
				case 'A':
					list.add(numberList.get(j + count).getNumberValue().floatValue());
					break;
				case 'c':
				case 'C':
					list.add(numberList.get(j + count).getStringValue().charAt(0));
					break;
				case 's':
				case 'S':
					list.add(numberList.get(j + count).getStringValue());
					break;
				case 'b':
				case 'B':
					list.add(numberList.get(j + count).getBooleanValue());
					break;
				case '%':
					count--;
					break;
				case 't':
				case 'T':
					throw new RobotRuntimeException("Date/Time conversions are not supported.");
				default:
					throw new RobotRuntimeException("Unexpected conversion type: " + typeString);
			}
		}
		try {
			return fromValue(stringService.format(textVar.getStringValue(), list));
		} catch (MissingFormatArgumentException e) {
			throw new RobotRuntimeException("Not enough arguments.");
		}
	}
}
