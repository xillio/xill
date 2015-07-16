package nl.xillio.xill.plugins.date;

import java.time.ZonedDateTime;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.errors.RobotRuntimeException;

/**
 * This class contains some utility for the constructs to use
 */
public abstract class BaseDateConstruct extends Construct {
	/**
	 * Get the date from a variable
	 * 
	 * @param dateVar
	 *        The expression
	 * @param name
	 *        The name of the parameter
	 * @return
	 */
	protected static ZonedDateTime getDate(final MetaExpression dateVar, final String name) {
	ZonedDateTime date = dateVar.getMeta(ZonedDateTime.class);

	if (date == null) {
		throw new RobotRuntimeException("Expected a date. Create a date using either Date.parse() or Date.of().");
	}

	return date;
	}

	/**
	 * @return the current {@link ZonedDateTime}
	 */
	protected static ZonedDateTime now() {
	return ZonedDateTime.now();
	}

	/**
	 * Create a {@link MetaExpression} from {@link ZonedDateTime}
	 * 
	 * @param date
	 * @return
	 */
	protected static MetaExpression fromValue(final ZonedDateTime date) {
	MetaExpression value = fromValue(date.toString());
	value.storeMeta(ZonedDateTime.class, date);
	return value;
	}

}
