package nl.xillio.xill.plugins.date.constructs;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQueries;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.date.BaseDateConstruct;

/**
 *
 * Returns a Date. If no parameters are passed, now() is used. The default
 * format for string date values is ISO. Optionally a different format can be
 * passed as second parameter.
 *
 *
 *
 * @author Sander
 *
 */
public class ParseConstruct extends BaseDateConstruct {
	private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {

		return new ConstructProcessor(ParseConstruct::process, new Argument("date", NULL, ATOMIC),
			new Argument("format", NULL, ATOMIC));
	}

	private static MetaExpression process(final MetaExpression dateVar, final MetaExpression formatVar) {
		// Process
		ZonedDateTime result = null;

		if (dateVar == NULL) {
			result = ZonedDateTime.now();
		} else {

			try {
				result = parseAsDate(dateVar, formatVar);
			} catch (DateTimeParseException e) {
				throw new RobotRuntimeException("Could not parse date: " + e.getLocalizedMessage());
			}
		}

		return fromValue(result);
	}

	private static ZonedDateTime parseAsDate(final MetaExpression dateVar, final MetaExpression formatVar)
					throws DateTimeParseException {

		if (formatVar != NULL) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formatVar.getStringValue());
			TemporalAccessor time = formatter.parse(dateVar.getStringValue());

			if (time instanceof ZonedDateTime) {
				return (ZonedDateTime) time;
			}

			if (time instanceof LocalDateTime) {
				return ZonedDateTime.of((LocalDateTime) time, ZoneId.systemDefault());
			}

			if (time instanceof LocalDate) {
				return ((LocalDate) time).atStartOfDay(ZoneId.systemDefault());
			}

			// We had something weird so try to parse outselves
			return parseFromScratch(time);
		}

		LocalDateTime local = LocalDateTime.parse(dateVar.getStringValue(), DEFAULT_FORMATTER);
		return ZonedDateTime.of(local, ZoneId.systemDefault());

	}

	private static ZonedDateTime parseFromScratch(final TemporalAccessor time) {
		int year = Year.now().getValue();
		int month = 1;
		int day = 1;
		int hour = 0;
		int minute = 0;
		int second = 0;
		int nano = 0;
		ZoneId zone = ZoneId.systemDefault();

		for (ChronoField field : ChronoField.values()) {
			if (time.isSupported(field)) {
				switch (field) {
					case DAY_OF_MONTH:
						day = time.get(field);
						break;
					case HOUR_OF_DAY:
						hour = time.get(field);
						break;
					case MINUTE_OF_HOUR:
						minute = time.get(field);
						break;
					case MONTH_OF_YEAR:
						month = time.get(field);
						break;
					case NANO_OF_SECOND:
						nano = time.get(field);
						break;
					case SECOND_OF_MINUTE:
						second = time.get(field);
						break;
					case YEAR:
						year = time.get(field);
						break;
					default:
						break;
				}
			}
		}

		ZoneId savedZone = time.query(TemporalQueries.zone());
		if (savedZone != null) {
			zone = savedZone;
		}

		return ZonedDateTime.of(year, month, day, hour, minute, second, nano, zone);
	}
}
