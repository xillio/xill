package nl.xillio.xill.plugins.date;

import java.text.DateFormat;
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

/**
 *
 * Return the date of a unix timestamp. if no parameters are passed, The current
 * time is used.
 *
 *
 *
 * @author Sander
 *
 */
public class FromTimestampConstruct implements Construct {

	@Override
	public String getName() {

		return "fromTimestamp";
	}

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {

		return new ConstructProcessor((dateVar, timezoneVar) -> process(context, dateVar, timezoneVar),
				new Argument("date", ExpressionBuilder.NULL), new Argument("timezone", ExpressionBuilder.NULL));
	}

	private static MetaExpression process(final ConstructContext context, final MetaExpression dateVar,
			final MetaExpression timezoneVar) {

		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT,
				Locale.getDefault());
		Date date = new Date();

		if (dateVar.getType() != ExpressionDataType.ATOMIC) {
			context.getLogger().warn("Expected atomic value for date.");
		}

		if (timezoneVar.getType() != ExpressionDataType.ATOMIC) {
			context.getLogger().warn("Expected atomic value for timezone.");
		}

		if (timezoneVar != ExpressionBuilder.NULL) {
			dateFormat.setTimeZone(TimeZone.getTimeZone(timezoneVar.getStringValue()));
		}

		if (dateVar != ExpressionBuilder.NULL && dateVar.getType() == ExpressionDataType.ATOMIC) {
			date.setTime(dateVar.getNumberValue().longValue());
		}

		return ExpressionBuilder.fromValue(dateFormat.format(date));
	}
}
