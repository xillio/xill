package nl.xillio.xill.plugins.date;

import nl.xillio.xill.api.components.ExpressionBuilder;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;

/**
 *
 *
 * Returns the unix timestamp in milliseconds.
 *
 * @author Sander
 *
 */
public class TimestampConstruct implements Construct {

	@Override
	public String getName() {

		return "timestamp";
	}

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(TimestampConstruct::process);
	}

	private static MetaExpression process() {

		return ExpressionBuilder.fromValue(System.currentTimeMillis());

	}
}
