package nl.xillio.xill.components.instructions;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.NoSuchElementException;

import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.components.InstructionFlow;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.Processable;
import nl.xillio.xill.api.construct.ExpressionBuilderHelper;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This {@link Instruction} represents the end of a value holding scope.
 */
public class ReturnInstruction extends Instruction {

	private final Processable value;

	private static final Logger LOGGER = LogManager.getLogger(ReturnInstruction.class);

	/**
	 * Create a new {@link ReturnInstruction}
	 *
	 * @param processable
	 */
	public ReturnInstruction(final Processable processable) {
		value = processable;
	}

	/**
	 * Create a new null {@link ReturnInstruction}
	 */
	public ReturnInstruction() {
		this(null);
	}

	@SuppressWarnings("squid:S1166")
	@Override
	public InstructionFlow<MetaExpression> process(final Debugger debugger) throws RobotRuntimeException {
		if (value == null) {
			return InstructionFlow.doReturn(ExpressionBuilderHelper.NULL);
		}

		try {
			MetaExpression result = value.process(debugger).get();

			return InstructionFlow.doReturn(result);
		} catch (NoSuchElementException e) {
			// No value was provided
			return InstructionFlow.doReturn(ExpressionBuilderHelper.NULL);
		}
	}

	@Override
	public Collection<Processable> getChildren() {
		return Collections.singletonList(value);
	}

	@Override
	public void close() throws Exception {}
}
