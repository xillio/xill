package nl.xillio.xill.components.expressions;

import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.components.InstructionFlow;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.Processable;
import nl.xillio.xill.components.instructions.VariableDeclaration;

import java.util.Arrays;
import java.util.Collection;

/**
 * This expression represents the accessing of a variable.
 */
public class VariableAccessExpression implements Processable {

    private final VariableDeclaration declaration;

    /**
     * Create a new {@link VariableAccessExpression} what will access provided
     * declaration.
     *
     * @param declaration    the provided declaration.
     */
    public VariableAccessExpression(final VariableDeclaration declaration) {
        this.declaration = declaration;
    }

    @Override
    public InstructionFlow<MetaExpression> process(final Debugger debugger) {
        return InstructionFlow.doResume(declaration.getVariable());
    }

    @Override
    public Collection<Processable> getChildren() {
        return Arrays.asList(declaration);
    }
}
