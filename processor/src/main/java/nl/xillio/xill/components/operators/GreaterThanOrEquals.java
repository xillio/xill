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
 * This class represents the &gt;= operator
 */
public final class GreaterThanOrEquals implements Processable {

	private final Processable left;
	private final Processable right;

	/**
	 * @param left
	 * @param right
	 */
	public GreaterThanOrEquals(final Processable left, final Processable right) {
		this.left = left;
		this.right = right;
	}

	@Override
	public InstructionFlow<MetaExpression> process(final Debugger debugger) throws RobotRuntimeException {
		MetaExpression leftValue = left.process(debugger).get();
		MetaExpression rightValue = right.process(debugger).get();

		boolean result = false;

		if (Double.isNaN(leftValue.getNumberValue().doubleValue()) || Double.isNaN(rightValue.getNumberValue().doubleValue())) {
			// Compare strings
			result = leftValue.getStringValue().compareToIgnoreCase(rightValue.getStringValue()) >= 0;
		} else {
			// Compare numbers
			result = leftValue.getNumberValue().doubleValue() >= rightValue.getNumberValue().doubleValue();
		}

		return InstructionFlow.doResume(new AtomicExpression(result));
	}

	@Override
	public Collection<Processable> getChildren() {
		return Arrays.asList(left, right);
	}
}
