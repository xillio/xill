package nl.xillio.xill.components.instructions;

import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.components.InstructionFlow;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.Processable;

import java.util.Arrays;
import java.util.Collection;

/**
 * This {@link Instruction} represents a stop in an instruction set.
 */
public class ElseInstruction extends CompoundInstruction {

    private final InstructionSet elseInstructions;

    /**
     * Create a new {@link ElseInstruction}
     *
     * @param elseInstructions A collection of elseInstructions.
     */
    public ElseInstruction(final InstructionSet elseInstructions) {
        this.elseInstructions = elseInstructions;
        elseInstructions.setParentInstruction(this);

    }

    @Override
    public InstructionFlow<MetaExpression> process(final Debugger debugger) {
        return elseInstructions.process(debugger);
    }

    @Override
    public Collection<Processable> getChildren() {
        return Arrays.asList(elseInstructions);
    }

    @Override
    public boolean preventDebugging() {
        return false;
    }

}
