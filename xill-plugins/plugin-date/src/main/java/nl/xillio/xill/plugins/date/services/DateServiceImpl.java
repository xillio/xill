package nl.xillio.xill.plugins.date.services;

import com.google.inject.Singleton;
import nl.xillio.xill.plugins.date.data.Date;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Implementation providing {@link DateService} functions.
 *
 * @author Sander Visser
 * @author Geert Konijnendijk
 */
@Singleton
public class DateServiceImpl implements DateService {

	private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	@Override
	public Date now() {
		return new Date(ZonedDateTime.now());
	}

	@Override
	public Date constructDate(int year, int month, int day, int hour, int minute, int second, int nano, ZoneId zone) {
		return new Date(ZonedDateTime.of(year, month, day, hour, minute, second, nano, zone));
	}

	@Override
	public Date parseDate(String date, String format) {
		DateTimeFormatter formatter = createDateTimeFormatter(format);
		TemporalAccessor time = formatter.parse(date);
		ZonedDateTime result;
		if (time instanceof ZonedDateTime) {
			result = (ZonedDateTime) time;
		} else if (time instanceof LocalDateTime) {
			result = ZonedDateTime.of((LocalDateTime) time, ZoneId.systemDefault());
		} else {
			result = LocalDate.from(time).atStartOfDay(ZoneId.systemDefault());
		}

		return new Date(result);
	}

	private DateTimeFormatter createDateTimeFormatter(String format) {
		return format != null ? DateTimeFormatter.ofPattern(format) : DEFAULT_FORMATTER;
	}

	@Override
	public Date add(Date original, Map<ChronoUnit, Long> toAdd) {
		ZonedDateTime value = original.getZoned();
		for (Entry<ChronoUnit, Long> entry : toAdd.entrySet()) {
			value = value.plus(entry.getValue(), entry.getKey());
		}
		return new Date(value);
	}

	@Override
	public Date changeTimeZone(Date original, ZoneId newZone) {
		return new Date(ZonedDateTime.from(original.getZoned().withZoneSameInstant(newZone)));
	}

	@Override
	public String formatDate(Date date, String format) {
		DateTimeFormatter formatter = createDateTimeFormatter(format);
		return formatter.format(date.getZoned());
	}

	@Override
	public String formatDateLocalized(Date date, FormatStyle dateStyle, FormatStyle timeStyle, Locale locale) {
		DateTimeFormatter formatter = null;
		if (locale == null)
			formatter = DEFAULT_FORMATTER;
		else if (dateStyle != null && timeStyle != null)
			formatter = DateTimeFormatter.ofLocalizedDateTime(dateStyle, timeStyle).withLocale(locale);
		else if (dateStyle == null && timeStyle != null)
			formatter = DateTimeFormatter.ofLocalizedTime(timeStyle).withLocale(locale);
		else if (dateStyle != null && timeStyle == null)
			formatter = DateTimeFormatter.ofLocalizedDate(dateStyle).withLocale(locale);
		else
			throw new IllegalArgumentException("No dateStyle or timeStyle was provided");

		return formatter.format(date.getZoned());
	}

	@Override
	public Map<String, Long> getFieldValues(Date date) {
		Map<String, Long> fields = new HashMap<>();
		for (ChronoField field : ChronoField.values()) {
			if (date.getZoned().isSupported(field)) {
				fields.put(field.toString(), date.getZoned().getLong(field));
			}
		}
		return fields;
	}

	@Override
	public ZoneId getTimezone(Date date) {
		return date.getZoned().getZone();
	}

	@Override
	public boolean isInFuture(Date date) {
		return date.getZoned().isAfter(now().getZoned());
	}

	@Override
	public boolean isInPast(Date date) {
		return date.getZoned().isBefore(now().getZoned());
	}

	@Override
	public Map<String, Double> difference(Date date1, Date date2, boolean absolute) {
		// Calculate difference and convert to seconds
		long nanoDifference = date1.getZoned().until(date2.getZoned(), ChronoUnit.NANOS);
		if (absolute)
			nanoDifference = Math.abs(nanoDifference);
		BigDecimal difference = new BigDecimal(nanoDifference).multiply(TimeUnits.Nanos.getNumSeconds());
		// Calculate the totals
		Map<String, Double> diff = new LinkedHashMap<>();
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
	 * <p>
	 * The units should be listed in growing order of length.
	 *
	 * @author Geert Konijnendijk
	 */
	private enum TimeUnits {

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
		 * @param numSeconds Number of seconds that fit into one unit of this kind in {@link BigDecimal} String representation
		 */
		TimeUnits(String numSeconds) {
			this.numSeconds = new BigDecimal(numSeconds);
		}

		public BigDecimal getNumSeconds() {
			return numSeconds;
		}

	}
}