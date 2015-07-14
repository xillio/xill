package nl.xillio.xill.plugins.date.constructs;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;

import nl.xillio.xill.api.components.ExpressionBuilder;
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
	return new ConstructProcessor(DiffConstruct::process, new Argument("date"), new Argument("other"), new Argument("absolute", ExpressionBuilder.TRUE));
    }

    private static MetaExpression process(final MetaExpression dateVar, final MetaExpression otherVar, final MetaExpression absolute) {
	ZonedDateTime date = getDate(dateVar, "date");
	ZonedDateTime other = getDate(otherVar, "other");
	double nanos = date.until(other, ChronoUnit.NANOS);
	if(absolute.getBooleanValue()) {
	    nanos = Math.abs(nanos);
	}

	double micros = nanos / 1000;
	double millis = micros / 1000;
	double seconds = millis / 1000;
	double minutes = seconds / 60;
	double hours = minutes / 60;
	double days = hours / 24;
	double weeks = days / 7;
	double years = days /  365.2425;
	double months = years * 12;
	double decades = years / 10;
	double centuries = decades / 10;
	double millennia = centuries / 10;
	double eras = millennia / 1000000;

	LinkedHashMap<String, MetaExpression> result = new LinkedHashMap<>();
	result.put("Nanos", ExpressionBuilder.fromValue(nanos));
	result.put("Micros", ExpressionBuilder.fromValue(micros));
	result.put("Millis", ExpressionBuilder.fromValue(millis));
	result.put("Seconds", ExpressionBuilder.fromValue(seconds));
	result.put("Minutes", ExpressionBuilder.fromValue(minutes));
	result.put("Hours", ExpressionBuilder.fromValue(hours));
	result.put("HalfDays", ExpressionBuilder.fromValue(days / 2));
	result.put("Days", ExpressionBuilder.fromValue(days));
	result.put("Weeks", ExpressionBuilder.fromValue(weeks));
	result.put("Months", ExpressionBuilder.fromValue(months));
	result.put("Years", ExpressionBuilder.fromValue(years));
	result.put("Decades", ExpressionBuilder.fromValue(decades));
	result.put("Centuries", ExpressionBuilder.fromValue(centuries));
	result.put("Millennia", ExpressionBuilder.fromValue(millennia));
	result.put("Eras", ExpressionBuilder.fromValue(eras));
	
	
	return ExpressionBuilder.fromValue(result);

    }
}
