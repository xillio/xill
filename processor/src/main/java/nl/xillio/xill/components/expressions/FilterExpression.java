package nl.xillio.xill.components.expressions;

import java.util.*;
import java.util.Map.Entry;

import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.components.InstructionFlow;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.Processable;
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
				atomicProcess(result, debugger);
			case LIST:
				// Pass every argument
				listProcess(result, debugger);
			case OBJECT:
				// Pass every argument but with key
				objectProcess(result, debugger);
			default:
				throw new NotImplementedException("This MetaExpression has not been defined yet.");
		}
	}

	private InstructionFlow<MetaExpression> atomicProcess(MetaExpression result, Debugger debugger){
		if (result.isNull()) {
			return InstructionFlow.doResume(ExpressionBuilderHelper.emptyList());
		}

		List<MetaExpression> atomicResults = new ArrayList<>(1);
		MetaExpression valueAtomic = functionDeclaration.run(debugger, Collections.singletonList(result)).get();
		valueAtomic.registerReference();

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

		for (MetaExpression expression : (List<MetaExpression>) result.getValue()) {
			MetaExpression valueList = functionDeclaration.run(debugger, Collections.singletonList(expression)).get();
			valueList.registerReference();

			if (valueList.getBooleanValue()) {
				listResults.add(expression);
			}

			valueList.releaseReference();
		}
		return InstructionFlow.doResume(ExpressionBuilderHelper.fromValue(listResults));
	}

	@SuppressWarnings("unchecked")
	private InstructionFlow<MetaExpression> objectProcess(MetaExpression result, Debugger debugger){
		LinkedHashMap<String, MetaExpression> objectResults = new LinkedHashMap<>();

		for (Entry<String, MetaExpression> expression : ((Map<String, MetaExpression>) result.getValue()).entrySet()) {
			MetaExpression valueObject = functionDeclaration.run(debugger,
					Arrays.asList(ExpressionBuilderHelper.fromValue(expression.getKey()), expression.getValue())).get();
			valueObject.registerReference();

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
