package nl.xillio.xill.plugins.date.constructs;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.UnsupportedTemporalTypeException;
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
 * Returns detailed info on the specified date.
 *
 * @author Sander
 *
 */
public class InfoConstruct extends BaseDateConstruct {

    @Override
    public String getName() {
	return "info";
    }

    @Override
    public ConstructProcessor prepareProcess(final ConstructContext context) {
	return new ConstructProcessor(InfoConstruct::process, new Argument("date"));

    }

    private static MetaExpression process(final MetaExpression dateVar) {
	ZonedDateTime date = getDate(dateVar, "date");
	ZonedDateTime now = now();

	LinkedHashMap<String, MetaExpression> info = new LinkedHashMap<>();

	for (ChronoField field : ChronoField.values()) {
	    if (date.isSupported(field)) {
		try {
		    info.put(field.toString(), ExpressionBuilder.fromValue(date.get(field)));
		} catch (UnsupportedTemporalTypeException e) {
		    info.put(field.toString(), ExpressionBuilder.fromValue(date.getLong(field)));
		}
	    }
	}

	info.put("TimeZone", ExpressionBuilder.fromValue(date.getZone().toString()));
	info.put("IsInFuture", ExpressionBuilder.fromValue(date.isAfter(now)));
	info.put("IsInPast", ExpressionBuilder.fromValue(date.isBefore(now)));

	return ExpressionBuilder.fromValue(info);

    }
}
