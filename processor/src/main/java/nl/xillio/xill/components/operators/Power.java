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
 * This class represents the * operator
 */
public class Power implements Processable {

	private final Processable left;
	private final Processable right;

	/**
	 * @param left
	 * @param right
	 */
	public Power(final Processable left, final Processable right) {
		this.left = left;
		this.right = right;
	}

	@Override
	public InstructionFlow<MetaExpression> process(Debugger debugger) throws RobotRuntimeException {

		double result = Math.pow(left.process(debugger).get().getNumberValue().doubleValue(), right.process(debugger).get().getNumberValue().doubleValue());

		return InstructionFlow.doResume(new AtomicExpression(result));
	}

	@Override
	public Collection<Processable> getChildren() {
		return Arrays.asList(left, right);
	}

}
