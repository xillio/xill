package nl.xillio.xill.components.expressions;

import java.util.*;
import java.util.Map.Entry;

import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.components.InstructionFlow;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.Processable;
import nl.xillio.xill.api.components.Robot;
import nl.xillio.xill.api.construct.ExpressionBuilderHelper;
import nl.xillio.xill.api.errors.NotImplementedException;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.components.instructions.FunctionDeclaration;

/**
 * Filter a collection of expressions using a function parameter
 */
public class FilterExpression implements Processable, FunctionParameterExpression {

	private final Processable argument;
	private FunctionDeclaration functionDeclaration;

	/**
	 * Create a new {@link FilterExpression}
	 * 
	 * @param argument
	 */
	public FilterExpression(final Processable argument) {
		this.argument = argument;
	}

	@Override
	public InstructionFlow<MetaExpression> process(final Debugger debugger) throws RobotRuntimeException {
		// Call the function for the argument
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

	private InstructionFlow<MetaExpression> process(MetaExpression result, Debugger debugger){
		switch (result.getType()) {
			case ATOMIC:
				// Process the one argument
				return atomicProcess(result, debugger);
			case LIST:
				// Pass every argument
				return listProcess(result, debugger);
			case OBJECT:
				// Pass every argument but with key
				return objectProcess(result, debugger);
			default:
				throw new NotImplementedException("This MetaExpression has not been defined yet.");
		}
	}

	private InstructionFlow<MetaExpression> atomicProcess(MetaExpression result, Debugger debugger){
		if (result.isNull()) {
			return InstructionFlow.doResume(ExpressionBuilderHelper.emptyList());
		}
		MetaExpression valueAtomic;
		List<MetaExpression> atomicResults = new ArrayList<>(1);

		if (functionDeclaration.getParametersSize() == 1){
			valueAtomic = functionDeclaration.run(debugger, Collections.singletonList(result)).get();
			valueAtomic.registerReference();
		} else if (functionDeclaration.getParametersSize() == 2) {
			valueAtomic = functionDeclaration.run(debugger,
					Arrays.asList(ExpressionBuilderHelper.fromValue(0), result)).get();
			valueAtomic.registerReference();
		} else {
			throw new RobotRuntimeException("The given function does not accept one or two arguments.");
		}

		if (valueAtomic.getBooleanValue()) {
			atomicResults.add(result);
			valueAtomic.releaseReference();
			return InstructionFlow.doResume(ExpressionBuilderHelper.fromValue(atomicResults));
		} else {
			valueAtomic.releaseReference();
			return InstructionFlow.doResume(ExpressionBuilderHelper.emptyList());
		}
	}

	@SuppressWarnings("unchecked")
	private InstructionFlow<MetaExpression> listProcess(MetaExpression result, Debugger debugger){
		List<MetaExpression> listResults = new ArrayList<>();
		List<MetaExpression> expressions = (List<MetaExpression>) result.getValue();

		for (int i = 0; i < expressions.size(); i++) {
			MetaExpression valueList;

			if (functionDeclaration.getParametersSize() == 1){
				valueList = functionDeclaration.run(debugger, Collections.singletonList(expressions.get(i))).get();
				valueList.registerReference();
			} else if (functionDeclaration.getParametersSize() == 2) {
				valueList = functionDeclaration.run(debugger,
						Arrays.asList(ExpressionBuilderHelper.fromValue(i), expressions.get(i))).get();
				valueList.registerReference();
			} else {
				throw new RobotRuntimeException("The given function does not accept one or two arguments.");
			}

			if (valueList.getBooleanValue()) {
				listResults.add(expressions.get(i));
			}

			valueList.releaseReference();
		}
		return InstructionFlow.doResume(ExpressionBuilderHelper.fromValue(listResults));
	}

	@SuppressWarnings("unchecked")
	private InstructionFlow<MetaExpression> objectProcess(MetaExpression result, Debugger debugger){
		LinkedHashMap<String, MetaExpression> objectResults = new LinkedHashMap<>();
		Set<Entry<String, MetaExpression>> expressions = ((Map<String, MetaExpression>) result.getValue()).entrySet();

		for (Iterator<Entry<String, MetaExpression>> it = expressions.iterator(); it.hasNext();) {
			Entry<String, MetaExpression> expression = it.next();
			MetaExpression valueObject;

			if (functionDeclaration.getParametersSize() == 1){
				valueObject = functionDeclaration.run(debugger, Collections.singletonList(expression.getValue())).get();
				valueObject.registerReference();
			} else if (functionDeclaration.getParametersSize() == 2){
				valueObject = functionDeclaration.run(debugger,
						Arrays.asList(ExpressionBuilderHelper.fromValue(expression.getKey()), expression.getValue())).get();
				valueObject.registerReference();
			} else {
				throw new RobotRuntimeException("The given function does not accept one or two arguments.");
			}

			if (valueObject.getBooleanValue()) {
				objectResults.put(expression.getKey(), expression.getValue());
			}

			valueObject.releaseReference();
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
