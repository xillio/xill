package nl.xillio.xill.components.instructions;

import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.components.InstructionFlow;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.Processable;
import nl.xillio.xill.api.construct.ExpressionBuilderHelper;
import nl.xillio.xill.api.errors.RobotRuntimeException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * This {@link Instruction} represents the declaration of a custom function
 */
public class FunctionDeclaration extends Instruction {

    private final InstructionSet instructions;
    private final List<VariableDeclaration> parameters;

    /**
     * Create a new {@link FunctionDeclaration}
     *
     * @param instructions
     * @param parameters
     */
    public FunctionDeclaration(final InstructionSet instructions, final List<VariableDeclaration> parameters) {
        this.instructions = instructions;
        this.parameters = parameters;
    }

    @Override
    public InstructionFlow<MetaExpression> process(final Debugger debugger) throws RobotRuntimeException {
        // Nothing to do on process
        // Actual functionality is in run method
        return InstructionFlow.doResume();
    }

    /**
     * Run the actual code
     *
     * @param debugger
     * @param arguments
     * @return The flow result
     * @throws RobotRuntimeException
     */
    public InstructionFlow<MetaExpression> run(final Debugger debugger, final List<MetaExpression> arguments)
            throws RobotRuntimeException {

        // Initiate the parameters
        for (VariableDeclaration parameter : parameters) {
            parameter.process(debugger);
        }

        // Push the new arguments
        Iterator<MetaExpression> argumentItt = arguments.iterator();
        Iterator<VariableDeclaration> parametersItt = parameters.iterator();
        while (argumentItt.hasNext() && parametersItt.hasNext()) {
            MetaExpression expression = argumentItt.next();
            parametersItt.next().replaceVariable(expression);
        }

        // Run the actual code
        InstructionFlow<MetaExpression> result = instructions.process(debugger);

        if (result.hasValue()) {
            result.get().preventDisposal();

            parameters.forEach(VariableDeclaration::releaseVariable);

            result.get().allowDisposal();

            return InstructionFlow.doResume(result.get());
        } else {
            parameters.forEach(VariableDeclaration::releaseVariable);
            return InstructionFlow.doResume(ExpressionBuilderHelper.NULL);
        }

    }

    @Override
    public boolean preventDebugging() {
        // Debugger should not halt on function declarations
        return true;
    }

    @Override
    public Collection<Processable> getChildren() {
        List<Processable> children = new ArrayList<>(parameters);
        children.add(instructions);
        return children;
    }

    @Override
    public void close() throws Exception {
    }

    public int getParametersSize() {
        return parameters.size();
    }

}
