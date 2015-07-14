package nl.xillio.xill.plugins.system;

import nl.xillio.xill.api.components.ExpressionBuilder;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;

/**
 * Pauses the execution of instructions for the specified amount of milliseconds.
 */
public class WaitConstruct implements Construct {

	@Override
	public String getName() {
		return "wait";
	}

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(
			WaitConstruct::process,
		new Argument("delay"));
	}

	private static MetaExpression process(final MetaExpression delayVar) {
		int delay = delayVar.getNumberValue().intValue();
		try {
			Thread.sleep(delay);
		} catch (Exception e) {
			throw new RobotRuntimeException("Error during the pause", e);
		}

		return ExpressionBuilder.NULL;
	}
}
