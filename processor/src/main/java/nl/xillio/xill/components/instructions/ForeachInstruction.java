package nl.xillio.xill.components.instructions;

import nl.xillio.xill.CodePosition;
import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.components.InstructionFlow;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.MetaExpressionIterator;
import nl.xillio.xill.api.components.Processable;
import nl.xillio.xill.api.construct.ExpressionBuilderHelper;
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
    private final VariableDeclaration valueVar;
    private final VariableDeclaration keyVar;

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
        valueVar.setHostInstruction(instructionSet);
        this.keyVar = keyVar;
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
    public ForeachInstruction(final InstructionSet instructionSet, final Processable list,
                              final VariableDeclaration valueVar) {
        this(instructionSet, list, valueVar, null);

    }

    @SuppressWarnings("unchecked")
    @Override
    public InstructionFlow<MetaExpression> process(final Debugger debugger) throws RobotRuntimeException {
        ExpressionInstruction listInstruction = new ExpressionInstruction(list);
        listInstruction.setHostInstruction(getHostInstruction());
        listInstruction.setPosition(getPosition());
        debugger.startInstruction(listInstruction);
        InstructionFlow<MetaExpression> flow = listInstruction.process(debugger);
        debugger.endInstruction(listInstruction, flow);


        MetaExpression result = flow.get();


        try {
            result.registerReference();
            return process(result, debugger);
        } finally {
            listInstruction.close();
        }
    }

    private InstructionFlow<MetaExpression> process(MetaExpression result, Debugger debugger) {
        InstructionFlow<MetaExpression> foreachResult = InstructionFlow.doResume();

        if (result.isNull()) {
            // The input was null. Skip this
            return foreachResult;
        }

        switch (result.getType()) {
            case ATOMIC:
                if (result.getMeta(MetaExpressionIterator.class) == null) {
                    //This is an atomic value with no MetaExpressionIterator. So we just iterate over the single value
                    foreachResult = processIteration(() -> ExpressionBuilderHelper.fromValue(0), result, debugger);
                } else {
                    //We have a MetaExpressionIterator in this value, this means we should iterate over that
                    MetaExpressionIterator iterator = result.getMeta(MetaExpressionIterator.class);
                    int i = 0;
                    while (iterator.hasNext()) {
                        MetaExpression value = iterator.next();

                        int keyValue = i++;
                        InstructionFlow<MetaExpression> instructionResult = processIteration(
                                () -> ExpressionBuilderHelper.fromValue(keyValue),
                                value,
                                debugger
                        );

                        if (instructionResult.skips()) {
                            continue;
                        }

                        if (instructionResult.returns()) {
                            foreachResult = instructionResult;
                            break;
                        }

                        if (instructionResult.breaks()) {
                            foreachResult = InstructionFlow.doResume();
                            break;
                        }


                    }
                }
                break;
            case LIST: // Iterate over list
                try {
                    int i = 0;
                    for (MetaExpression value : (List<MetaExpression>) result.getValue()) {

                        int keyValue = i++;
                        InstructionFlow<MetaExpression> instructionResult = processIteration(
                                () -> ExpressionBuilderHelper.fromValue(keyValue), value, debugger);


                        if (instructionResult.skips()) {
                            continue;
                        }

                        if (instructionResult.returns()) {
                            foreachResult = instructionResult;
                            break;
                        }

                        if (instructionResult.breaks()) {
                            foreachResult = InstructionFlow.doResume();
                            break;
                        }

                    }
                } catch (ConcurrentModificationException e) {
                    throw new RobotRuntimeException("You cannot modify (add to, or remove from) a list while you are iterating over it.", e);
                }
                break;
            case OBJECT:
                try {
                    for (Map.Entry<String, MetaExpression> value : ((Map<String, MetaExpression>) result.getValue()).entrySet()) {


                        InstructionFlow<MetaExpression> instructionResult = processIteration(() -> ExpressionBuilderHelper.fromValue(value.getKey()), value.getValue(), debugger);
                        if (instructionResult.skips()) {
                            continue;
                        }

                        if (instructionResult.returns()) {
                            foreachResult = instructionResult;
                            break;
                        }

                        if (instructionResult.breaks()) {
                            foreachResult = InstructionFlow.doResume();
                            break;
                        }

                    }
                } catch (ConcurrentModificationException e) {
                    throw new RobotRuntimeException("You cannot modify (add to, or remove from) an object while you are iterating over it.", e);
                }
                break;
            default:
                throw new NotImplementedException("This type has not been implemented.");

        }

        return foreachResult;
    }

    private InstructionFlow<MetaExpression> processIteration(Supplier<MetaExpression> key, MetaExpression value, Debugger debugger) {
        valueVar.pushVariable(value);
        if (keyVar != null) {
            keyVar.pushVariable(key.get());
        }

        InstructionFlow<MetaExpression> instructionResult = instructionSet.process(debugger);

        if (instructionResult.returns() && instructionResult.hasValue()) {
            instructionResult.get().preventDisposal();

            // Release
            releaseVariables();


            instructionResult.get().allowDisposal();
        } else {
            releaseVariables();
        }

        return instructionResult;
    }

    private void releaseVariables() {
        valueVar.releaseVariable();
        if (keyVar != null) {
            keyVar.releaseVariable();
        }
    }

    @Override
    public void setPosition(CodePosition position) {
        super.setPosition(position);
    }

    @Override
    public Collection<Processable> getChildren() {
        if (keyVar != null) {
            return Arrays.asList(valueVar, keyVar, list, instructionSet);
        }
        return Arrays.asList(valueVar, list, instructionSet);
    }

}
