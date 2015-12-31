package nl.xillio.xill.api;

import nl.xillio.events.Event;
import nl.xillio.xill.api.components.*;
import nl.xillio.xill.api.errors.ErrorHandlingPolicy;
import nl.xillio.xill.api.events.RobotContinuedAction;
import nl.xillio.xill.api.events.RobotPausedAction;
import nl.xillio.xill.api.events.RobotStartedAction;
import nl.xillio.xill.api.events.RobotStoppedAction;

import java.util.Collection;
import java.util.List;

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
     * @param breakpoint the breakpoint that should be added
     */
    public void addBreakpoint(final Breakpoint breakpoint);

    /**
     * Replace all the breakpoints in this debugger
     *
     * @param breakpoints the breakpoints that should be set
     */
    public void setBreakpoints(final List<Breakpoint> breakpoints);

    /**
     * This method is called before processing an instruction
     *
     * @param instruction the instruction that started
     */
    public void startInstruction(final Instruction instruction);

    /**
     * This method is called after an instruction has been processed
     *
     * @param instruction the instruction that ended
     * @param result      the result of the instruction
     */
    public void endInstruction(final Instruction instruction, final InstructionFlow<MetaExpression> result);

    /**
     * @param instructionSet the set that is returning a value
     * @param result         the returned value
     */
    public void returning(final InstructionSet instructionSet, final InstructionFlow<MetaExpression> result);

    /**
     * This method is called whenever a robot starts
     *
     * @param robot the robot that started
     */
    public void robotStarted(final Robot robot);

    /**
     * This method is called whenever a robot ends
     *
     * @param robot the robot that finished
     */
    public void robotFinished(final Robot robot);

    /**
     * Add debugging info
     *
     * @param info the info that should be added
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
     * @return The Event
     */
    public EventEx<Object> getOnRobotInterrupt();

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
    public Collection<Object> getVariables(Instruction instruction);

    /**
     * @param identifier the identifier returned by {@link Debugger#getVariables(Instruction)}
     * @return The current value in a variable
     * @see Debugger#getVariables(Instruction)
     */
    public MetaExpression getVariableValue(final Object identifier, int stackPosition);

    /**
     * @param identifier the identifier returned by {@link Debugger#getVariables(Instruction)}
     * @return The name of a variable
     * @see Debugger#getVariables(Instruction)
     */
    public String getVariableName(final Object identifier);

    /**
     * Purge all information
     */
    public void reset();

    /**
     * @param handler the handler to set
     */
    public void setErrorHandler(final ErrorHandlingPolicy handler);

    /**
     * @return the stack trace to the current instruction
     */
    public List<Instruction> getStackTrace();

    /**
     * Instantiate a child debugger
     *
     * @return the debugger
     */
    public Debugger createChild();

    /**
     * Start processing a function call.
     *
     * @param functionDeclaration the function
     */
    void startFunction(Processable functionDeclaration);

    /**
     * End the function.
     *
     * @param functionDeclaration the function
     */
    void endFunction(Processable functionDeclaration);
}
