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
public class BreakInstruction extends Instruction {

	@Override
	public InstructionFlow<MetaExpression> process(final Debugger debugger) {
		return InstructionFlow.doBreak();
	}

	@Override
	public Collection<Processable> getChildren() {
		return new ArrayList<>();
	}

	@Override
	public void close() throws Exception {}

}
