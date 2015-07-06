package nl.xillio.xill.components.instructions;

import java.util.Arrays;
import java.util.Collection;
import java.util.Stack;

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
	private Stack<MetaExpression> results = new Stack<>();

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
			results.push(expression.process(debugger).get());
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
	public void close() throws Exception {
	    //Close all results
	    while(!results.isEmpty()) {
		try{
		    results.pop().close();
		}catch(Exception e){}
	    }
	}
	
	@Override
	public String toString() {
	return super.toString() + ": " + expression.getClass().getSimpleName();
	}

}
