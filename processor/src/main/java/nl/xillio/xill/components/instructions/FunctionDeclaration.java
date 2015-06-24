package nl.xillio.xill.components.instructions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.components.InstructionFlow;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.Processable;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.components.operators.Assign;

/**
 * This {@link Instruction} represents the declaration of a custom function
 */
public class FunctionDeclaration extends Instruction {

	private final InstructionSet instructions;
	private final List<VariableDeclaration> parameters;

	/**
	 * Create a new {@link FunctionDeclaration}
	 *
	 * @param instructions
	 * @param parameters
	 */
	public FunctionDeclaration(final InstructionSet instructions, final List<VariableDeclaration> parameters) {
		this.instructions = instructions;
		this.parameters = parameters;
	}

	@Override
	public InstructionFlow<MetaExpression> process(final Debugger debugger) throws RobotRuntimeException {

		// Reset all parameters
		for (Processable parameter : parameters) {
			parameter.process(debugger);
		}

		return InstructionFlow.doResume();
	}

	/**
	 * Run the actual code
	 * 
	 * @param debugger
	 *
	 * @param arguments
	 * @return The flow result
	 * @throws RobotRuntimeException
	 */
	public InstructionFlow<MetaExpression> run(final Debugger debugger, final List<Assign> arguments) throws RobotRuntimeException {

		// Save the current parameters
		List<MetaExpression> saved_params = parameters.stream().map(VariableDeclaration::getVariable).collect(Collectors.toList());

		// Push the new arguments
		for(Assign argument : arguments)
			argument.process(debugger);

		// Run the actual code
		InstructionFlow<MetaExpression> result = instructions.process(debugger);

		// Put back previous parameters
		for (int i = 0; i < parameters.size(); i++) {
			parameters.get(i).setVariable(saved_params.get(i));
		}

		if (result.hasValue()) {
			return InstructionFlow.doResume(result.get());
		}

		return InstructionFlow.doResume();
	}

	@Override
	public Collection<Processable> getChildren() {
		List<Processable> children = new ArrayList<>(parameters);
		children.add(instructions);
		return children;
	}

	@Override
	public void close() throws Exception {}

}
