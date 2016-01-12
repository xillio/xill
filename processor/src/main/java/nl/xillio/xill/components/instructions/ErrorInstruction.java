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

    /**
     * start processing the blocks.
     *
     * @param debugger The debugger that should be used when processing this
     * @return
     */
    @Override
    public InstructionFlow<MetaExpression> process(final Debugger debugger) {
        ErrorBlockDebugger errorBlockDebugger = new ErrorBlockDebugger();
        return process(debugger, errorBlockDebugger);
    }

    public InstructionFlow<MetaExpression> process(final Debugger debugger, final ErrorBlockDebugger errorBlockDebugger) {
        errorBlockDebugger.setDebug(debugger); //we need the breakpoints from the old debugger.

        InstructionFlow<MetaExpression> result = doInstructions.process(errorBlockDebugger);

        if (result.hasValue()) {
            result.get().preventDisposal();
        }

        if (errorBlockDebugger.hasError()) {
            processException(debugger, errorBlockDebugger.getError());
        }

        processFinally(debugger, errorBlockDebugger.hasError());

        if (result.hasValue()) {
            result.get().allowDisposal();
        }

        return result;
    }

    /**
     * do the finally block.
     *
     * @param debugger the debugger
     */
    private void processFinally(Debugger debugger, boolean hadError) {
        //successBlock in finally because exceptions in these blocks should not be caught by errorBlockDebugger

        if (!hadError && successInstructions != null) {
            InstructionFlow<MetaExpression> result = successInstructions.process(debugger);
            checkFlowValues(result, "success");
        }

        if (finallyInstructions != null && finallyInstructions.process(debugger).hasValue()) {
            InstructionFlow<MetaExpression> result = finallyInstructions.process(debugger);
            checkFlowValues(result, "finally");
        }
    }

    private void checkFlowValues(InstructionFlow<MetaExpression> result, String blockName) {
        if(result.returns()) {
            throw new RobotRuntimeException("A return is not allowed in the " + blockName + " block.");
        }

        if(result.breaks()) {
            throw new RobotRuntimeException("A break is not allowed in the " + blockName + " block.");
        }

        if(result.skips()) {
            throw new RobotRuntimeException("A continue is not allowed in the " + blockName + " block.");
        }
    }

    /**
     * process what happens if a exception is caught.
     *
     * @param debugger the debugger
     * @param e        the exception
     */
    private void processException(Debugger debugger, Throwable e) {
        if (errorInstructions != null) {

            if (cause != null) {
                cause.pushVariable(ExpressionBuilderHelper.fromValue(e.getMessage()));
            }
            LOGGER.error("Caught exception in error handler", e);

            InstructionFlow<MetaExpression> result = errorInstructions.process(debugger);
            checkFlowValues(result, "error");
        }
    }

    @Override
    public Collection<Processable> getChildren() {
        List<Processable> children = new ArrayList<>();
        if (errorInstructions != null) {
            children.add(errorInstructions);
        }
        if (errorInstructions != null) {
            children.add(doInstructions);
        }
        if (errorInstructions != null) {
            children.add(finallyInstructions);
        }
        if (errorInstructions != null) {
            children.add(successInstructions);
        }
        if (errorInstructions != null) {
            children.add(cause);
        }
        return children;
    }

}
