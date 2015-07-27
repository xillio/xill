package nl.xillio.xill.plugins.date.services;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import com.google.inject.Singleton;

/**
 * Implementation providing {@link DateService} functions.
 * 
 * @author Sander Visser
 * @author Geert Konijnendijk
 *
 */
@Singleton
public class DateServiceImpl implements DateService {

	private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	@Override
	public ZonedDateTime now() {
		return ZonedDateTime.now();
	}

	@Override
	public ZonedDateTime constructDate(int year, int month, int day, int hour, int minute, int second, int nano, ZoneId zone) {
		return ZonedDateTime.of(year, month, day, hour, minute, second, nano, zone);
	}

	@Override
	public ZonedDateTime parseDate(String date, String format) {
		DateTimeFormatter formatter = createDateTimeFormatter(format);
		TemporalAccessor time = formatter.parse(date);

		if (time instanceof ZonedDateTime) {
			return (ZonedDateTime) time;
		}
		else if (time instanceof LocalDateTime) {
			return ZonedDateTime.of((LocalDateTime) time, ZoneId.systemDefault());
		}
		else {
			return LocalDate.from(time).atStartOfDay(ZoneId.systemDefault());
		}
	}

	private DateTimeFormatter createDateTimeFormatter(String format) {
		DateTimeFormatter formatter = format == null ? DateTimeFormatter.ofPattern(format) : DEFAULT_FORMATTER;
		return formatter;
	}

	@Override
	public ZonedDateTime add(ZonedDateTime original, Map<ChronoUnit, Long> toAdd) {
		for (Entry<ChronoUnit, Long> entry : toAdd.entrySet()) {
			original = original.plus(Duration.of(entry.getValue(), entry.getKey()));
		}
		return original;
	}

	@Override
	public ZonedDateTime changeTimeZone(ZonedDateTime original, ZoneId newZone) {
		return original.withZoneSameInstant(newZone);
	}

	@Override
	public String formatDate(ZonedDateTime date, String format) {
		DateTimeFormatter formatter = createDateTimeFormatter(format);
		return formatter.format(date);
	}

	@Override
	public String formatDateLocalized(ZonedDateTime date, FormatStyle dateStyle, FormatStyle timeStyle, Locale locale) {
		DateTimeFormatter formatter = null;
		if (locale == null)
			formatter = DEFAULT_FORMATTER;
		else if (dateStyle != null && timeStyle != null)
			formatter = DateTimeFormatter.ofLocalizedDateTime(dateStyle, timeStyle).withLocale(locale);
		else if (dateStyle == null && timeStyle != null)
			formatter = DateTimeFormatter.ofLocalizedTime(timeStyle).withLocale(locale);
		else if (dateStyle != null && timeStyle == null)
		  formatter = DateTimeFormatter.ofLocalizedDate(dateStyle).withLocale(locale);

		return formatter.format(date);
	}

	@Override
	public ZoneId getTimezone(ZonedDateTime date) {
		return date.getZone();
	}

	@Override
	public boolean isInFuture(ZonedDateTime date) {
		return date.isAfter(now());
	}

	@Override
	public boolean isInPast(ZonedDateTime date) {
		return date.isBefore(now());
	}

	public Map<String, Long> difference(ZonedDateTime date1, ZonedDateTime date2) {

	}

	/**
	 * Represents different kinds of time units, containing their name and the amount of nanoseconds they contain.
	 * 
	 * @author Geert Konijnendijk
	 *
	 */
	private static enum TimeUnits {

		Nanos(10e-9), Micros(10e-6), Millis(10e-3), Seconds(1), Minutes(60), Hours(3600), HalfDays(43200), Days(86400), Weeks(604800), Months(2629746), Years(31556952), Decades(315569520), Centuries(
		    3155695200), Millenia(31556952000), Eras(31556952000000000);

		private double numSeconds;

		/**
		 * 
		 * @param numSecond
		 *        Number of nanoseconds that fit into one unit of this kind
		 */
		private TimeUnits(double numSeconds) {
			this.numSeconds = numSeconds;
		}

		public double getNumSeconds() {
			return numSeconds;
		}

	}

}
