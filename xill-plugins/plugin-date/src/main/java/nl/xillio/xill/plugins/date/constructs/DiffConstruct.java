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
	double nanos = date.until(other, ChronoUnit.NANOS);
	if (absolute.getBooleanValue()) {
		nanos = Math.abs(nanos);
	}

	double micros = nanos / 1000;
	double millis = micros / 1000;
	double seconds = millis / 1000;
	double minutes = seconds / 60;
	double hours = minutes / 60;
	double days = hours / 24;
	double weeks = days / 7;
	double years = days / 365.2425;
	double months = years * 12;
	double decades = years / 10;
	double centuries = decades / 10;
	double millennia = centuries / 10;
	double eras = millennia / 1000000;

	LinkedHashMap<String, MetaExpression> result = new LinkedHashMap<>();
	result.put("Nanos", fromValue(nanos));
	result.put("Micros", fromValue(micros));
	result.put("Millis", fromValue(millis));
	result.put("Seconds", fromValue(seconds));
	result.put("Minutes", fromValue(minutes));
	result.put("Hours", fromValue(hours));
	result.put("HalfDays", fromValue(days / 2));
	result.put("Days", fromValue(days));
	result.put("Weeks", fromValue(weeks));
	result.put("Months", fromValue(months));
	result.put("Years", fromValue(years));
	result.put("Decades", fromValue(decades));
	result.put("Centuries", fromValue(centuries));
	result.put("Millennia", fromValue(millennia));
	result.put("Eras", fromValue(eras));

	return fromValue(result);

	}
}
