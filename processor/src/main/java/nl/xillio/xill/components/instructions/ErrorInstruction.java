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
    private final InstructionSet successInstructions;
    private final InstructionSet errorInstructions;
    private final InstructionSet finallyInstructions;
    private final VariableDeclaration cause;
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
    public void setHostInstruction(InstructionSet hostInstruction) {
        super.setHostInstruction(hostInstruction);
    }

    @Override
    public InstructionFlow<MetaExpression> process(final Debugger debugger) throws RobotRuntimeException {
        ErrorBlockDebugger errorBlockDebugger = new ErrorBlockDebugger();
        errorBlockDebugger.setDebug(debugger); //we need the breakpoints from the old debugger.

        InstructionFlow<MetaExpression> result = null;

        try {
            result = doInstructions.process(errorBlockDebugger);
            hasReturn = result.returns();

            if (hasReturn) {
                result.get().preventDisposal();
            }

        } catch (RobotRuntimeException e) {
            if (errorBlockDebugger.hasError() && errorInstructions != null) {

                if (cause != null) {
                    cause.pushVariable(ExpressionBuilderHelper.fromValue(e.getMessage()));
                }

                errorInstructions.process(debugger);
            }

        } finally {
            //succesBlock in finally because exceptions in these blocks should not be caught by errorBlockDebugger
            if (!hasReturn && !errorBlockDebugger.hasError() && successInstructions != null) {
                successInstructions.process(debugger);
            }

            if (finallyInstructions != null) {
                finallyInstructions.process(debugger);
            }

            if (hasReturn) {
                InstructionFlow<MetaExpression> meta = InstructionFlow.doReturn(result.get());
                result.get().allowDisposal();
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
        children.add(successInstructions);
        children.add(cause);
        return children;
    }

}
