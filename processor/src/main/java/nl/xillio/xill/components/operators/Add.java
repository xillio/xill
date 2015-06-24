package nl.xillio.xill.components.operators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.components.AtomicExpression;
import nl.xillio.xill.api.components.ExpressionDataType;
import nl.xillio.xill.api.components.InstructionFlow;
import nl.xillio.xill.api.components.ListExpression;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.Processable;
import nl.xillio.xill.api.errors.RobotRuntimeException;

/**
 * This class represents the + operation.
 */
public final class Add implements Processable {

	private final Processable left;
	private final Processable right;

	/**
	 * @param left
	 * @param right
	 */
	public Add(final Processable left, final Processable right) {
		this.left = left;
		this.right = right;
	}

	@SuppressWarnings("unchecked")
	@Override
	public InstructionFlow<MetaExpression> process(Debugger debugger) throws RobotRuntimeException {
		MetaExpression leftValue = left.process(debugger).get();
		MetaExpression rightValue = right.process(debugger).get();

		// If both entries are a list, then add them as such
		if (leftValue.getType() == rightValue.getType() && leftValue.getType() == ExpressionDataType.LIST) {
			return InstructionFlow.doResume(processList((List<MetaExpression>) leftValue.getValue(), (List<MetaExpression>) rightValue.getValue(), debugger));
		}

		return InstructionFlow.doResume(processNumber(leftValue, rightValue));

	}

	private static MetaExpression processNumber(final MetaExpression left, final MetaExpression right) {
		double result = left.getNumberValue().doubleValue() + right.getNumberValue().doubleValue();

		return new AtomicExpression(result);
	}

	private static MetaExpression processList(final List<MetaExpression> leftValue, final List<MetaExpression> rightValue, Debugger debugger) throws RobotRuntimeException {
		List<Processable> result = new ArrayList<>(leftValue);
		result.addAll(rightValue);

		return new ListExpression(result).process(debugger).get();
	}

	@Override
	public Collection<Processable> getChildren() {
		return Arrays.asList(left, right);
	}
}
