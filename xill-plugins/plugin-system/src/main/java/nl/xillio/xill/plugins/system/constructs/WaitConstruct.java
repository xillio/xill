package nl.xillio.xill.plugins.system.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;

/**
 * Pauses the execution of instructions for the specified amount of
 * milliseconds.
 */
public class WaitConstruct extends Construct {

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(WaitConstruct::process, new Argument("delay", fromValue(100)));
	}

	private static MetaExpression process(final MetaExpression delayVar) {
		int delay = delayVar.getNumberValue().intValue();
		try {
			Thread.sleep(delay);
		} catch (Exception e) {
			throw new RobotRuntimeException("Error during the pause", e);
		}

		return NULL;
	}
}
