package nl.xillio.xill.plugins.date.services;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.Locale;
import java.util.Map;

import nl.xillio.xill.services.XillService;

/**
 * 
 * Service implementing all functionality needed by the date plugin.
 * 
 * @author Geert Konijnendijk
 *
 */
public interface DateService extends XillService {

	/**
	 * @return The current date
	 */
	ZonedDateTime now();

	/**
	 * Create a new {@link ZonedDateTime}
	 * 
	 * @param zone
	 *        Timezone to be used for the new date
	 * @return {@link ZonedDateTime} from the given parameters
	 */
	ZonedDateTime constructDate(int year, int month, int day, int hour, int minute, int second, int nano, ZoneId zone);

	/**
	 * Parse a {@link ZonedDateTime} from a String.
	 * 
	 * @param date
	 *        The String to parse from.
	 * @param format
	 *        The format String according to
	 * @return A new {@link ZonedDateTime}
	 */
	ZonedDateTime parseDate(String date, String format);

	/**
	 * 
	 * Adds a number of different time units to a {@link ZonedDateTime}.
	 * 
	 * @param original
	 *        The orginal date to add to
	 * @param toAdd
	 *        A map containing {@link ChronoUnit ChronoUnits} as key and the amount of the unit to add as a value.
	 * @return The resulting time
	 */
	ZonedDateTime add(ChronoZonedDateTime<?> original, Map<ChronoUnit, Long> toAdd);

	/**
	 * Change the timezone of a {@link ZonedDateTime}
	 * 
	 * @param original
	 *        Original date
	 * @param newZone
	 *        Timezone to chenge to
	 * @return A new {@link ZonedDateTime}
	 */
	ZonedDateTime changeTimeZone(ChronoZonedDateTime<?> original, ZoneId newZone);

	/**
	 * Format a {@link ZonedDateTime} as a String
	 * 
	 * @param date
	 *        The date to format
	 * @param format
	 *        Format string according to {@link DateTimeFormatter#ofPattern(String)}
	 * @return The formatted date
	 */
	String formatDate(ChronoZonedDateTime<?> date, String format);

	/**
	 * Format a {@link ZonedDateTime} for a given locale
	 * 
	 * @param date
	 *        Date to format
	 * @param dateStyle
	 *        Length of the date, can be null
	 * @param timeStyle
	 *        Length of the time, can be null
	 * @param locale
	 *        Locale to format for, can be null
	 * @return The formatted date
	 */
	String formatDateLocalized(ChronoZonedDateTime<?> date, FormatStyle dateStyle, FormatStyle timeStyle, Locale locale);

	/**
	 * @param date
	 *        The date to process
	 * @return A map with the names of all supported {@link ChronoField ChronoFields} as keys and their value as values.
	 */
	Map<String, Long> getFieldValues(ChronoZonedDateTime<?> date);

	/**
	 * Get the timezone of the date
	 * 
	 * @param date
	 *        Date to get the timezone from
	 * @return The timezone of the date
	 */
	ZoneId getTimezone(ChronoZonedDateTime<?> date);

	/**
	 * Tests if the date is in the future
	 * 
	 * @param date
	 *        Date to test
	 * @return True if the date is in the future
	 */
	boolean isInFuture(ChronoZonedDateTime<?> date);

	/**
	 * Tests if the date is in the past
	 * 
	 * @param date
	 *        Date to test
	 * @return True if the date is in the past
	 */
	boolean isInPast(ChronoZonedDateTime<?> date);

	/**
	 * Calculates the difference between two dates
	 * 
	 * @param date1
	 *        First comparison date
	 * @param date2
	 *        Second comparison date
	 * @return A map containing names of time units as keys (first letter capitlized and in plural) and the amount of this time unit as value. The amount is always positive. When the key has "Total "
	 *         prepended, the value is not additive.
	 */
	public Map<String, Double> difference(Temporal date1, Temporal date2, boolean absolute);

}
