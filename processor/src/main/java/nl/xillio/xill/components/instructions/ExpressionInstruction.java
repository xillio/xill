package nl.xillio.xill.components.instructions;

import java.util.Arrays;
import java.util.Collection;

import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.components.InstructionFlow;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.Processable;
import nl.xillio.xill.api.errors.RobotRuntimeException;

/**
 * This {@link Instruction} represents any expression on instruction level. (i.e. construct invocations)
 */
public class ExpressionInstruction extends Instruction {

	private final Processable expression;

	/**
	 * Create a new {@link ExpressionInstruction}
	 *
	 * @param expression
	 */
	public ExpressionInstruction(final Processable expression) {
		this.expression = expression;
	}

	@Override
	public InstructionFlow<MetaExpression> process(final Debugger debugger) throws RobotRuntimeException {
		try {
			InstructionFlow<MetaExpression> result = expression.process(debugger);
			
			//We're done with this
			result.get().releaseReference();
		} catch (Exception e) {
			debugger.handle(e);
		}
		return InstructionFlow.doResume();
	}

	@Override
	public Collection<Processable> getChildren() {
		return Arrays.asList(expression);
	}

	@Override
	public void close() throws Exception {}
	
	@Override
	public String toString() {
	return super.toString() + ": " + expression;
	}

}
