package nl.xillio.xill.components.instructions;

import nl.xillio.xill.CodePosition;
import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.components.InstructionFlow;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.Processable;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.debugging.ErrorBlockDebugger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.Collection;

/**
 * This {@link Instruction} represents the error mechanism
 */
public class ErrorInstruction extends CompoundInstruction {

    private static final Logger LOGGER = LogManager.getLogger();
    private final InstructionSet doInstructions;
    private final InstructionSet successsInstructions;
    private final InstructionSet errorInstructions;
    private final InstructionSet finallyInstructions;

    /**
     * Instantiate an {@link ErrorInstruction}
     */
    public ErrorInstruction(InstructionSet doInstructions, InstructionSet successsInstructions, InstructionSet errorInstructions, InstructionSet finallyInstructions) {
        this.doInstructions = doInstructions;
        this.successsInstructions = successsInstructions;
        this.errorInstructions = errorInstructions;
        this.finallyInstructions = finallyInstructions;

        if (doInstructions != null) {
            doInstructions.setParentInstruction(this);
        }

        if (successsInstructions != null) {
            successsInstructions.setParentInstruction(this);
        }

        if (errorInstructions != null) {
            errorInstructions.setParentInstruction(this);
        }

        if (finallyInstructions != null) {
            finallyInstructions.setParentInstruction(this);
        }
    }

    @Override
    public void setHostInstruction(InstructionSet hostInstruction) {
        super.setHostInstruction(hostInstruction);
    }

    @Override
    public InstructionFlow<MetaExpression> process(final Debugger debugger) throws RobotRuntimeException {

        ErrorBlockDebugger errorBlockDebugger = new ErrorBlockDebugger();
        errorBlockDebugger.setDebug(debugger);

        try {
            doInstructions.process(errorBlockDebugger);

            if (errorBlockDebugger.hasError() && errorInstructions != null) {
                errorInstructions.process(debugger);
            } else {
                if (successsInstructions != null) {
                    successsInstructions.process(debugger);
                }
            }
            return InstructionFlow.doResume();
        } finally {
            if (finallyInstructions != null) {
                finallyInstructions.process(debugger);
            }
        }
    }

    @Override
    public void setPosition(CodePosition position) {
        super.setPosition(position);
    }

    @Override
    public Collection<Processable> getChildren() {
        return Arrays.asList(doInstructions);
    }
}
