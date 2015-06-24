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
 * This class represents the == operator
 */
public class NotEquals implements Processable {

	private final Processable left;
	private final Processable right;

	/**
	 * @param left
	 * @param right
	 */
	public NotEquals(final Processable left, final Processable right) {
		this.left = left;
		this.right = right;

	}

	@Override
	public InstructionFlow<MetaExpression> process(Debugger debugger) throws RobotRuntimeException {
		boolean result = !left.process(debugger).get().equals(right.process(debugger).get());

		return InstructionFlow.doResume(new AtomicExpression(result));
	}

	@Override
	public Collection<Processable> getChildren() {
		return Arrays.asList(left, right);
	}

}
