package nl.xillio.xill.components.expressions;

import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.components.InstructionFlow;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.MetaExpressionIterator;
import nl.xillio.xill.api.components.Processable;
import nl.xillio.xill.api.construct.ExpressionBuilderHelper;
import nl.xillio.xill.api.errors.NotImplementedException;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.components.instructions.FunctionDeclaration;

import java.util.*;

/**
 * @author Pieter Soels
 *
 * This class is for abstracting methods from the map- and filterexpression classes.
 */
public abstract class MapFilterHandler implements Processable, FunctionParameterExpression {
    protected FunctionDeclaration functionDeclaration;
    protected final Processable argument;

    public MapFilterHandler(final Processable argument) {
        this.argument = argument;
    }

    /**
     * Start of instructions for the filter expression.
     */
    @Override
    public InstructionFlow<MetaExpression> process(final Debugger debugger) throws RobotRuntimeException {
        // Call the function for the argument
        MetaExpression result = argument.process(debugger).get();

        try {
            result.registerReference();
            return process(result, debugger);
        } catch (ConcurrentModificationException e) {
            throw new RobotRuntimeException("You can not change the expression upon which you are iterating.", e);
        } finally {
            result.releaseReference();
        }
    }

    /**
     * Depending on type of MetaExpression (result) process individually.
     */
    private InstructionFlow<MetaExpression> process(MetaExpression result, Debugger debugger) {
        switch (result.getType()) {
            case ATOMIC:
                if (result.isNull()) {
                    return InstructionFlow.doResume(ExpressionBuilderHelper.emptyList());
                }
                if (result.getMeta(MetaExpressionIterator.class) == null){
                    return atomicProcessNoIterator(result, debugger);
                } else {
                    return atomicProcessIterator(result, debugger);
                }
            case LIST:
                return listProcess(result, debugger);
            case OBJECT:
                return objectProcess(result, debugger);
            default:
                throw new NotImplementedException("This MetaExpression has not been defined yet.");
        }
    }



    protected abstract InstructionFlow<MetaExpression> atomicProcessNoIterator(MetaExpression result, Debugger debugger);
    protected abstract InstructionFlow<MetaExpression> atomicProcessIterator(MetaExpression result, Debugger debugger);
    protected abstract InstructionFlow<MetaExpression> listProcess(MetaExpression result, Debugger debugger);
    protected abstract InstructionFlow<MetaExpression> objectProcess(MetaExpression result, Debugger debugger);






    /**
     * Set the function parameter
     *
     * @param functionDeclaration
     */
    @Override
    public void setFunction(final FunctionDeclaration functionDeclaration) {
        this.functionDeclaration = functionDeclaration;
    }
}