package nl.xillio.xill.plugins.date;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
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
 * Returns the difference between two dates. By default the function will return
 * the absolute difference. Optionally you can set 'absolute' to false to get
 * the relative difference.
 *
 * @author Sander
 *
 */
public class DiffConstruct implements Construct {

	@Override
	public String getName() {

		return "diff";
	}

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor((dateVar1, dateVar2, absolute) -> process(context, dateVar1, dateVar2, absolute),
				new Argument("date1"), new Argument("date2"), new Argument("absolute", ExpressionBuilder.TRUE));
	}

	private static MetaExpression process(final ConstructContext context, final MetaExpression dateVar1,
			final MetaExpression dateVar2, final MetaExpression absolute) {

		if (dateVar1 == ExpressionBuilder.NULL || dateVar2 == ExpressionBuilder.NULL) {
			return ExpressionBuilder.NULL;
		}

		if (dateVar1.getType() != ExpressionDataType.ATOMIC) {
			context.getRootLogger().warn(("Expected atomic value for first date."));
		}
		if (dateVar2.getType() != ExpressionDataType.ATOMIC) {
			context.getRootLogger().warn(("Expected atomic value for second date."));
		}

		Date date1;
		Date date2;

		try {
			DateInfo info1 = new DateInfo();
			info1 = dateVar1.getMeta(info1.getClass());
			date1 = info1.GetDate();

			DateInfo info2 = new DateInfo();
			info2 = dateVar2.getMeta(info2.getClass());
			date2 = info2.GetDate();
		} catch (NullPointerException e) {
			throw new RobotRuntimeException("Invalid date.");
		}
		Calendar cal = Calendar.getInstance();
		cal.setTimeZone(TimeZone.getTimeZone("UTC"));

		cal.setTime(date1);
		long t1 = cal.getTimeInMillis();

		cal.setTime(date2);
		long t2 = cal.getTimeInMillis();

		long fulldiff = t1 - t2;
		if (absolute.getBooleanValue()) {
			fulldiff = Math.abs(fulldiff);
		}

		long diff = fulldiff;

		final int ONE_DAY = 1000 * 60 * 60 * 24;
		final int ONE_HOUR = ONE_DAY / 24;
		final int ONE_MINUTE = ONE_HOUR / 60;
		final int ONE_SECOND = ONE_MINUTE / 60;

		long d = diff / ONE_DAY;
		diff %= ONE_DAY;

		long h = diff / ONE_HOUR;
		diff %= ONE_HOUR;

		long m = diff / ONE_MINUTE;
		diff %= ONE_MINUTE;

		long s = diff / ONE_SECOND;
		long ms = diff % ONE_SECOND;

		LinkedHashMap<String, MetaExpression> mapping = new LinkedHashMap<String, MetaExpression>();
		mapping.put("day", ExpressionBuilder.fromValue(d));
		mapping.put("hour", ExpressionBuilder.fromValue(h));
		mapping.put("minute", ExpressionBuilder.fromValue(m));
		mapping.put("second", ExpressionBuilder.fromValue(s));
		mapping.put("millisecond", ExpressionBuilder.fromValue(ms));
		mapping.put("timestamp", ExpressionBuilder.fromValue(fulldiff));

		return ExpressionBuilder.fromValue(mapping);

	}
}
