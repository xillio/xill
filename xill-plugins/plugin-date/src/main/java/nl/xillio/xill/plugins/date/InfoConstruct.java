package nl.xillio.xill.plugins.date;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;

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
 * Returns detailed info on the specified date.
 *
 * @author Sander
 *
 */
public class InfoConstruct implements Construct {

	@Override
	public String getName() {

		return "info";
	}

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor((dateVar) -> process(context, dateVar), new Argument("date"));

	}

	private static MetaExpression process(final ConstructContext context, final MetaExpression dateVar) {

		if (dateVar == ExpressionBuilder.NULL) {
			return ExpressionBuilder.NULL;
		}

		if (dateVar.getType() != ExpressionDataType.ATOMIC) {
			context.getRootLogger().warn("Expected an atomic value.");
		}

		DateInfo info = new DateInfo();
		DateFormat df;
		Date date;
		try {
			info = dateVar.getMeta(info.getClass());
			df = info.GetFormat();
			date = info.GetDate();
		} catch (NullPointerException e) {
			throw new RobotRuntimeException("Invalid date.");
		}
		Locale locale = new Locale("enUS");
		Calendar cal = df.getCalendar();
		cal.setTime(date);

		LinkedHashMap<String, MetaExpression> mapping = new LinkedHashMap<String, MetaExpression>();
		mapping.put("year", ExpressionBuilder.fromValue(cal.get(Calendar.YEAR)));
		mapping.put("month", ExpressionBuilder.fromValue(cal.get(Calendar.MONTH) + 1));
		mapping.put("day", ExpressionBuilder.fromValue(cal.get(Calendar.DAY_OF_MONTH)));
		mapping.put("hour", ExpressionBuilder.fromValue(cal.get(Calendar.HOUR_OF_DAY)));
		mapping.put("minute", ExpressionBuilder.fromValue(cal.get(Calendar.MINUTE)));
		mapping.put("second", ExpressionBuilder.fromValue(cal.get(Calendar.SECOND)));
		mapping.put("millisecond", ExpressionBuilder.fromValue(cal.get(Calendar.MILLISECOND)));
		mapping.put("monthname",
				ExpressionBuilder.fromValue(cal.getDisplayName(Calendar.MONTH, Calendar.LONG, locale)));
		mapping.put("dayname",
				ExpressionBuilder.fromValue(cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, locale)));
		mapping.put("week", ExpressionBuilder.fromValue(cal.get(Calendar.WEEK_OF_YEAR)));
		mapping.put("weekofmonth", ExpressionBuilder.fromValue(cal.get(Calendar.WEEK_OF_MONTH)));
		mapping.put("timestamp", ExpressionBuilder.fromValue(cal.getTime().getTime()));

		return ExpressionBuilder.fromValue(mapping);

	}
}
