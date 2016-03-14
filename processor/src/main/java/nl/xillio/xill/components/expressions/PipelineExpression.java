package nl.xillio.xill.components.expressions;

import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.components.InstructionFlow;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.Processable;
import nl.xillio.xill.api.components.WrappingIterator;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.components.instructions.FunctionDeclaration;

import java.util.Arrays;
import java.util.Collection;

import static nl.xillio.xill.api.components.ExpressionBuilderHelper.fromValue;

/**
 * This class represents the base for an iterable to iterable pipeline function.
 *
 * @author Thomas Biesaart
 */
abstract class PipelineExpression implements Processable, FunctionParameterExpression {
    private final Processable input;
    private FunctionDeclaration functionDeclaration;

    public PipelineExpression(Processable input) {
        this.input = input;
    }

    @Override
    public void setFunction(FunctionDeclaration function) {
        this.functionDeclaration = function;
    }

    @Override
    public InstructionFlow<MetaExpression> process(Debugger debugger) throws RobotRuntimeException {
        // Evaluate the input argument
        MetaExpression input = this.input.process(debugger).get();

        // Wrap it
        MetaExpression result = fromValue(String.format("%s(%s)", describe(), input.getStringValue()));
        result.storeMeta(wrap(input, functionDeclaration, debugger));
        return InstructionFlow.doResume(result);
    }

    protected abstract WrappingIterator wrap(MetaExpression input, FunctionDeclaration functionDeclaration, Debugger debugger);

    protected abstract String describe();


    @Override
    public Collection<Processable> getChildren() {
        return Arrays.asList(input, functionDeclaration);
    }
}
