package nl.xillio.xill.plugins.date.constructs;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.plugins.date.BaseDateConstruct;
import nl.xillio.xill.plugins.date.services.DateService;

import org.apache.logging.log4j.Logger;

/**
 *
 *
 * Modifies the provided date with the specified changes from a list.
 *
 * @author Sander
 *
 */
public class ChangeConstruct extends BaseDateConstruct {

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor((date, change) -> process(context.getRootLogger(), date, change, getDateService()), new Argument("date"), new Argument("change", OBJECT));
	}

	@SuppressWarnings("unchecked")
	private static MetaExpression process(final Logger logger, final MetaExpression dateVar, final MetaExpression changeVar, DateService dateService) {
		ZonedDateTime date = getDate(dateVar, "date");

		// First we need the zone
		ZoneId zone = date.getZone();
		Map<String, MetaExpression> map = (Map<String, MetaExpression>) changeVar.getValue();
		if (map.containsKey("zone")) {
			zone = ZoneId.of(map.get("zone").getStringValue());
		} else if (map.containsKey("timezone")) {
			zone = ZoneId.of(map.get("timezone").getStringValue());
		}

		// Change the timezone to the new one
		ZonedDateTime newDate = dateService.changeTimeZone(date, zone);

		Map<ChronoUnit, Long> changes = new HashMap<>();

		// Convert changes
		for (Entry<String, MetaExpression> entry : map.entrySet()) {
			try {
				ChronoUnit unit = ChronoUnit.valueOf(entry.getKey().toUpperCase());
				long value = entry.getValue().getNumberValue().longValue();
				changes.put(unit, value);
			} catch (IllegalArgumentException e) {
				String lower = entry.getKey().toLowerCase();
				if (!(lower.equals("zone") || lower.equals("timezone"))) {
					logger.warn("`" + entry.getKey() + "` is not a valid date change operation.");
				}
			}
		}

		// Add changes
		newDate = dateService.add(newDate, changes);

		return fromValue(newDate);
	}
}
