package nl.xillio.xill.components.operators;

import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.components.AtomicExpression;
import nl.xillio.xill.api.components.InstructionFlow;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.Processable;
import nl.xillio.xill.api.errors.RobotRuntimeException;

import java.util.Arrays;
import java.util.Collection;

/**
 * This class represents the &amp;&amp; operator.
 */
public final class And implements Processable {

	private final Processable left;
	private final Processable right;

	/**
	 * @param left
	 * @param right
	 */
	public And(final Processable left, final Processable right) {
		this.left = left;
		this.right = right;
	}

	@Override
	public InstructionFlow<MetaExpression> process(final Debugger debugger) throws RobotRuntimeException {
		boolean result = left.process(debugger).get().getBooleanValue() && right.process(debugger).get().getBooleanValue();

		return InstructionFlow.doResume(new AtomicExpression(result));
	}

	@Override
	public Collection<Processable> getChildren() {
		return Arrays.asList(left, right);
	}

}
