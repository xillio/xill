package nl.xillio.xill.components.instructions;

import nl.xillio.xill.CodePosition;
import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.components.ExpressionBuilder;
import nl.xillio.xill.api.components.InstructionFlow;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.Processable;
import nl.xillio.xill.api.construct.ExpressionBuilderHelper;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.components.operators.Assign;

import java.util.Arrays;
import java.util.Collection;
import java.util.Stack;

/**
 * This {@link Instruction} represents the start of a variable's lifespan
 */
public class VariableDeclaration extends Instruction {
	private final Processable assignation;
	private final Stack<MetaExpression> valueStack = new Stack<>();
	/**
	 * This is here for debugging purposes
	 */
	private final String name;

	/**
	 * Create a new {@link VariableDeclaration}
	 *
	 * @param expression
	 * @param name
	 */
	public VariableDeclaration(final Processable expression, final String name) {
		assignation = new Assign(this, Arrays.asList(), expression);
		this.name = name;

	}

	@Override
	public InstructionFlow<MetaExpression> process(final Debugger debugger) throws RobotRuntimeException {
		pushVariable(ExpressionBuilderHelper.NULL);
		assignation.process(debugger);

		return InstructionFlow.doResume();
	}

	/**
	 * @return the expression of the variable
	 */
	public MetaExpression getVariable() {
		return valueStack.peek();
	}

	/**
	 * Set the value of the variable
	 *
	 * @param value
	 */
	public void replaceVariable(final MetaExpression value) {
		MetaExpression current = valueStack.pop();
		pushVariable(value);
		current.releaseReference();
	}

	/**
	 * Set the value of the variable without popping the last one
	 *
	 * @param value
	 */
	public void pushVariable(final MetaExpression value) {
		value.registerReference();
		valueStack.push(value);
	}

	/**
	 * Release the current variable.
	 *
	 */
	public void releaseVariable() {
		valueStack.pop().releaseReference();
	}

	/**
	 * @param position
	 * @param name
	 * @return A declaration with value {@link ExpressionBuilder#NULL}
	 */
	public static VariableDeclaration nullDeclaration(final CodePosition position, final String name) {
		VariableDeclaration dec = new VariableDeclaration(ExpressionBuilderHelper.NULL, name);
		dec.setPosition(position);

		return dec;
	}

	@Override
	public Collection<Processable> getChildren() {
		return Arrays.asList(assignation);
	}

	@Override
	public void close() throws Exception {
		releaseVariable();
	}

	/**
	 * This name is for debugging purposes and is <b>NOT UNIQUE</b>.
	 * Do not use as identifier
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

}
