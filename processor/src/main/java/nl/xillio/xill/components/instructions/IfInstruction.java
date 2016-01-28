package nl.xillio.xill.components.instructions;

import nl.xillio.xill.CodePosition;
import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.components.InstructionFlow;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.Processable;

import java.util.ArrayList;
import java.util.Collection;

/**
 * This {@link Instruction} represents a stop in an instruction set.
 */
public class IfInstruction extends CompoundInstruction {

    private final Processable condition;
    private final InstructionSet instructionSet;

    /**
     * Create a new {@link IfInstruction}
     *
     * @param condition
     * @param instructionSet
     */
    public IfInstruction(final Processable condition, final InstructionSet instructionSet) {
        this.condition = condition;
        this.instructionSet = instructionSet;
        instructionSet.setParentInstruction(this);
    }

    /**
     * Check if the condition for this block is true.
     *
     * @param debugger the debugger to use for processing
     * @return true if and only if the condition of this statement evaluates to true
     */
    public boolean isTrue(final Debugger debugger) {
        try (ExpressionInstruction conditionInstruction = new ExpressionInstruction(condition)) {
            conditionInstruction.setHostInstruction(getHostInstruction());
            conditionInstruction.setPosition(getPosition());
            debugger.startInstruction(conditionInstruction);
            InstructionFlow<MetaExpression> result = conditionInstruction.process(debugger);
            result.get().registerReference();
            debugger.endInstruction(conditionInstruction, result);
            return result.get().getBooleanValue();
        }
    }

    @Override
    public InstructionFlow<MetaExpression> process(final Debugger debugger) {
        return instructionSet.process(debugger);
    }

    @Override
    public void setPosition(CodePosition position) {
        super.setPosition(position);
    }

    @Override
    public Collection<Processable> getChildren() {
        return new ArrayList<>();
    }

}
