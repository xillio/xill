package nl.xillio.xill.plugins.date.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
import java.util.HashMap;
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
	public Map<String, Long> getFieldValues(ZonedDateTime date) {
		Map<String, Long> fields = new HashMap<>();
		for (ChronoField field : ChronoField.values()) {
			if (date.isSupported(field)) {
				fields.put(field.toString(), date.getLong(field));
			}
		}
		return fields;
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

	public Map<String, Double> difference(ZonedDateTime date1, ZonedDateTime date2, boolean absolute) {
		// Calculate difference and convert to seconds
		long nanoDifference = date1.until(date2, ChronoUnit.NANOS);
		if (absolute)
		  nanoDifference = Math.abs(nanoDifference);
		BigDecimal difference = new BigDecimal(nanoDifference).multiply(TimeUnits.Nanos.getNumSeconds());
		// Calculate the totals
		Map<String, Double> diff = new HashMap<>();
		for (TimeUnits t : TimeUnits.values()) {
			diff.put(String.format("Total %s", t.name()), difference.divide(t.getNumSeconds(), RoundingMode.HALF_UP).doubleValue());
		}
		// Calculate the additive differences by going through the TimeUnits in reverse order and
		for (int i = TimeUnits.values().length - 1; i >= 0; i--) {
			TimeUnits unit = TimeUnits.values()[i];
			BigDecimal[] division = difference.divideAndRemainder(unit.getNumSeconds());
			diff.put(unit.name(), Math.floor(division[0].doubleValue()));
			difference = division[1];
		}
		return diff;
	}

	/**
	 * Represents different kinds of time units, containing their name and the amount of nanoseconds they contain.
	 * 
	 * The units should be listed in growing order of length.
	 * 
	 * @author Geert Konijnendijk
	 *
	 */
	private static enum TimeUnits {

		// @formatter:off
		Nanos("1E-9"),
		Micros("1E-6"),
		Millis("1E-3"),
		Seconds("1"),
		Minutes("60"),
		Hours("3600"),
		HalfDays("43200"),
		Days("86400"),
		Weeks("604800"),
		Months("2629746"),
		Years("31556952"),
		Decades("31556952E1"),
		Centuries("31556952E2"),
		Millenia("31556952E3"),
		Eras("31556952E9");
		// @formatter:on

		private BigDecimal numSeconds;

		/**
		 * 
		 * @param numSecond
		 *        Number of seconds that fit into one unit of this kind in {@link BigDecimal} String representation
		 */
		private TimeUnits(String numSeconds) {
			this.numSeconds = new BigDecimal(numSeconds);
		}

		public BigDecimal getNumSeconds() {
			return numSeconds;
		}

	}
}
