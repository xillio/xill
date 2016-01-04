package nl.xillio.xill.components.instructions;

import nl.xillio.xill.CodePosition;
import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.components.InstructionFlow;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.Processable;
import nl.xillio.xill.api.construct.ExpressionBuilderHelper;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.debugging.ErrorBlockDebugger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This {@link Instruction} represents the error mechanism
 */
public class ErrorInstruction extends CompoundInstruction {


    private final InstructionSet doInstructions;
    private final InstructionSet successsInstructions;
    private final InstructionSet errorInstructions;
    private final InstructionSet finallyInstructions;
    private final VariableDeclaration cause;
    private boolean hasReturn = false;

    /**
     * Instantiate an {@link ErrorInstruction}
     */
    public ErrorInstruction(InstructionSet doInstructions, InstructionSet successsInstructions, InstructionSet errorInstructions, InstructionSet finallyInstructions, VariableDeclaration cause) {
        this.doInstructions = doInstructions;
        this.successsInstructions = successsInstructions;
        this.errorInstructions = errorInstructions;
        this.finallyInstructions = finallyInstructions;
        this.cause = cause;

        if (cause != null) {
            cause.setHostInstruction(errorInstructions);
        }

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
        InstructionFlow<MetaExpression> result = null;

        try {
            result = doInstructions.process(errorBlockDebugger);
            hasReturn = result.returns();
            if (hasReturn) {
                result.get().registerReference();
            }

            if (!hasReturn && successsInstructions != null) {
                return successsInstructions.process(debugger);
            }

        } catch (RobotRuntimeException e) {
            if (errorBlockDebugger.hasError() && errorInstructions != null) {
                if (cause != null) {
                    cause.pushVariable(ExpressionBuilderHelper.fromValue(e.getMessage()));
                }
                return errorInstructions.process(debugger);
            }

        } finally {
            if (finallyInstructions != null) {
                finallyInstructions.process(debugger);

            }
            if (hasReturn) {
                InstructionFlow<MetaExpression> meta = InstructionFlow.doReturn(result.get());
                return meta;
            }

        }

        return InstructionFlow.doResume();
    }

    @Override
    public void setPosition(CodePosition position) {
        super.setPosition(position);
    }


    @Override
    public Collection<Processable> getChildren() {
        List<Processable> children = new ArrayList<>();
        children.add(errorInstructions);
        children.add(doInstructions);
        children.add(finallyInstructions);
        children.add(successsInstructions);
        children.add(cause);
        return children;
    }

}
