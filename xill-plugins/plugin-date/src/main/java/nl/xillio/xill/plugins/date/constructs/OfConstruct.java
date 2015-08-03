package nl.xillio.xill.plugins.date.constructs;

import java.time.DateTimeException;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.date.BaseDateConstruct;
import nl.xillio.xill.plugins.date.services.DateService;

/**
 *
 *
 * constructs a date from the provided values.
 *
 * @author Sander
 *
 */
public class OfConstruct extends BaseDateConstruct {

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		Argument args[] = {new Argument("year"), new Argument("month"),
		    new Argument("day"), new Argument("hour"),
		    new Argument("minute"), new Argument("second"),
		    new Argument("nano", fromValue(0)), new Argument("zone", fromValue(ZoneId.systemDefault().getId()))};

		return new ConstructProcessor((a) -> process(a, getDateService()), args);
	}

	static MetaExpression process(final MetaExpression[] input, DateService dateService) {
		ZonedDateTime date;
		ZoneId zone;

		for (MetaExpression m : input) {
			assertNotNull(m, "input");
		}

		int year = input[0].getNumberValue().intValue();
		int month = input[1].getNumberValue().intValue();
		int day = input[2].getNumberValue().intValue();
		int hour = input[3].getNumberValue().intValue();
		int minute = input[4].getNumberValue().intValue();
		int second = input[5].getNumberValue().intValue();
		int nano = input[6].getNumberValue().intValue();

		try {
			zone = ZoneId.of(input[7].getStringValue());
		} catch (DateTimeException e) {
			throw new RobotRuntimeException("Invalid zone ID");
		}
		try {
			date = dateService.constructDate(year, month, day, hour, minute, second, nano, zone);
		} catch (DateTimeException e) {
			throw new RobotRuntimeException(e.getLocalizedMessage());
		}

		return fromValue(date);

	}
}
