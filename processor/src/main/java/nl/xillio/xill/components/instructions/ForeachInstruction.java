package nl.xillio.xill.components.instructions;

import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.components.*;
import nl.xillio.xill.api.errors.NotImplementedException;
import nl.xillio.xill.api.errors.RobotRuntimeException;

import java.util.*;
import java.util.function.Supplier;

/**
 * This {@link Instruction} represents the foreach looping context.
 */
public class ForeachInstruction extends CompoundInstruction {
    private final InstructionSet instructionSet;
    private final Processable list;
    private final VariableDeclaration valueVar, keyVar;

    /**
     * Create a {@link ForeachInstruction} with key and value variables
     *
     * @param instructionSet the instructionSet to run
     * @param list           the list of values
     * @param valueVar       the reference to the value variable
     * @param keyVar         the reference to the key variable
     */
    public ForeachInstruction(final InstructionSet instructionSet, final Processable list, final VariableDeclaration valueVar, final VariableDeclaration keyVar) {
        this.instructionSet = instructionSet;
        instructionSet.setParentInstruction(this);

        this.list = list;
        this.valueVar = valueVar;
        this.keyVar = keyVar;

        valueVar.setHostInstruction(instructionSet);
        if (keyVar != null) {
            keyVar.setHostInstruction(instructionSet);
        }
    }

    /**
     * Create a {@link ForeachInstruction} without a key variable
     *
     * @param instructionSet the instructionSet to run
     * @param list           the list of values
     * @param valueVar       the reference to the value variable
     */
    public ForeachInstruction(final InstructionSet instructionSet, final Processable list, final VariableDeclaration valueVar) {
        this(instructionSet, list, valueVar, null);
    }

    @SuppressWarnings("unchecked")
    @Override
    public InstructionFlow<MetaExpression> process(final Debugger debugger) {
        // Create the list instruction, set the host instruction and position.
        ExpressionInstruction listInstruction = new ExpressionInstruction(list);
        listInstruction.setHostInstruction(getHostInstruction());
        listInstruction.setPosition(getPosition());

        // Start, process, and end the instruction.
        debugger.startInstruction(listInstruction);
        if (debugger.shouldStop()) {
            debugger.endInstruction(listInstruction, InstructionFlow.doReturn(ExpressionBuilderHelper.NULL));
            return InstructionFlow.doReturn(ExpressionBuilderHelper.NULL);
        }
        InstructionFlow<MetaExpression> flow = listInstruction.process(debugger);
        debugger.endInstruction(listInstruction, flow);

        MetaExpression result = flow.get();

        // Register a reference to the result and process it.
        try {
            return process(result, debugger);
        } finally {
            listInstruction.close();
        }
    }

    private InstructionFlow<MetaExpression> process(MetaExpression result, Debugger debugger) {
        // Check if the input is null.
        if (result.isNull()) {
            return InstructionFlow.doResume();
        }

        switch (result.getType()) {
            case ATOMIC:
                return iterateAtomic(debugger, result);
            case LIST:
                return tryIterations(debugger, ((List<MetaExpression>) result.getValue()).iterator(), null);
            case OBJECT:
                Map<String, MetaExpression> map = result.getValue();
                return tryIterations(debugger, map.values().iterator(), map.keySet());
            default:
                throw new NotImplementedException("This type has not been implemented."); // Should never happen.
        }
    }

    private InstructionFlow<MetaExpression> iterateAtomic(Debugger debugger, MetaExpression value) {
        // If the atomic has an iterable meta, iterate over that. Otherwise do a single iteration.
        if (value.hasMeta(MetaExpressionIterator.class)) {
            return doIterations(debugger, value.getMeta(MetaExpressionIterator.class), null);
        } else {
            return processIteration(() -> ExpressionBuilderHelper.fromValue(0), value, debugger);
        }
    }

    private InstructionFlow<MetaExpression> tryIterations(Debugger debugger, Iterator<MetaExpression> valueIterable, Set<String> keySet) {
        // Try to do the iterations, catching exceptions that will occur when modifying the collection.
        try {
            return doIterations(debugger, valueIterable, keySet);
        } catch (ConcurrentModificationException e) {
            throw new RobotRuntimeException("You cannot modify a collection while you are iterating over it.", e);
        }
    }

    // Two breaks keep the code readable, while adding code in between if blocks is not possible here
    @SuppressWarnings("squid:S135")
    private InstructionFlow<MetaExpression> doIterations(Debugger debugger, Iterator<MetaExpression> valueIterator, Set<String> keySet) {
        InstructionFlow<MetaExpression> result = InstructionFlow.doResume();
        int index = 0;
        Iterator<String> keys = keySet != null ? keySet.iterator() : null;

        // Iterate over all values.
        while (valueIterator.hasNext()) {
            MetaExpression value = valueIterator.next();

            // If there are string keys, get the next one.
            String keyString = keys != null ? keys.next() : null;

            // Get the next key as a meta expression.
            MetaExpression key = keyString != null ? ExpressionBuilderHelper.fromValue(keyString) : ExpressionBuilderHelper.fromValue(index);

            InstructionFlow<MetaExpression> instructionResult = processIteration(() -> key, value, debugger);

            // Check if the instruction skips, returns or breaks.
            if (instructionResult.returns()) {
                result = instructionResult;
                break;
            } else if (instructionResult.breaks()) {
                result = InstructionFlow.doResume();
                break;
            } else if (!instructionResult.skips()) {
                index++;
            }
        }

        return result;
    }

    private InstructionFlow<MetaExpression> processIteration(Supplier<MetaExpression> key, MetaExpression value, Debugger debugger) {
        // Push the value and key variable values.
        valueVar.pushVariable(value, debugger.getStackDepth());
        if (keyVar != null) {
            keyVar.pushVariable(key.get(), debugger.getStackDepth());
        }

        InstructionFlow<MetaExpression> instructionResult = instructionSet.process(debugger);

        if (instructionResult.returns() && instructionResult.hasValue()) {
            // Prevent the instruction result from being disposed.
            instructionResult.get().preventDisposal();
            releaseVariables();
            instructionResult.get().allowDisposal();
        } else {
            releaseVariables();
        }

        return instructionResult;
    }

    private void releaseVariables() {
        // Release the value and key variables.
        valueVar.releaseVariable();
        if (keyVar != null) {
            keyVar.releaseVariable();
        }
    }

    @Override
    public Collection<Processable> getChildren() {
        if (keyVar != null) {
            return Arrays.asList(valueVar, keyVar, list, instructionSet);
        }
        return Arrays.asList(valueVar, list, instructionSet);
    }
}
