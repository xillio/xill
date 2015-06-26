package nl.xillio.xill.api;

import java.util.Collection;
import java.util.List;

import nl.xillio.events.Event;
import nl.xillio.xill.api.components.Instruction;
import nl.xillio.xill.api.components.InstructionFlow;
import nl.xillio.xill.api.components.InstructionSet;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.Robot;
import nl.xillio.xill.api.errors.ErrorHandlingPolicy;
import nl.xillio.xill.api.events.RobotContinuedAction;
import nl.xillio.xill.api.events.RobotPausedAction;
import nl.xillio.xill.api.events.RobotStartedAction;
import nl.xillio.xill.api.events.RobotStoppedAction;

/**
 * This interface represents the container for all debugging information
 */
public interface Debugger extends ErrorHandlingPolicy {

	// BREAKPOINTS

	/**
	 * pause the robot
	 */
	public void pause();

	/**
	 * Stop and kill the current robot
	 */
	public void stop();

	/**
	 * Resume running
	 */
	public void resume();

	/**
	 * Step into the current instruction
	 */
	public void stepIn();

	/**
	 * Step over to the next instruction
	 */
	public void stepOver();

	/**
	 * @param breakpoint
	 */
	public void addBreakpoint(final Breakpoint breakpoint);

	/**
	 * @param breakpoints
	 */
	public void setBreakpoints(final List<Breakpoint> breakpoints);

	/**
	 * This method is called before processing an instruction
	 *
	 * @param instruction
	 */
	public void startInstruction(final Instruction instruction);

	/**
	 * This method is called after an instruction has been processed
	 *
	 * @param instruction
	 * @param result
	 */
	public void endInstruction(final Instruction instruction, final InstructionFlow<MetaExpression> result);

	/**
	 * @param instructionSet
	 * @param result
	 */
	public void returning(final InstructionSet instructionSet, final InstructionFlow<MetaExpression> result);

	/**
	 * This method is called whenever a robot starts
	 * 
	 * @param robot
	 */
	public void robotStarted(final Robot robot);

	/**
	 * This method is called whenever a robot ends
	 * 
	 * @param robot
	 */
	public void robotFinished(final Robot robot);

	/**
	 * Add debugging info
	 * 
	 * @param info
	 */
	public void addDebugInfo(final DebugInfo info);

	// EVENTS

	/**
	 * @return The Event
	 */
	public Event<RobotStartedAction> getOnRobotStart();

	/**
	 * @return The Event
	 */
	public Event<RobotStoppedAction> getOnRobotStop();

	/**
	 * @return The Event
	 */
	public Event<RobotPausedAction> getOnRobotPause();

	/**
	 * @return The Event
	 */
	public Event<RobotContinuedAction> getOnRobotContinue();

	/**
	 * @return true if the robot should be killed
	 */
	public boolean shouldStop();

	// VARIABLE DEBUGGING
	/**
	 * Returns a list of variable identifiers
	 * 
	 * @return The variables
	 */
	public Collection<Object> getVariables();

	/**
	 * @param identifier
	 *        @see {@link Debugger#getVariables()}
	 * @return The current value in a variable
	 */
	public MetaExpression getVariableValue(final Object identifier);

	/**
	 * @param identifier
	 *        @see {@link Debugger#getVariables()}
	 * @return The name of a variable
	 */
	public String getVariableName(final Object identifier);

	/**
	 * Purge all information
	 */
	public void reset();

	/**
	 * @param handler
	 */
	public void setErrorHander(final ErrorHandlingPolicy handler);
	
	/**
	 * @return the stack trace to the current instruction
	 */
	public List<Instruction> getStackTrace();
}
