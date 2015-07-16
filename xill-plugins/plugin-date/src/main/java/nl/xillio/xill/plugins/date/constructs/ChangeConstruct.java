package nl.xillio.xill.plugins.date.constructs;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import nl.xillio.xill.api.components.ExpressionDataType;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.date.BaseDateConstruct;

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
	public String getName() {
		return "change";
	}

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor((date, change) -> process(context.getRootLogger(), date, change), new Argument("date"), new Argument("change"));
	}

	@SuppressWarnings("unchecked")
	private static MetaExpression process(final Logger logger, final MetaExpression dateVar, final MetaExpression changeVar) {
		ZonedDateTime date = getDate(dateVar, "date");
		if (changeVar.getType() != ExpressionDataType.OBJECT) {
			throw new RobotRuntimeException("Expected OBJECT value for change");
		}

		// First we need the zone
		ZoneId zone = date.getZone();
		Map<String, MetaExpression> map = (Map<String, MetaExpression>) changeVar.getValue();
		if (map.containsKey("zone")) {
			zone = ZoneId.of(map.get("zone").getStringValue());
		} else if (map.containsKey("timezone")) {
			zone = ZoneId.of(map.get("timezone").getStringValue());
		}

		// Copy the date
		ZonedDateTime newDate = ZonedDateTime.ofInstant(date.toInstant(), zone);

		// Add changes
		for (Entry<String, MetaExpression> entry : map.entrySet()) {
			try {
				ChronoUnit unit = ChronoUnit.valueOf(entry.getKey().toUpperCase());
				long value = entry.getValue().getNumberValue().longValue();
				newDate = newDate.plus(value, unit);
			} catch (IllegalArgumentException e) {
				String lower = entry.getKey().toLowerCase();
				if (!(lower.equals("zone") || lower.equals("timezone"))) {
					logger.warn("`" + entry.getKey() + "` is not a valid date change operation.");
				}
			}
		}

		return fromValue(newDate);
	}
}
