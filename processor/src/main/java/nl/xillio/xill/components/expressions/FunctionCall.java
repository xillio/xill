package nl.xillio.xill.components.expressions;

import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.Xill;
import nl.xillio.xill.api.components.InstructionFlow;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.Processable;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.components.instructions.FunctionDeclaration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This class represents a call of a custom function
 */
public class FunctionCall implements Processable {

    private FunctionDeclaration function;
    private List<Processable> arguments;

    @Override
    public InstructionFlow<MetaExpression> process(final Debugger debugger) throws RobotRuntimeException {
        if (debugger.getStackTrace().size() > Xill.MAX_STACK_SIZE) {
            throw new RobotRuntimeException("The robot went in too many recursions. The language currently supports up to " + Xill.MAX_STACK_SIZE + " recursions");
        }

        // Process arguments
        List<MetaExpression> expressions = new ArrayList<>();

        for (Processable argument : arguments) {
            expressions.add(argument.process(debugger).get());
        }

        return function.run(debugger, expressions);

    }

    /**
     * Initialize this {@link FunctionCall}
     *
     * @param function
     * @param arguments
     */
    public void initialize(final FunctionDeclaration function, final List<Processable> arguments) {
        this.function = function;
        this.arguments = arguments;
    }

    @Override
    public Collection<Processable> getChildren() {
        List<Processable> children = new ArrayList<>(arguments);
        children.add(function);
        return children;
    }

    public FunctionDeclaration getDeclaration() {
        return function;
    }
}
