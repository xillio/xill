package nl.xillio.xill.plugins.date;

import com.google.inject.Inject;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.date.data.Date;
import nl.xillio.xill.plugins.date.services.DateService;

import java.time.ZonedDateTime;

/**
 * This class contains some utility for the constructs to use
 */
public abstract class BaseDateConstruct extends Construct {

	/**
	 * Service used by all extending classes
	 */
	@Inject
	private DateService dateService;

	/**
	 * Get the date from a variable
	 *
	 * @param dateVar The expression
	 * @param name    The name of the parameter
	 * @return
	 */
	protected static ZonedDateTime getDate(final MetaExpression dateVar, final String name) {
		Date date = dateVar.getMeta(Date.class);

		if (date == null) {
			throw new RobotRuntimeException("Expected a date. Create a date using either Date.parse() or Date.of().");
		}

		return date.getZoned();
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
		value.storeMeta(Date.class, new Date(date));
		return value;
	}

	public DateService getDateService() {
		return dateService;
	}
}
