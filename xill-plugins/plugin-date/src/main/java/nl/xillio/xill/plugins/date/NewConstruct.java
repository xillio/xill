package nl.xillio.xill.plugins.date;

import java.text.DateFormat;
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
 * Return the date of a unix timestamp. if no parameters are passed, now() is
 * used.
 *
 *
 * @author Sander
 *
 */
public class NewConstruct implements Construct {

	@Override
	public String getName() {

		return "new";
	}

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(NewConstruct::process, new Argument("date", ExpressionBuilder.NULL),
				new Argument("timezone", ExpressionBuilder.NULL));
	}

	private static MetaExpression process(final MetaExpression dateVar, final MetaExpression timezoneVar) {

		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();

		if (dateVar.getType() != ExpressionDataType.ATOMIC || timezoneVar.getType() != ExpressionDataType.ATOMIC) {
			throw new RobotRuntimeException("Expected atomic value.");
		}

		if (timezoneVar != ExpressionBuilder.NULL) {
			dateFormat.setTimeZone(TimeZone.getTimeZone(timezoneVar.getStringValue()));
		}

		if (dateVar != ExpressionBuilder.NULL) {
			date.setTime(dateVar.getNumberValue().longValue());
		}

		return ExpressionBuilder.fromValue(dateFormat.format(date));
	}
}
