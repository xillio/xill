package nl.xillio.xill.components.instructions;

import java.util.Arrays;
import java.util.Collection;

import nl.xillio.xill.CodePosition;
import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.components.InstructionFlow;
import nl.xillio.xill.api.components.ExpressionBuilder;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.Processable;
import nl.xillio.xill.api.errors.RobotRuntimeException;

/**
 * This {@link Instruction} represents the start of a variable's lifespan
 */
public class VariableDeclaration extends Instruction {

	private final Processable expression;
	private MetaExpression currentValue;

	/**
	 * Create a new {@link VariableDeclaration}
	 *
	 * @param expression
	 */
	public VariableDeclaration(final Processable expression) {
		this.expression = expression;
	}

	@Override
	public InstructionFlow<MetaExpression> process(final Debugger debugger) throws RobotRuntimeException {
		InstructionFlow<MetaExpression> result = expression.process(debugger);
		
		if(result.hasValue())
			currentValue = result.get();
		else
			currentValue = ExpressionBuilder.NULL;
		
		return InstructionFlow.doResume();
	}

	/**
	 * @return the expression of the variable
	 */
	public MetaExpression getVariable() {
		return currentValue;
	}

	/**
	 * Set the internal value of the variable
	 *
	 * @param value
	 */
	public void setVariable(final MetaExpression value) {
		currentValue = value;
	}

	/**
	 * @param position 
	 * @return A declaration with value {@link ExpressionBuilder#NULL}
	 */
	public static VariableDeclaration nullDeclaration(final CodePosition position) {
		VariableDeclaration dec = new VariableDeclaration(ExpressionBuilder.NULL);
		dec.setPosition(position);

		return dec;
	}

	@Override
	public Collection<Processable> getChildren() {
		return Arrays.asList(expression);
	}

	@Override
	public void close() throws Exception {
		currentValue.close();
	}

}
