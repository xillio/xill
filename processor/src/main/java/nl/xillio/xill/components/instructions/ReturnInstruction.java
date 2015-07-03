package nl.xillio.xill.components.instructions;

import java.util.Arrays;
import java.util.Collection;
import java.util.NoSuchElementException;

import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.components.ExpressionBuilder;
import nl.xillio.xill.api.components.InstructionFlow;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.Processable;
import nl.xillio.xill.api.errors.RobotRuntimeException;

/**
 * This {@link Instruction} represents the end of a value holding scope.
 */
public class ReturnInstruction extends Instruction {

    private final Processable value;

    /**
     * Create a new {@link ReturnInstruction}
     *
     * @param processable
     */
    public ReturnInstruction(final Processable processable) {
	value = processable;
    }

    /**
     * Create a new null {@link ReturnInstruction}
     */
    public ReturnInstruction() {
	this(null);
    }

    @Override
    public InstructionFlow<MetaExpression> process(final Debugger debugger) throws RobotRuntimeException {
	if (value == null) {
	    return InstructionFlow.doReturn(ExpressionBuilder.NULL);
	}

	try {
	    MetaExpression result = value.process(debugger).get();
	    //Prevent return value from being 
	    result.registerReference();
	    
	    return InstructionFlow.doReturn(result);
	} catch (NoSuchElementException e) {
	    // No value was provided
	    return InstructionFlow.doReturn(ExpressionBuilder.NULL);
	}
    }

    @Override
    public Collection<Processable> getChildren() {
	return Arrays.asList(value);
    }

    @Override
    public void close() throws Exception {
    }
}
