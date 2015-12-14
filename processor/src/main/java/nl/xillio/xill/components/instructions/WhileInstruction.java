package nl.xillio.xill.components.instructions;

import nl.xillio.xill.CodePosition;
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
public class WhileInstruction extends CompoundInstruction {

    private final ExpressionInstruction condition;
    private final InstructionSet instructionSet;

    /**
     * Instantiate a {@link WhileInstruction} from a condition and an InstructionSet
     *
     * @param condition      the condition
     * @param instructionSet the set that should be processes until the condition hits false
     */
    public WhileInstruction(final Processable condition, final InstructionSet instructionSet) {
        this.condition = new ExpressionInstruction(condition);
        this.instructionSet = instructionSet;
    }

    @Override
    public InstructionFlow<MetaExpression> process(final Debugger debugger) throws RobotRuntimeException {
        while (check(debugger)) {
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

    @Override
    public void setPosition(CodePosition position) {
        super.setPosition(position);
        condition.setPosition(position);
    }

    private boolean check(Debugger debugger) {
        debugger.startInstruction(condition);
        InstructionFlow<MetaExpression> result = condition.process(debugger);
        MetaExpression expression = result.get();
        expression.registerReference();
        boolean isValue = expression.getBooleanValue();
        debugger.endInstruction(condition, result);
        expression.releaseReference();
        condition.clear();
        return isValue;
    }

    @Override
    public Collection<Processable> getChildren() {
        return Arrays.asList(condition, instructionSet);
    }
}
