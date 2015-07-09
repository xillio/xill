package nl.xillio.xill.plugins.date;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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
 * Modifies the provided date with the specified changes from a list. The list
 * must have seven elements: [year, month, day, hour, minute, second,
 * millisecond]
 *
 * @author Sander
 *
 */
public class ChangeConstruct implements Construct {

	@Override
	public String getName() {

		return "change";
	}

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor((dateVar, changeVar) -> process(context, dateVar, changeVar),
				new Argument("date"), new Argument("change"));
	}

	private static MetaExpression process(final ConstructContext context, final MetaExpression dateVar,
			final MetaExpression changeVar) {

		if (dateVar == ExpressionBuilder.NULL) {
			return ExpressionBuilder.NULL;
		}

		if (dateVar.getType() != ExpressionDataType.ATOMIC) {
			context.getRootLogger().warn("Expected atomic value.");
		}

		// Change the input string to a date
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT,
				Locale.getDefault());
		Date date;
		try {
			date = dateFormat.parse(dateVar.getStringValue());
		} catch (ParseException e) {
			throw new RobotRuntimeException("Invalid date.");
		}

		// The changes have to be in a list.
		if (changeVar.getType() != ExpressionDataType.LIST || changeVar == ExpressionBuilder.NULL) {
			return dateVar;
		}

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);

		@SuppressWarnings("unchecked")
		List<MetaExpression> numberList = (List<MetaExpression>) changeVar.getValue();

		if (numberList.size() == 7) {
			cal.add(Calendar.YEAR, numberList.get(0).getNumberValue().intValue());
			cal.add(Calendar.MONTH, numberList.get(1).getNumberValue().intValue());
			cal.add(Calendar.DAY_OF_MONTH, numberList.get(2).getNumberValue().intValue());
			cal.add(Calendar.HOUR_OF_DAY, numberList.get(3).getNumberValue().intValue());
			cal.add(Calendar.MINUTE, numberList.get(4).getNumberValue().intValue());
			cal.add(Calendar.SECOND, numberList.get(5).getNumberValue().intValue());
			cal.add(Calendar.MILLISECOND, numberList.get(6).getNumberValue().intValue());
			return ExpressionBuilder.fromValue(dateFormat.format(cal.getTime()));
		}
		context.getRootLogger().warn("Incorrect number of elements in list, returning unchanged date.");
		return dateVar;

	}
}
