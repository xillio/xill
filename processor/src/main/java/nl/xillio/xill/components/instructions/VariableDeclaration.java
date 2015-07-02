package nl.xillio.xill.components.instructions;

import java.util.Arrays;
import java.util.Collection;
import java.util.Stack;

import nl.xillio.xill.CodePosition;
import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.components.ExpressionBuilder;
import nl.xillio.xill.api.components.InstructionFlow;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.Processable;
import nl.xillio.xill.api.errors.RobotRuntimeException;

/**
 * This {@link Instruction} represents the start of a variable's lifespan
 */
public class VariableDeclaration extends Instruction {

    private final Processable expression;
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
	this.expression = expression;
	this.name = name;
    }

    @Override
    public InstructionFlow<MetaExpression> process(final Debugger debugger) throws RobotRuntimeException {
	InstructionFlow<MetaExpression> result = expression.process(debugger);
	
	if (result.hasValue()) {
	    valueStack.push(result.get());
	} else {
	    valueStack.push(ExpressionBuilder.NULL);
	}
	if(name.equals("output"))
	System.out.println("Initialize " + name + ": " + valueStack.peek());
	
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
    public void setVariable(final MetaExpression value) {
	if(name.equals("output"))
	System.out.println("Set " + name + ": " + valueStack.peek());
	valueStack.pop();

	valueStack.push(value);
    }

    /**
     * @param position
     * @param name 
     * @return A declaration with value {@link ExpressionBuilder#NULL}
     */
    public static VariableDeclaration nullDeclaration(final CodePosition position, final String name) {
	VariableDeclaration dec = new VariableDeclaration(ExpressionBuilder.NULL, name);
	dec.setPosition(position);

	return dec;
    }

    @Override
    public Collection<Processable> getChildren() {
	return Arrays.asList(expression);
    }

    @Override
    public void close() throws Exception {
	if(name.equals("output"))
	System.out.println("Close " + name + ": " + valueStack.peek());
	valueStack.peek().close();
	valueStack.pop();
    }

}
