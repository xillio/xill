package nl.xillio.xill.plugins.date.constructs;

import java.time.ZonedDateTime;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.plugins.date.BaseDateConstruct;

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
    public String getName() {
	return "now";
    }

    @Override
    public ConstructProcessor prepareProcess(final ConstructContext context) {
	return new ConstructProcessor(NowConstruct::process);
    }

    private static MetaExpression process() {
	return expression(ZonedDateTime.now());

    }
}
