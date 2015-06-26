package nl.xillio.xill.components.expressions;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.components.InstructionFlow;
import nl.xillio.xill.api.components.ExpressionBuilder;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.Processable;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;

/**
 * This class represents a call to a java construct
 */
public class ConstructCall implements Processable {

	private final Construct construct;
	private final Processable[] arguments;
	private final ConstructContext context;

	/**
	 * Create a new {@link ConstructCall}
	 *
	 * @param construct
	 * @param arguments
	 * @param context
	 */
	public ConstructCall(final Construct construct, final List<Processable> arguments, final ConstructContext context) {
		this.construct = construct;
		this.arguments = arguments.toArray(new Processable[arguments.size()]);
		this.context = context;
	}

	@Override
	public InstructionFlow<MetaExpression> process(final Debugger debugger) throws RobotRuntimeException {
		ConstructProcessor processor = construct.prepareProcess(context);

		// Process arguments
		for (int i = 0; i < arguments.length; i++) {
			MetaExpression result = ExpressionBuilder.NULL;
			try {
				result = arguments[i].process(debugger).get();
			} catch (RobotRuntimeException e) {
				debugger.handle(e);
			}

			processor.setArgument(i, result);
		}

		try {

			// Process
			return InstructionFlow.doResume(processor.process());

		} catch (Exception e) {
			// Catch all exceptions that happen in constructs. (Unsafe envirionment)
			debugger.handle(e);
		}

		// If something goes wrong and the debugger thinks it's okay, then we return null
		return InstructionFlow.doResume(ExpressionBuilder.NULL);

	}

	@Override
	public Collection<Processable> getChildren() {
		return Arrays.asList(arguments);
	}

}
