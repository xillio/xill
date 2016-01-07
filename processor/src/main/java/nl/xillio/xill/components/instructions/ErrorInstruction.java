package nl.xillio.xill.components.instructions;

import me.biesaart.utils.Log;
import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.components.InstructionFlow;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.Processable;
import nl.xillio.xill.api.construct.ExpressionBuilderHelper;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.debugging.ErrorBlockDebugger;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This {@link Instruction} represents the error mechanism
 */
public class ErrorInstruction extends CompoundInstruction {

    private static final Logger LOGGER = Log.get();
    private final InstructionSet doInstructions;
    private final InstructionSet successInstructions;
    private final InstructionSet errorInstructions;
    private final InstructionSet finallyInstructions;
    private final VariableDeclaration cause;
    private ErrorBlockDebugger errorBlockDebugger;
    private boolean hasReturn = false;

    /**
     * Instantiate an {@link ErrorInstruction}
     */
    public ErrorInstruction(InstructionSet doInstructions, InstructionSet successInstructions, InstructionSet errorInstructions, InstructionSet finallyInstructions, VariableDeclaration cause) {
        this.doInstructions = doInstructions;
        this.successInstructions = successInstructions;
        this.errorInstructions = errorInstructions;
        this.finallyInstructions = finallyInstructions;
        this.cause = cause;

        if (cause != null) {
            cause.setHostInstruction(errorInstructions);
        }

        if (doInstructions != null) {
            doInstructions.setParentInstruction(this);
        }

        if (successInstructions != null) {
            successInstructions.setParentInstruction(this);
        }

        if (errorInstructions != null) {
            errorInstructions.setParentInstruction(this);
        }

        if (finallyInstructions != null) {
            finallyInstructions.setParentInstruction(this);
        }
    }

    @Override
    public InstructionFlow<MetaExpression> process(final Debugger debugger) {
        errorBlockDebugger = new ErrorBlockDebugger();
        errorBlockDebugger.setDebug(debugger); //we need the breakpoints from the old debugger.

        try {
            return tryDoBlock();
        } catch (RobotRuntimeException e) {
            processException(debugger, e);
        } finally {
            processFinally(debugger);
        }

        return InstructionFlow.doResume();
    }

    /**
     * do the finally block.
     *
     * @param debugger the debugger
     */
    private void processFinally(Debugger debugger) {
        //successBlock in finally because exceptions in these blocks should not be caught by errorBlockDebugger
        if (!hasReturn && !errorBlockDebugger.hasError() && successInstructions != null) {
            successInstructions.process(debugger);
        }

        if (finallyInstructions != null) {
            finallyInstructions.process(debugger);
        }
    }

    /**
     * process what happens if a exception is caught.
     *
     * @param debugger the debugger
     * @param e        the exception
     */
    private void processException(Debugger debugger, RobotRuntimeException e) {
        if (errorInstructions != null) {

            if (cause != null) {
                cause.pushVariable(ExpressionBuilderHelper.fromValue(e.getMessage()));
            }
            LOGGER.error("Caught exception in error handler", e);
            errorInstructions.process(debugger);
        }
    }

    /**
     * do the do Block. return the result if there is one.
     *
     * @return
     */
    private InstructionFlow<MetaExpression> tryDoBlock() {
        InstructionFlow<MetaExpression> result = doInstructions.process(errorBlockDebugger);
        hasReturn = result.returns();

        if (hasReturn) {
            return InstructionFlow.doReturn(result.get());
        }

        return InstructionFlow.doResume();
    }

    @Override
    public Collection<Processable> getChildren() {
        List<Processable> children = new ArrayList<>();
        children.add(errorInstructions);
        children.add(doInstructions);
        children.add(finallyInstructions);
        children.add(successInstructions);
        children.add(cause);
        return children;
    }

}
