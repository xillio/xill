package nl.xillio.xill.components.expressions;

import java.util.*;
import java.util.Map.Entry;

import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.components.*;
import nl.xillio.xill.api.construct.ExpressionBuilderHelper;
import nl.xillio.xill.api.errors.NotImplementedException;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.components.instructions.FunctionDeclaration;

/**
 * Call a function for every value in a collection
 *
 * @author Pieter Soels, Thomas Biesaart
 */
public class MapExpression extends MapFilterHandler implements Processable, FunctionParameterExpression {
	private final Processable argument;

	/**
	 * Create a new {@link MapExpression}
	 * 
	 * @param argument
	 */
	public MapExpression(final Processable argument) {
		this.argument = argument;
	}

    /**
     * Start of instructions for the map expression.
     */
	@Override
	public InstructionFlow<MetaExpression> process(final Debugger debugger) {
		// Call the function for all arguments
		MetaExpression result = argument.process(debugger).get();

		try {
			result.registerReference();
			return process(result, debugger);
		} catch (ConcurrentModificationException e) {
			throw new RobotRuntimeException("You can not change the expression upon which you are iterating.", e);
		} finally {
			result.releaseReference();
		}
	}

    /**
     * Depending on type of MetaExpression (result) process individually.
     */
	private InstructionFlow<MetaExpression> process(MetaExpression result, Debugger debugger){
		switch (result.getType()) {
			case ATOMIC:
                if (result.isNull()) {
                    return InstructionFlow.doResume(ExpressionBuilderHelper.emptyList());
                }
                if (result.getMeta(MetaExpressionIterator.class) == null){
                    return atomicProcessNoIterator(result, debugger);
                } else {
                    return atomicProcessIterator(result, debugger);
                }
			case LIST:
				return listProcess(result, debugger);
			case OBJECT:
				return objectProcess(result, debugger);
			default:
				throw new NotImplementedException("This MetaExpression has not been implemented yet");
		}
	}

    /**
     * Map process of an atomic value
     * Result empty list when input is null
     */
	private InstructionFlow<MetaExpression> atomicProcessNoIterator(MetaExpression input, Debugger debugger) {
        List<MetaExpression> atomicResults = new ArrayList<>(1);
        if (functionDeclaration.getParametersSize() == 1) {
            atomicResults.add(functionDeclaration.run(debugger, Collections.singletonList(input)).get());
        } else if (functionDeclaration.getParametersSize() == 2) {
            atomicResults.add(functionDeclaration.run(debugger,
                    Arrays.asList(ExpressionBuilderHelper.fromValue(0), input)).get());
        } else {
            throw new RobotRuntimeException("The given function does not accept one or two arguments.");
        }
        return InstructionFlow.doResume(ExpressionBuilderHelper.fromValue(atomicResults));
    }

    private InstructionFlow<MetaExpression> atomicProcessIterator(MetaExpression input, Debugger debugger) {
        MetaExpressionIterator iterator = input.getMeta(MetaExpressionIterator.class);
        List<MetaExpression> atomicResults = new ArrayList<>(1);
        int i = -1;

        while (iterator.hasNext()) {
            MetaExpression value = iterator.next();
            value.registerReference();

            if (functionDeclaration.getParametersSize() == 1){
                atomicResults.add(functionDeclaration.run(debugger, Collections.singletonList(value)).get());
            } else if (functionDeclaration.getParametersSize() == 2){
                int keyValue = i++;
                atomicResults.add(functionDeclaration.run(debugger,
                        Arrays.asList(ExpressionBuilderHelper.fromValue(keyValue), value)).get());
            } else {
                throw new RobotRuntimeException("The given function does not accept one or two arguments.");
            }
        }
        return InstructionFlow.doResume(ExpressionBuilderHelper.fromValue(atomicResults));
    }

    /**
     * Map process of an list value.
     * Perform function on all elements of the list (one layer deep).
     */
    @SuppressWarnings("unchecked")
    private InstructionFlow<MetaExpression> listProcess(MetaExpression result, Debugger debugger) {
        List<MetaExpression> listResults = new ArrayList<>(result.getNumberValue().intValue());

        if (functionDeclaration.getParametersSize() == 1) {
            for (MetaExpression expression : (List<MetaExpression>) result.getValue()) {
                listResults.add(functionDeclaration.run(debugger, Collections.singletonList(expression)).get());
            }
        } else if (functionDeclaration.getParametersSize() == 2) {
            List<MetaExpression> expressions = (List<MetaExpression>) result.getValue();
            for (int i = 0; i < expressions.size(); i++) {
                listResults.add(functionDeclaration.run(debugger,
                        Arrays.asList(ExpressionBuilderHelper.fromValue(i), expressions.get(i))).get());
            }
        } else {
            throw new RobotRuntimeException("The given function does not accept one or two arguments.");
        }

        return InstructionFlow.doResume(ExpressionBuilderHelper.fromValue(listResults));
    }

    /**
     * Map process of an object value.
     * Perform function on all elements in object (one layer deep).
     */
    @SuppressWarnings("unchecked")
	private InstructionFlow<MetaExpression> objectProcess(MetaExpression result, Debugger debugger) {
        LinkedHashMap<String, MetaExpression> objectResults = new LinkedHashMap<>(result.getNumberValue().intValue());

        if (functionDeclaration.getParametersSize() == 1) {
            for (Entry<String, MetaExpression> expression : ((Map<String, MetaExpression>) result.getValue()).entrySet()) {
                objectResults.put(expression.getKey(), functionDeclaration.run(debugger, Collections.singletonList(expression.getValue())).get());
            }
        } else if (functionDeclaration.getParametersSize() == 2) {
            for (Entry<String, MetaExpression> expression : ((Map<String, MetaExpression>) result.getValue()).entrySet()) {
                objectResults.put(expression.getKey(), functionDeclaration.run(debugger,
                        Arrays.asList(ExpressionBuilderHelper.fromValue(expression.getKey()), expression.getValue())).get());
            }
        } else {
            throw new RobotRuntimeException("The given function does not accept one or two arguments.");
        }

        return InstructionFlow.doResume(ExpressionBuilderHelper.fromValue(objectResults));
    }

	@Override
	public Collection<Processable> getChildren() {
		return null;
	}

	/**
	 * Set the function parameter
	 * 
	 * @param functionDeclaration
	 */
	@Override
	public void setFunction(final FunctionDeclaration functionDeclaration) {
		this.functionDeclaration = functionDeclaration;
	}

}
