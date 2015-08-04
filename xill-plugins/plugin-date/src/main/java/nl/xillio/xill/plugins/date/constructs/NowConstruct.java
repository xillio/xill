package nl.xillio.xill.plugins.date.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.plugins.date.BaseDateConstruct;
import nl.xillio.xill.plugins.date.services.DateService;

/**
 *
 *
 * Returns the current time
 *
 * @author Sander
 *
 */
public class NowConstruct extends BaseDateConstruct {

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(() -> process(getDateService()));
	}

	static MetaExpression process(DateService dateService) {
		return fromValue(dateService.now());
	}
}
