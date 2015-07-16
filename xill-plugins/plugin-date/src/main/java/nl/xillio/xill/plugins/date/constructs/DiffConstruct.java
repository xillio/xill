package nl.xillio.xill.plugins.date.constructs;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.plugins.date.BaseDateConstruct;

/**
 *
 *
 * Returns the difference between two dates. By default the function will return
 * the absolute difference. Optionally you can set 'absolute' to false to get
 * the relative difference.
 *
 * @author Sander
 *
 */
public class DiffConstruct extends BaseDateConstruct {

	@Override
	public String getName() {

	return "diff";
	}

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
	return new ConstructProcessor(DiffConstruct::process, new Argument("date"), new Argument("other"), new Argument("absolute", TRUE));
	}

	private static MetaExpression process(final MetaExpression dateVar, final MetaExpression otherVar, final MetaExpression absolute) {
	ZonedDateTime date = getDate(dateVar, "date");
	ZonedDateTime other = getDate(otherVar, "other");
	double nanosT = date.until(other, ChronoUnit.NANOS);
	if (absolute.getBooleanValue()) {
		nanosT = Math.abs(nanosT);
	}

	double microsT = nanosT / 1000;
	double millisT = microsT / 1000;
	double secondsT = millisT / 1000;
	double minutesT = secondsT / 60;
	double hoursT = minutesT / 60;
	double daysT = hoursT / 24;
	double weeksT = daysT / 7;
	double yearsT = daysT / 365.2425;
	double monthsT = yearsT * 12;
	double decadesT = yearsT / 10;
	double centuriesT = decadesT / 10;
	double millenniaT = centuriesT / 10;
	double erasT = millenniaT / 1000000;

	int eras = (int) erasT;
	int millennia = (int) (millenniaT - (erasT * 1000000));
	int centuries = (int) (centuriesT - ((int) millenniaT * 10));
	int decades = (int) (decadesT - ((int) centuriesT * 10));
	int years = (int) (yearsT - ((int) decadesT * 10));
	int months = (int) (monthsT - ((int) (yearsT * 12)));
	int days = (int) (daysT - ((int) monthsT * (365.2425 / 12)));
	int hours = (int) (hoursT - ((int) daysT * 24));
	int minutes = (int) (minutesT - ((int) hoursT * 60));
	int seconds = (int) (secondsT - ((int) minutesT * 60));
	int millis = (int) (millisT - ((long) secondsT * 1000));
	int micros = (int) (microsT - ((long) millisT * 1000));
	int nanos = (int) (nanosT - ((long) microsT * 1000));

	LinkedHashMap<String, MetaExpression> result = new LinkedHashMap<>();
	result.put("Total Nanos", fromValue(nanosT));
	result.put("Total Micros", fromValue(microsT));
	result.put("Total Millis", fromValue(millisT));
	result.put("Total Seconds", fromValue(secondsT));
	result.put("Total Minutes", fromValue(minutesT));
	result.put("Total Hours", fromValue(hoursT));
	result.put("Total HalfDays", fromValue(daysT / 2));
	result.put("Total Days", fromValue(daysT));
	result.put("Total Weeks", fromValue(weeksT));
	result.put("Total Months", fromValue(monthsT));
	result.put("Total Years", fromValue(yearsT));
	result.put("Total Decades", fromValue(decadesT));
	result.put("Total Centuries", fromValue(centuriesT));
	result.put("Total Millennia", fromValue(millenniaT));
	result.put("Total Eras", fromValue(erasT));

	result.put("Eras", fromValue(eras));
	result.put("Millennia", fromValue(millennia));
	result.put("Centuries", fromValue(centuries));
	result.put("Decades", fromValue(decades));
	result.put("Years", fromValue(years));
	result.put("Months", fromValue(months));
	result.put("Days", fromValue(days));
	result.put("Hours", fromValue(hours));
	result.put("Minutes", fromValue(minutes));
	result.put("Seconds", fromValue(seconds));
	result.put("Millis", fromValue(millis));
	result.put("Micros", fromValue(micros));
	result.put("Nanos", fromValue(nanos));
	return fromValue(result);

	}
}
