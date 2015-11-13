package nl.xillio.xill.components.instructions;

import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.components.InstructionFlow;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.Processable;
import nl.xillio.xill.api.errors.RobotRuntimeException;

import java.util.Arrays;
import java.util.Collection;

/**
 * This {@link Instruction} represents the while looping mechanism.
 */
public class WhileInstruction extends Instruction {

    private final Processable condition;
    private final InstructionSet instructionSet;

    /**
     * Instantiate a {@link WhileInstruction} from a condition and an InstructionSet
     *
     * @param condition      the condition
     * @param instructionSet the set that should be processes until the condition hits false
     */
    public WhileInstruction(final Processable condition, final InstructionSet instructionSet) {
        this.condition = condition;
        this.instructionSet = instructionSet;
    }

    @Override
    public InstructionFlow<MetaExpression> process(final Debugger debugger) throws RobotRuntimeException {
        while (check(condition.process(debugger))) {
            InstructionFlow<MetaExpression> result = instructionSet.process(debugger);

            if (result.returns()) {
                return result;
            }

            if (result.breaks()) {
                break;
            }
        }

        return InstructionFlow.doResume();
    }

    private static boolean check(final InstructionFlow<MetaExpression> conditionResult) {
        return conditionResult.get().getBooleanValue();
    }

    @Override
    public Collection<Processable> getChildren() {
        return Arrays.asList(condition, instructionSet);
    }

    @Override
    public void close() throws Exception {
    }
}
