package nl.xillio.xill.components.instructions;

import java.util.Arrays;
import java.util.Collection;

import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.components.InstructionFlow;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.Processable;
import nl.xillio.xill.api.errors.RobotRuntimeException;

/**
 * This {@link Instruction} represents the condition based branching mechanism.
 */
public class IfInstruction extends Instruction {

	private final Processable condition;
	private final InstructionSet ifBlock;
	private final InstructionSet elseBlock;

	/**
	 * Create a new IfInstruction
	 *
	 * @param processable
	 *        The condition to check
	 * @param ifBlock
	 *        The block to run when the condition is true
	 * @param elseBlock
	 *        The block to run when the condition is false
	 */
	public IfInstruction(final Processable processable, final InstructionSet ifBlock, final InstructionSet elseBlock) {
		condition = processable;
		this.ifBlock = ifBlock;
		this.elseBlock = elseBlock;
	}

	/**
	 * Create a new IfInstruction
	 *
	 * @param processable
	 *        The condition to check
	 * @param ifBlock
	 *        The block to run when the condition is true
	 */
	public IfInstruction(final Processable processable, final InstructionSet ifBlock) {
		this(processable, ifBlock, null);
	}

	@Override
	public InstructionFlow<MetaExpression> process(final Debugger debugger) throws RobotRuntimeException {

		@SuppressWarnings("resource")
		InstructionSet result = condition.process(debugger).get().getBooleanValue() ? ifBlock : elseBlock;

		if (result != null) {
			return result.process(debugger);
		}

		return InstructionFlow.doResume();
	}

	@Override
	public Collection<Processable> getChildren() {
		if (elseBlock == null) {
			return Arrays.asList(condition, ifBlock);
		}

		return Arrays.asList(condition, ifBlock, elseBlock);
	}

	@Override
	public void close() throws Exception {}
}
