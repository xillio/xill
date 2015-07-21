package nl.xillio.xill.plugins.date.constructs;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.date.BaseDateConstruct;

/**
 *
 *
 * converts the date to a string using the provided format.
 * if no format is given the pattern "yyyy-MM-dd HH:mm:ss" is used.
 *
 * @author Sander
 *
 */
public class FormatConstruct extends BaseDateConstruct {
	private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {

		return new ConstructProcessor(FormatConstruct::process, new Argument("date"), new Argument("format", NULL));
	}

	private static MetaExpression process(final MetaExpression dateVar,
					final MetaExpression formatVar) {

		ZonedDateTime date = getDate(dateVar, "date");
		DateTimeFormatter formatter;

		if (formatVar != NULL) {
			try {
				formatter = DateTimeFormatter.ofPattern(formatVar.getStringValue());
			} catch (IllegalArgumentException e) {
				throw new RobotRuntimeException(e.getMessage());
			}
		} else {
			formatter = DEFAULT_FORMATTER;
		}

		String s = "";
		s = date.format(formatter);

		MetaExpression result = fromValue(s);
		return result;

	}
}
