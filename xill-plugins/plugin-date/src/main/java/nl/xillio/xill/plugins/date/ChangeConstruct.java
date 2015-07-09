package nl.xillio.xill.plugins.date;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
 * Modifies the provided date with the specified changes.
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
		return new ConstructProcessor(ChangeConstruct::process, new Argument("date"), new Argument("change"));
	}

	private static MetaExpression process(final MetaExpression dateVar, final MetaExpression changeVar) {

		if (changeVar == ExpressionBuilder.NULL || dateVar == ExpressionBuilder.NULL) {
			throw new RobotRuntimeException("Input cannot be null.");
		}

		// The changes has to be in a list (or object?).
		if (changeVar.getType() != ExpressionDataType.LIST) {
			throw new RobotRuntimeException("Expected a list for the changes.");
		}

		// The date has to be atomic
		if (dateVar.getType() != ExpressionDataType.ATOMIC) {
			throw new RobotRuntimeException("Expected atomic value for the date.");
		}

		// Change the input string to a date
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date;
		try {
			date = dateFormat.parse(dateVar.getStringValue());
		} catch (ParseException e) {
			throw new RobotRuntimeException("Parse error.");
		}

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);

		@SuppressWarnings("unchecked")
		List<MetaExpression> numberList = (List<MetaExpression>) changeVar.getValue();
		if (numberList.size() > 7) {
			throw new RobotRuntimeException("Number of elements in list is too high. (>7)");
		}
		if (numberList.size() < 7) {
			throw new RobotRuntimeException("Number of elements in list is too low. (<7)");
		}

		cal.add(Calendar.YEAR, numberList.get(0).getNumberValue().intValue());
		cal.add(Calendar.MONTH, numberList.get(1).getNumberValue().intValue());
		cal.add(Calendar.DAY_OF_MONTH, numberList.get(2).getNumberValue().intValue());
		cal.add(Calendar.HOUR_OF_DAY, numberList.get(3).getNumberValue().intValue());
		cal.add(Calendar.MINUTE, numberList.get(4).getNumberValue().intValue());
		cal.add(Calendar.SECOND, numberList.get(5).getNumberValue().intValue());
		cal.add(Calendar.MILLISECOND, numberList.get(6).getNumberValue().intValue());
		return ExpressionBuilder.fromValue(dateFormat.format(cal.getTime()));

	}
}
