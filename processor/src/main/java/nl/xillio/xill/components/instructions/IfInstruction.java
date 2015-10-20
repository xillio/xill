package nl.xillio.xill.components.instructions;

import java.util.ArrayList;
import java.util.Collection;

import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.components.InstructionFlow;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.Processable;

/**
 * This {@link Instruction} represents a stop in an instruction set.
 */
public class IfInstruction extends Instruction {

	private final Processable condition;
	private final InstructionSet instructionSet;

	/**
	 * Create a new {@link IfInstruction}
	 * 
	 * @param condition
	 * @param instructionSet
	 */
	public IfInstruction(final Processable condition, final InstructionSet instructionSet) {
		this.condition = condition;
		this.instructionSet = instructionSet;
	}

	/**
	 * @param debugger
	 * @return true if the condition of this statement is true
	 */
	public boolean isTrue(final Debugger debugger) {
		return condition.process(debugger).get().getBooleanValue();
	}

	@Override
	public InstructionFlow<MetaExpression> process(final Debugger debugger) {
		return instructionSet.process(debugger);
	}

	@Override
	public Collection<Processable> getChildren() {
		return new ArrayList<>();
	}

	@Override
	public void close() throws Exception {}

}
