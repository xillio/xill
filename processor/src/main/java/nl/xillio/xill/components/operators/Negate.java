package nl.xillio.xill.components.operators;

import java.util.Arrays;
import java.util.Collection;

import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.components.AtomicExpression;
import nl.xillio.xill.api.components.InstructionFlow;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.Processable;
import nl.xillio.xill.api.errors.RobotRuntimeException;

/**
 * This class represents the || operator
 */
public class Negate implements Processable {

	private final Processable value;

	/**
	 * @param value
	 * @param right
	 */
	public Negate(final Processable value) {
		this.value = value;

	}

	@Override
	public InstructionFlow<MetaExpression> process(final Debugger debugger) throws RobotRuntimeException {
		boolean result = !value.process(debugger).get().getBooleanValue();

		return InstructionFlow.doResume(new AtomicExpression(result));
	}

	@Override
	public Collection<Processable> getChildren() {
		return Arrays.asList(value);
	}

}
