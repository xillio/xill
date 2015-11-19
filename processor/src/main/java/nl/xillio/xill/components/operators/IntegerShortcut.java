package nl.xillio.xill.components.operators;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.components.ExpressionBuilder;
import nl.xillio.xill.api.components.InstructionFlow;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.Processable;
import nl.xillio.xill.api.construct.ExpressionBuilderHelper;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.components.instructions.VariableDeclaration;

/**
 * This class represents the + operation.
 */
public final class IntegerShortcut implements Processable {

	private final Assign assign;
	private final boolean returnFirst;
	private final int additiveValue;

	/**
	 * @param variable
	 * @param path
	 * @param value
	 * @param additiveValue
	 * @param returnFirst
	 *        true for suffix mode, false for prefix
	 */
	public IntegerShortcut(final VariableDeclaration variable, final List<Processable> path, final Processable value, final int additiveValue, final boolean returnFirst) {
		this.additiveValue = additiveValue;
		this.returnFirst = returnFirst;
		assign = new Assign(variable, path, new Add(value, new ExpressionBuilder(additiveValue)));
	}

	@Override
	@SuppressWarnings("squid:RedundantThrowsDeclarationCheck")
	// This RobotRuntimeException will not be addressed as it triggers editor specific behaviour.
	public InstructionFlow<MetaExpression> process(final Debugger debugger) throws RobotRuntimeException {
		// Actually assign the value
		MetaExpression assignedValue = assign.processWithValue(debugger);

		// Now we need to determine the return type
		long value = assignedValue.getNumberValue().longValue();

		if (returnFirst) {
			// This is suffix mode so revert the addition
			value -= additiveValue;
		}

		return InstructionFlow.doResume(ExpressionBuilderHelper.fromValue(value));
	}

	@Override
	public Collection<Processable> getChildren() {
		return Arrays.asList(assign);
	}
}
