package nl.xillio.xill.plugins.date;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import nl.xillio.xill.api.components.ExpressionBuilder;
import nl.xillio.xill.api.components.ExpressionDataType;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;

/**
 *
 *
 * Converts the date to a string formatted according to the provided format.
 *
 * @author Sander
 *
 */
public class FormatConstruct implements Construct {

	@Override
	public String getName() {

		return "format";
	}

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {

		return new ConstructProcessor(
				(dateVar, formatVar, timezoneVar) -> process(context, dateVar, formatVar, timezoneVar),
				new Argument("date"), new Argument("format", ExpressionBuilder.NULL),
				new Argument("timezone", ExpressionBuilder.NULL));
	}

	private static MetaExpression process(final ConstructContext context, final MetaExpression dateVar,
			final MetaExpression formatVar, final MetaExpression timezoneVar) {

		if (dateVar.getType() != ExpressionDataType.ATOMIC || formatVar.getType() != ExpressionDataType.ATOMIC
				|| timezoneVar.getType() != ExpressionDataType.ATOMIC) {
			context.getRootLogger().warn("Expected atomic value.");
		}

		if (dateVar == ExpressionBuilder.NULL) {
			return ExpressionBuilder.NULL;
		}

		String date = dateVar.getStringValue();
		String format = formatVar.getStringValue();
		String timezone = timezoneVar.getStringValue();

		Date oldDate;
		DateFormat dfOld = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.getDefault());
		DateFormat dfNew;
		try {
			if (formatVar == ExpressionBuilder.NULL) {
				dfNew = dfOld;
			} else {
				dfNew = new SimpleDateFormat(format);
			}
		} catch (IllegalArgumentException e) {
			context.getLogger().warn("Illegal character in format");
			return ExpressionBuilder.NULL;
		}
		if (timezoneVar != ExpressionBuilder.NULL) {
			dfNew.setTimeZone(TimeZone.getTimeZone(timezone));
		}
		// Get date from string.
		try {
			oldDate = dfOld.parse(date);
		} catch (ParseException e) {
			throw new RobotRuntimeException("Invalid date.");
		}

		return ExpressionBuilder.fromValue(dfNew.format(oldDate));

	}
}
