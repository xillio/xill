package nl.xillio.xill.plugins.string.constructs;

import java.util.ArrayList;
import java.util.List;
import java.util.MissingFormatArgumentException;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.string.services.string.RegexService;

import com.google.inject.Inject;

/**
 *
 * <p>Formats the string with the provided values. </p>
 * Does not support Time/Date.
 *
 * @author Sander
 *
 */
public class FormatConstruct extends Construct {
	private final RegexConstruct regexConstruct;
	@Inject
	private RegexService regexService;

	/**
	 * Create a new {@link FormatConstruct}
	 *
	 * @param regexConstruct
	 *        the contruct used to find the format parameters
	 */
	public FormatConstruct(final RegexConstruct regexConstruct) {
		this.regexConstruct = regexConstruct;
	}

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(
			(textVar, valueVar) -> process(regexConstruct, textVar, valueVar, regexService),
			new Argument("text", ATOMIC),
			new Argument("value", LIST));
	}

	private static MetaExpression process(final RegexConstruct regexConstruct, final MetaExpression textVar, final MetaExpression valueVar, final RegexService regexService) {
		assertNotNull(textVar, "text");

		List<MetaExpression> formatList = new ArrayList<>();
		List<Object> list = new ArrayList<>();
		@SuppressWarnings("unchecked")
		List<MetaExpression> numberList = (List<MetaExpression>) valueVar.getValue();

		List<String> tryFormat = regexService.tryMatch("%[[^a-zA-Z%]]*([a-zA-Z]|[%])", textVar.getStringValue(), RegexConstruct.REGEX_TIMEOUT, regexConstruct);
		for (String s : tryFormat) {
			formatList.add(fromValue(s));
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
			return fromValue(String.format(textVar.getStringValue(), list.toArray()));
		} catch (MissingFormatArgumentException e) {
			throw new RobotRuntimeException("Not enough arguments.");
		}
	}
}
