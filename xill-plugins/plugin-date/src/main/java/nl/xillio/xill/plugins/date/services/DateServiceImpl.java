package nl.xillio.xill.plugins.date.services;

import com.google.inject.Singleton;
import me.biesaart.utils.Log;
import nl.xillio.xill.api.data.Date;
import nl.xillio.xill.api.data.DateFactory;
import org.slf4j.Logger;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.Map.Entry;

/**
 * Implementation providing {@link DateService} functions.
 *
 * @author Sander Visser
 * @author Geert Konijnendijk
 */
@Singleton
public class DateServiceImpl implements DateService, DateFactory {

    private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final Logger LOGGER = Log.get();

    @Override
    public Date now() {
        return new nl.xillio.xill.plugins.date.data.Date(ZonedDateTime.now());
    }

    @Override
    public Date constructDate(int year, int month, int day, int hour, int minute, int second, int nano, ZoneId zone) {
        return new nl.xillio.xill.plugins.date.data.Date(ZonedDateTime.of(year, month, day, hour, minute, second, nano, zone));
    }

    ZonedDateTime getValueOrDefaultZDT(TemporalAccessor parsed) {
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime _default = ZonedDateTime.of(now.toLocalDate(), LocalTime.MIN, now.getZone());

        ChronoField[] parameters = {ChronoField.YEAR,
                ChronoField.MONTH_OF_YEAR,
                ChronoField.DAY_OF_MONTH,
                ChronoField.HOUR_OF_DAY,
                ChronoField.MINUTE_OF_HOUR,
                ChronoField.SECOND_OF_MINUTE,
                ChronoField.NANO_OF_SECOND};
        int[] p = Arrays.stream(parameters).mapToInt(cf -> parsed.isSupported(cf) ? parsed.get(cf) : _default.get(cf)).toArray();

        ZoneId zone;
        try {
            zone = ZoneId.from(parsed);
        } catch (DateTimeException dte) {
            zone = ZoneId.systemDefault();
        }

        return ZonedDateTime.of(p[0], p[1], p[2], p[3], p[4], p[5], p[6], zone);
    }

    @Override
    public Date parseDate(String date, String format) {
        DateTimeFormatter formatter = createDateTimeFormatter(format);
        TemporalAccessor parsed = formatter.parse(date);
        return new nl.xillio.xill.plugins.date.data.Date(getValueOrDefaultZDT(parsed));
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
        return new nl.xillio.xill.plugins.date.data.Date(value);
    }

    @Override
    public Date changeTimeZone(Date original, ZoneId newZone) {
        return new nl.xillio.xill.plugins.date.data.Date(ZonedDateTime.from(original.getZoned().withZoneSameInstant(newZone)));
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
        BigDecimal difference = getDifference(date1.getZoned(), date2.getZoned(), absolute);

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

    private BigDecimal getDifference(ZonedDateTime dateA, ZonedDateTime dateB, boolean absolute) {
        try {
            // Calculate difference and convert to seconds
            long nanoDifference = dateA.until(dateB, ChronoUnit.NANOS);
            if (absolute)
                nanoDifference = Math.abs(nanoDifference);
            return new BigDecimal(nanoDifference).multiply(TimeUnits.Nanos.getNumSeconds());
        } catch(ArithmeticException ignore) {
            LOGGER.error("Failed to get difference in nanos for {} and {}", dateA, dateB);
        }

        // If we fail to get difference based on nanos, simply return in seconds
        long secondDifference = dateA.until(dateB, ChronoUnit.SECONDS);
        if(absolute) {
            secondDifference = Math.abs(secondDifference);
        }
        return new BigDecimal(secondDifference);
    }

    @Override
    public nl.xillio.xill.plugins.date.data.Date from(Instant instant) {
        long milli = instant.toEpochMilli();

        return new nl.xillio.xill.plugins.date.data.Date(
                ZonedDateTime.from(Instant.ofEpochMilli(milli).atZone(ZoneId.systemDefault()))
        );
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
