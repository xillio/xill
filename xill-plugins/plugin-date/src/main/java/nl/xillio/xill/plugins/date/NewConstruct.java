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
 * "Returns a Date. If no parameters are passed, now() is used. Datetime can
 * either be a string or a unix timestamp. The default format for string date
 * values is yyyy-MM-dd HH:mm:ss.S. Optionally a different format can be passed
 * as second parameter." ;
 *
 *
 *
 * @author Sander
 *
 */
public class NewConstruct implements Construct {

	@Override
	public String getName() {

		return "fromTimestamp";
	}

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {

		return new ConstructProcessor(
				(dateVar, formatVar, timezoneVar) -> process(context, dateVar, formatVar, timezoneVar),
				new Argument("date", ExpressionBuilder.NULL), new Argument("format", ExpressionBuilder.NULL),
				new Argument("timezone", ExpressionBuilder.NULL));
	}

	private static MetaExpression process(final ConstructContext context, final MetaExpression dateVar,
			final MetaExpression formatVar, final MetaExpression timezoneVar) {

		DateFormat df;
		if (formatVar == ExpressionBuilder.NULL) {
			df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
		} else {
			df = new SimpleDateFormat(formatVar.getStringValue());
		}
		Date date = new Date();

		if (dateVar.getType() != ExpressionDataType.ATOMIC) {
			context.getLogger().warn("Expected atomic value for date.");
		}

		if (timezoneVar.getType() != ExpressionDataType.ATOMIC) {
			context.getLogger().warn("Expected atomic value for timezone.");
		}

		if (timezoneVar != ExpressionBuilder.NULL) {
			df.setTimeZone(TimeZone.getTimeZone(timezoneVar.getStringValue()));
		}

		if (dateVar != ExpressionBuilder.NULL) {
			if (dateVar.getNumberValue().longValue() > 2100) {
				date.setTime(dateVar.getNumberValue().longValue());
			} else {
				try {
					date = df.parse(dateVar.getStringValue());
				} catch (ParseException e) {
					throw new RobotRuntimeException("Invalid Date.");
				}
			}
		}

		MetaExpression result = ExpressionBuilder.fromValue(df.format(date));
		DateInfo info = new DateInfo(date, df);
		result.storeMeta(info);
		return result;
	}
}
