package nl.xillio.xill.plugins.date.constructs;

import java.time.DateTimeException;
import java.time.ZonedDateTime;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.date.BaseDateConstruct;
import nl.xillio.xill.plugins.date.services.DateService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * Returns a Date. If no parameters are passed, now() is used. The default
 * format for string date values is ISO. Optionally a different format can be
 * passed as second parameter.
 *
 *
 *
 * @author Sander
 *
 */
public class ParseConstruct extends BaseDateConstruct {

	private static final Logger log = LogManager.getLogger();

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {

		return new ConstructProcessor((dateVar, formatVar) -> process(dateVar, formatVar, getDateService()), new Argument("date", NULL, ATOMIC),
		  new Argument("format", NULL, ATOMIC));
	}

	private static MetaExpression process(final MetaExpression dateVar, final MetaExpression formatVar, DateService dateService) {
		// Process
		ZonedDateTime result = null;

		if (dateVar == NULL) {
			result = dateService.now();
		} else {

			try {
				String formatString = formatVar.isNull() ? null : formatVar.getStringValue();
				result = dateService.parseDate(dateVar.getStringValue(), formatString);
			} catch (DateTimeException | IllegalArgumentException e) {
				log.error("Exception while parsing date", e);
				throw new RobotRuntimeException(String.format("Could not parse date %s", e.getMessage()), e);
			}
		}

		return fromValue(result);
	}
}
