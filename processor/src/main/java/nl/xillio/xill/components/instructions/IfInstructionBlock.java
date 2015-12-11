package nl.xillio.xill.components.instructions;

import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.components.InstructionFlow;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.Processable;
import nl.xillio.xill.api.errors.RobotRuntimeException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This {@link Instruction} represents the condition based branching mechanism.
 */
public class IfInstructionBlock extends CompoundInstruction {
    private final List<IfInstruction> conditionInstructions;
    private final ElseInstruction elseInstruction;

    /**
     * Create a new {@link IfInstructionBlock}.
     *
     * @param conditionals    the if instructions
     * @param elseInstruction the else instruction
     */
    public IfInstructionBlock(final List<IfInstruction> conditionals, final ElseInstruction elseInstruction) {
        conditionInstructions = conditionals;
        this.elseInstruction = elseInstruction;
    }

    @Override
    public InstructionFlow<MetaExpression> process(final Debugger debugger) throws RobotRuntimeException {

        // Find the first if instruction
        for (IfInstruction instruction : conditionInstructions) {

            if (instruction.isTrue(debugger)) {
                return instruction.process(debugger);
            }
        }

        // Process the else instruction
        if (elseInstruction != null) {
            return elseInstruction.process(debugger);
        }

        // There was no else instruction
        return InstructionFlow.doResume();
    }

    @Override
    public Collection<Processable> getChildren() {
        List<Processable> children = new ArrayList<>();
        children.addAll(conditionInstructions);

        return children;
    }

    @Override
    public void close() throws Exception {
    }
}
