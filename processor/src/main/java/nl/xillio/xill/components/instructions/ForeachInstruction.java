package nl.xillio.xill.components.instructions;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.components.InstructionFlow;
import nl.xillio.xill.api.components.Literal;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.Processable;
import nl.xillio.xill.api.errors.NotImplementedException;
import nl.xillio.xill.api.errors.RobotRuntimeException;

/**
 * This {@link Instruction} represents the foreach looping context.
 */
public class ForeachInstruction extends Instruction {

	private final InstructionSet instructionSet;
	private final Processable list;
	private final VariableDeclaration valueVar;
	private final VariableDeclaration keyVar;

	/**
	 * Create a {@link ForeachInstruction} with key and value variables
	 *
	 * @param instructionSet
	 * @param list
	 * @param valueVar
	 * @param keyVar
	 */
	public ForeachInstruction(final InstructionSet instructionSet, final Processable list, final VariableDeclaration valueVar, final VariableDeclaration keyVar) {
		this.instructionSet = instructionSet;
		this.list = list;
		this.valueVar = valueVar;
		this.keyVar = keyVar;
	}

	/**
	 * Create a {@link ForeachInstruction} without a key variable
	 *
	 * @param instructionSet
	 * @param list
	 * @param valueVar
	 */
	public ForeachInstruction(final InstructionSet instructionSet, final Processable list,
			final VariableDeclaration valueVar) {
		this(instructionSet, list, valueVar, null);

	}

	@SuppressWarnings("unchecked")
	@Override
	public InstructionFlow<MetaExpression> process(Debugger debugger) throws RobotRuntimeException {
		MetaExpression result = list.process(debugger).get();

		switch (result.getType()) {
			case ATOMIC: // Iterate over single value
				valueVar.setVariable(result);
				if (keyVar != null) {
					keyVar.setVariable(Literal.fromValue(0));
				}

				return instructionSet.process(debugger);
			case LIST: // Iterate over list
				int i = 0;
				for (MetaExpression value : (List<MetaExpression>) result.getValue()) {
					valueVar.setVariable(value);
					if (keyVar != null) {
						keyVar.setVariable(Literal.fromValue(i++));
					}

					InstructionFlow<MetaExpression> instructionResult = instructionSet.process(debugger);

					if (instructionResult.returns()) {
						return instructionResult;
					}

					if (instructionResult.breaks()) {
						return InstructionFlow.doResume();
					}

					if (instructionResult.skips()) {
						continue;
					}
				}
				break;
			case OBJECT:
				for (Map.Entry<String, MetaExpression> value : ((Map<String, MetaExpression>) result.getValue()).entrySet()) {
					valueVar.setVariable(value.getValue());
					if (keyVar != null) {
						keyVar.setVariable(Literal.fromValue(value.getKey()));
					}

					InstructionFlow<MetaExpression> instructionResult = instructionSet.process(debugger);

					if (instructionResult.returns()) {
						return instructionResult;
					}

					if (instructionResult.breaks()) {
						return InstructionFlow.doResume();
					}

					if (instructionResult.skips()) {
						continue;
					}
				}
				break;
			default:
				throw new NotImplementedException("This type has not been implemented.");

		}

		return InstructionFlow.doResume();
	}

	@Override
	public Collection<Processable> getChildren() {
		if (keyVar != null) {
			return Arrays.asList(valueVar, keyVar, list, instructionSet);
		}
		return Arrays.asList(valueVar, list, instructionSet);
	}

	@Override
	public void close() throws Exception { }

}
