package nl.xillio.xill.plugins.date.constructs;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.date.BaseDateConstruct;

/**
 *
 *
 * converts the date to a string using the provided locale if no locale is given the pattern "yyyy-MM-dd HH:mm:ss" is used.
 *
 * @author Sander
 *
 */
public class FormatLocaleConstruct extends BaseDateConstruct {
	private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	@Override
	public String getName() {

	return "formatLocale";
	}

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {

	return new ConstructProcessor(FormatLocaleConstruct::process,
		new Argument("date"), new Argument("format", NULL), new Argument("datestyle", NULL),
		new Argument("timestyle", NULL));
	}

	private static MetaExpression process(final MetaExpression dateVar,
		final MetaExpression localeVar, final MetaExpression dateStyleVar, final MetaExpression timeStyleVar) {

	ZonedDateTime date = getDate(dateVar, "date");
	DateTimeFormatter formatter;
	FormatStyle dateStyle;
	FormatStyle timeStyle;

	if (localeVar != NULL) {
		// no styles are given so we use medium
		if (dateStyleVar == NULL) {
		dateStyle = FormatStyle.MEDIUM;
		} else {
		try {
			dateStyle = FormatStyle.valueOf(dateStyleVar.getStringValue().toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new RobotRuntimeException("Datestyle has to be 'full','long','medium' or 'short'.");
		}
		}

		if (timeStyleVar == NULL) {
		timeStyle = FormatStyle.MEDIUM;
		} else {
		try {
			timeStyle = FormatStyle.valueOf(timeStyleVar.getStringValue().toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new RobotRuntimeException("Timestyle has to be 'full','long','medium' or 'short'.");
		}
		}

		if (dateStyleVar == NULL && timeStyleVar != NULL) {
		// only timestyle given so use localizedTime
		formatter = DateTimeFormatter.ofLocalizedTime(timeStyle)
			.withLocale(Locale.forLanguageTag(localeVar.getStringValue()));

		} else if (timeStyleVar == NULL && dateStyleVar != NULL) {
		// only datestyle given so use localizedDate
		formatter = DateTimeFormatter.ofLocalizedDate(dateStyle)
			.withLocale(Locale.forLanguageTag(localeVar.getStringValue()));
		} else {
		// datestyle and timestyle are either set to medium or given
		formatter = DateTimeFormatter.ofLocalizedDateTime(dateStyle, timeStyle)
			.withLocale(Locale.forLanguageTag(localeVar.getStringValue()));
		}
	} else {
		formatter = DEFAULT_FORMATTER;
	}

	String s = "";
	s = date.format(formatter);

	MetaExpression result = fromValue(s);
	return result;

	}
}
