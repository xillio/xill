package nl.xillio.xill.components.instructions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.components.InstructionFlow;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.Processable;
import nl.xillio.xill.api.errors.RobotRuntimeException;

/**
 * This {@link Instruction} represents the condition based branching mechanism.
 */
public class IfInstructionBlock extends Instruction {
	private final List<IfInstruction> conditionInstructions;
	private final ElseInstruction elseInstruction;

	/**
	 * Create a new {@link IfInstructionBlock}
	 *
	 * @param conditionals
	 * @param elseInstruction
	 */
	public IfInstructionBlock(final List<IfInstruction> conditionals, final ElseInstruction elseInstruction) {
		conditionInstructions = conditionals;
		this.elseInstruction = elseInstruction;
	}

	@Override
	public InstructionFlow<MetaExpression> process(final Debugger debugger) throws RobotRuntimeException {

		// Find the first if instruction
		for (IfInstruction instruction : conditionInstructions) {
			debugger.startInstruction(instruction);

			InstructionFlow<MetaExpression> result = InstructionFlow.doResume();

			if (instruction.isTrue(debugger)) {
				result = instruction.process(debugger);
				debugger.endInstruction(instruction, result);
				return result;
			}

			debugger.endInstruction(instruction, result);
		}

		// Process the else instruction
		if (elseInstruction != null) {
			debugger.startInstruction(elseInstruction);
			InstructionFlow<MetaExpression> result = elseInstruction.process(debugger);
			debugger.endInstruction(elseInstruction, result);
			return result;
		}

		// There was no else instruction
		return InstructionFlow.doResume();
	}

	@Override
	public Collection<Processable> getChildren() {
		List<Processable> children = new ArrayList<>();
		children.addAll(conditionInstructions);

		return children;
	}

	@Override
	public boolean preventDebugging() {
		return true;
	}

	@Override
	public void close() throws Exception {}
}
