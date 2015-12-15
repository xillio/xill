package nl.xillio.xill.components.instructions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.components.InstructionFlow;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.Processable;
import nl.xillio.xill.api.construct.ExpressionBuilderHelper;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class represents the InstructionSet language component: any number of
 * lines of valid code
 */
public class InstructionSet implements nl.xillio.xill.api.components.InstructionSet {
	private final List<Instruction> instructions = new LinkedList<>();
	private final Debugger debugger;

	private static final Logger LOGGER = LogManager.getLogger();

	/**
	 * Create a new {@link InstructionSet} in debugging mode
	 *
	 * @param debugger
	 */
	public InstructionSet(final Debugger debugger) {
		this.debugger = debugger;
	}

	/**
	 * Add an instruction to the instructionSet
	 *
	 * @param instruction
	 */
	public void add(final Instruction instruction) {
		instructions.add(instruction);
	}

	@Override
	public InstructionFlow<MetaExpression> process(final Debugger debugger) throws RobotRuntimeException {
		InstructionFlow<MetaExpression> processResult = null;
		List<Instruction> processedInstructions = new ArrayList<>();

		for (Instruction instruction : instructions) {

			if (!instruction.preventDebugging()) {
				debugger.startInstruction(instruction);
			}

			if (debugger.shouldStop()) {
				processResult = InstructionFlow.doReturn(ExpressionBuilderHelper.NULL);
				break;
			}

			InstructionFlow<MetaExpression> result = processInstruction(instruction, debugger);
			processedInstructions.add(instruction);

			if (!instruction.preventDebugging()) {
				debugger.endInstruction(instruction, result);
			}

			if (!result.resumes()) {
				debugger.returning(this, result);
				processResult = result;
				break;
			}
		}

		// Make sure the result is not disposed
		if(processResult != null && processResult.hasValue()) {
			processResult.get().preventDisposal();
		}

		// Dispose all processed instructions
		for (Instruction instruction : processedInstructions) {
			try {
				instruction.close();
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
		// Done so dispose of this
		try {
			close();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}

		if (processResult != null) {
			// Restore disposal state
			if(processResult.hasValue()) {
				processResult.get().allowDisposal();
			}

			return processResult;
		}

		return InstructionFlow.doResume();
	}

	private InstructionFlow<MetaExpression> processInstruction(Instruction instruction, Debugger debugger) {
		try {
			return instruction.process(debugger);
		}catch(RobotRuntimeException e) {
			debugger.handle(e);
		}

		return InstructionFlow.doResume(ExpressionBuilderHelper.NULL);
	}

	/**
	 * Run only declarations.
	 * This is required to run functions in this robot as a library.
	 *
	 * @throws RobotRuntimeException
	 */
	public void initialize() throws RobotRuntimeException {
		for (Instruction instruction : instructions) {
			if (instruction instanceof VariableDeclaration || instruction instanceof FunctionDeclaration) {
				instruction.process(debugger);
			}
		}
	}

	/**
	 * @return the debugger
	 */
	public Debugger getDebugger() {
		return debugger;
	}

	@Override
	public Collection<Processable> getChildren() {
		return new ArrayList<>(instructions);
	}

	@Override
	public void close() {

	}
}
