package nl.xillio.xill.plugins.date.constructs;

import java.time.ZonedDateTime;
import java.util.LinkedHashMap;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.plugins.date.BaseDateConstruct;
import nl.xillio.xill.plugins.date.services.DateService;

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
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor((dateVar) -> process(dateVar, getDateService()), new Argument("date"));

	}

	private static MetaExpression process(final MetaExpression dateVar, DateService dateService) {
		ZonedDateTime date = getDate(dateVar, "date");
		ZonedDateTime now = now();

		LinkedHashMap<String, MetaExpression> info = new LinkedHashMap<>();

		// Get ChronoField values
		dateService.getFieldValues(date).forEach((k, v) -> info.put(k, fromValue(v)));

		info.put("TimeZone", fromValue(dateService.getTimezone(date).toString()));
		info.put("IsInFuture", fromValue(dateService.isInFuture(date)));
		info.put("IsInPast", fromValue(dateService.isInPast(date)));

		return fromValue(info);

	}
}
