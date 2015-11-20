package nl.xillio.xill.components.expressions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.Xill;
import nl.xillio.xill.api.components.*;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.components.instructions.FunctionDeclaration;

/**
 * This class represents a call of a custom function
 */
public class FunctionCall implements Processable {

	private FunctionDeclaration function;
	private List<Processable> arguments;

	@Override
	@SuppressWarnings("squid:RedundantThrowsDeclarationCheck")
	// The RobotRuntimeException will not be addressed as it triggers editor specific behaviour.
	public InstructionFlow<MetaExpression> process(final Debugger debugger) throws RobotRuntimeException {
		if(debugger.getStackTrace().size() > Xill.MAX_STACK_SIZE) {
			throw new RobotRuntimeException("The robot went in too many recursions. The language currently supports up to " + Xill.MAX_STACK_SIZE + " recursions");
		}

		// Process arguments
		List<MetaExpression> expressions = new ArrayList<>();

		for (Processable argument : arguments) {
			expressions.add(argument.process(debugger).get());
		}

		return function.run(debugger, expressions);

	}

	/**
	 * Initialize this {@link FunctionCall}
	 * 
	 * @param function
	 * @param arguments
	 */
	public void initialize(final FunctionDeclaration function, final List<Processable> arguments) {
		this.function = function;
		this.arguments = arguments;
	}

	@Override
	public Collection<Processable> getChildren() {
		List<Processable> children = new ArrayList<>(arguments);
		children.add(function);
		return children;
	}
}
