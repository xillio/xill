package nl.xillio.xill.plugins.date;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
		return new ConstructProcessor(FormatConstruct::process, new Argument("date"), new Argument("format"),
				new Argument("timezone", ExpressionBuilder.NULL));
	}

	private static MetaExpression process(final MetaExpression dateVar, final MetaExpression formatVar,
			final MetaExpression timezoneVar) {

		if (dateVar == ExpressionBuilder.NULL || formatVar == ExpressionBuilder.NULL) {
			throw new RobotRuntimeException("Input cannot be null.");
		}

		if (dateVar.getType() != ExpressionDataType.ATOMIC || formatVar.getType() != ExpressionDataType.ATOMIC
				|| timezoneVar.getType() != ExpressionDataType.ATOMIC) {
			throw new RobotRuntimeException("Expected atomic value");
		}

		String date = dateVar.getStringValue();
		String format = formatVar.getStringValue();
		String timezone = timezoneVar.getStringValue();

		Date oldDate;
		DateFormat dfOld = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		DateFormat dfNew;
		try {
			dfNew = new SimpleDateFormat(format);
		} catch (IllegalArgumentException e) {
			throw new RobotRuntimeException("Illegal character in format.");
		}
		if (timezoneVar != ExpressionBuilder.NULL) {
			dfNew.setTimeZone(TimeZone.getTimeZone(timezone));
		}
		// Get date from string.
		try {
			oldDate = dfOld.parse(date);
		} catch (ParseException e) {
			throw new RobotRuntimeException("Parse error.");
		}

		return ExpressionBuilder.fromValue(dfNew.format(oldDate));

	}
}
