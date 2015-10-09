package nl.xillio.xill.plugins.date.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.date.BaseDateConstruct;
import nl.xillio.xill.api.data.Date;
import nl.xillio.xill.plugins.date.services.DateService;

import java.time.DateTimeException;
import java.time.format.FormatStyle;
import java.util.Locale;

/**
 * converts the date to a string using the provided Language tag if no tag is given the pattern "yyyy-MM-dd HH:mm:ss" is used.
 * If the parameters 'timestyle' and 'datestyle' are both null, it will use the FormatStyle MEDIUM.
 * If only 'timestyle' is null, it will return the date. if only 'datestyle' is null. it will return the time.
 * if both are not null it will return the date and time;
 * <p>
 * timestyle and datestyle have to be 'full','long','medium' or 'short'.
 *
 * @author Sander
 */
public class LocalizedFormatConstruct extends BaseDateConstruct {

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {

		return new ConstructProcessor((dateVar, localeVar, dateStyleVar, timeStyleVar) -> process(dateVar, localeVar, dateStyleVar, timeStyleVar, getDateService()),
						new Argument("date"), new Argument("format", NULL), new Argument("datestyle", NULL),
						new Argument("timestyle", NULL));
	}

	static MetaExpression process(final MetaExpression dateVar,
					final MetaExpression localeVar, final MetaExpression dateStyleVar, final MetaExpression timeStyleVar, DateService dateService) {

		Date date = getDate(dateVar, "date");
		FormatStyle dateStyle;
		FormatStyle timeStyle;

		// no styles are given so we use medium
		if (dateStyleVar.isNull()) {
			dateStyle = FormatStyle.MEDIUM;
		} else {
			try {
				dateStyle = FormatStyle.valueOf(dateStyleVar.getStringValue().toUpperCase());
			} catch (IllegalArgumentException e) {
				throw new RobotRuntimeException("Datestyle has to be 'full','long','medium' or 'short'.");
			}
		}

		if (timeStyleVar.isNull()) {
			timeStyle = FormatStyle.MEDIUM;
		} else {
			try {
				timeStyle = FormatStyle.valueOf(timeStyleVar.getStringValue().toUpperCase());
			} catch (IllegalArgumentException e) {
				throw new RobotRuntimeException("Timestyle has to be 'full','long','medium' or 'short'.");
			}
		}

		Locale locale = localeVar.isNull() ? null : Locale.forLanguageTag(localeVar.getStringValue());
		MetaExpression result = null;
		try {
			result = fromValue(dateService.formatDateLocalized(date, dateStyle, timeStyle, locale));
		} catch (DateTimeException | IllegalArgumentException e) {
			throw new RobotRuntimeException("Date could not be formatted", e);
		}
		return result;

	}
}
