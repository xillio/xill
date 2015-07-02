package nl.xillio.xill.debugging;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Stack;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import nl.xillio.events.Event;
import nl.xillio.events.EventHost;
import nl.xillio.xill.api.Breakpoint;
import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.NullDebugger;
import nl.xillio.xill.api.components.Instruction;
import nl.xillio.xill.api.components.InstructionFlow;
import nl.xillio.xill.api.components.InstructionSet;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.Robot;
import nl.xillio.xill.api.errors.ErrorHandlingPolicy;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.api.events.RobotContinuedAction;
import nl.xillio.xill.api.events.RobotPausedAction;
import nl.xillio.xill.api.events.RobotStartedAction;
import nl.xillio.xill.api.events.RobotStoppedAction;
import nl.xillio.xill.components.instructions.VariableDeclaration;
import xill.lang.xill.Target;

/**
 * This class contains all information and controls required for debugging
 */
public class XillDebugger implements Debugger {
    private static final Logger log = Logger.getLogger(XillDebugger.class);
    private final List<Breakpoint> breakpoints;
    private Instruction previousInstruction;
    private Instruction currentInstruction;
    private boolean stepIn = false;
    private DebugInfo debugInfo = new DebugInfo();

    /**
     * Here we put the instruction we want to step over. (Aka pause after)
     */
    private Instruction stepOver = null;
    private Instruction pausedOnInstruction = null;
    private boolean paused = false;
    private final EventHost<RobotStartedAction> onRobotStarted = new EventHost<>();
    private final EventHost<RobotStoppedAction> onRobotStopped = new EventHost<>();
    private final EventHost<RobotPausedAction> onRobotPaused = new EventHost<>();
    private final EventHost<RobotContinuedAction> onRobotContinued = new EventHost<>();
    private boolean shouldStop = false;
    private ErrorHandlingPolicy handler = new NullDebugger();
    private final Stack<Instruction> currentStack = new Stack<>();

    /**
     * Create a new {@link XillDebugger}
     */
    public XillDebugger() {
	breakpoints = new ArrayList<>();
    }

    /**
     * Create a child {@link XillDebugger} that will use the same breakpoints as
     * it's parents
     * 
     * @param breakpoints
     */
    public XillDebugger(final List<Breakpoint> breakpoints) {
	this.breakpoints = breakpoints;

    }

    @Override
    public void pause() {
	paused = true;
    }

    @Override
    public void stop() {
	resume();
	shouldStop = true;
    }

    /**
     * Resume running
     */
    @Override
    public void resume() {
	paused = false;
	stepIn = false;
	stepOver = null;
	shouldStop = false;
    }

    @Override
    public void stepIn() {
	paused = false;
	stepIn = true;
	stepOver = null;
    }

    @Override
    public void stepOver() {
	paused = false;
	stepIn = false;
	stepOver = currentInstruction;
    }

    @Override
    public void startInstruction(final Instruction instruction) {
	currentInstruction = instruction;
	currentStack.push(instruction);
	Optional<Breakpoint> breakpoint = breakpoints.stream()
		.filter(bp -> bp.matches(previousInstruction, instruction)).findAny();

	if (stepIn || breakpoint.isPresent()) {
	    paused = true;
	}

	checkPause(instruction);

    }

    @Override
    public void endInstruction(final Instruction instruction, final InstructionFlow<MetaExpression> result) {
	checkPause(instruction);
	
	if (!currentStack.isEmpty()) {
	    currentStack.pop();
	}

	previousInstruction = instruction;

	if (stepOver == instruction) {
	    paused = true;
	}
    }

    /**
     * Check if the robot should pause
     * 
     * @param instruction
     *            The instruction to pause on (i.e. pass to the pause event
     */
    private void checkPause(final Instruction instruction) {
	if (paused) {
	    pausedOnInstruction = instruction;
	    onRobotPaused.invoke(new RobotPausedAction(instruction));
	    while (paused) {
		try {
		    Thread.sleep(100);
		} catch (InterruptedException e) {
		    e.printStackTrace();
		}
	    }
	    pausedOnInstruction = null;
	    onRobotContinued.invoke(new RobotContinuedAction(instruction));
	}
    }

    @Override
    public void returning(final InstructionSet instructionSet, final InstructionFlow<MetaExpression> result) {
    }

    /**
     * Add breakpoints to this debugger
     *
     * @param breakpoints
     */
    public void addBreakpoints(final Collection<Breakpoint> breakpoints) {
	this.breakpoints.addAll(breakpoints);
    }

    /**
     * Add breakpoint to this debugger
     *
     * @param breakpoint
     */
    @Override
    public void addBreakpoint(final Breakpoint breakpoint) {
	breakpoints.add(breakpoint);
    }

    /**
     * Remove all breakpoints from this debugger
     */
    public void clearBreakpoints() {
	breakpoints.clear();
    }

    /**
     * @return the currentInstruction
     */
    public Instruction getCurrentInstruction() {
	return currentInstruction;
    }

    @Override
    public void robotStarted(final Robot robot) {
	onRobotStarted.invoke(new RobotStartedAction(robot));
	resume();
	previousInstruction = null;
	paused = false;
	currentStack.clear();
    }

    @Override
    public void robotFinished(final Robot robot) {
	onRobotStopped.invoke(new RobotStoppedAction(robot));

	log.info("Robot finished.");
    }

    @Override
    public void addDebugInfo(final nl.xillio.xill.api.DebugInfo info) {
	debugInfo.add((DebugInfo) info);
    }

    @Override
    public Event<RobotStartedAction> getOnRobotStart() {
	return onRobotStarted.getEvent();
    }

    @Override
    public Event<RobotStoppedAction> getOnRobotStop() {
	return onRobotStopped.getEvent();
    }

    @Override
    public Event<RobotPausedAction> getOnRobotPause() {
	return onRobotPaused.getEvent();
    }

    @Override
    public Event<RobotContinuedAction> getOnRobotContinue() {
	return onRobotContinued.getEvent();
    }

    @Override
    public void setBreakpoints(final List<Breakpoint> breakpoints) {
	clearBreakpoints();
	this.breakpoints.addAll(breakpoints);
    }

    @Override
    public boolean shouldStop() {
	return shouldStop;
    }

    @Override
    public Collection<Object> getVariables() {
	if (!paused) {
	    throw new IllegalStateException("Cannot get variables if not paused.");
	}
	List<Object> filtered = new ArrayList<>();
	
	for(Entry<Target, VariableDeclaration> pair : debugInfo.getVariables().entrySet().stream()
	.sorted((a, b) -> Integer.compare(a.getValue().getLineNumber(), b.getValue().getLineNumber())).collect(Collectors.toList())) {
	    
	    try{
	    if(pair.getValue().getVariable() != null || isVisible(pair.getKey(), pausedOnInstruction)) {
		filtered.add(pair.getKey());
	    }
	    }catch(EmptyStackException e){}
	}

	
	return filtered;
    }

    private boolean isVisible(final Target target, final Instruction instruction) {
	VariableDeclaration dec = debugInfo.getVariables().get(target);

	if (dec.getRobotID() != instruction.getRobotID()) {
	    return false;
	}

	return true;
    }

    @Override
    public MetaExpression getVariableValue(final Object identifier) {
	VariableDeclaration dec = debugInfo.getVariables().get(identifier);

	return dec.getVariable();
    }

    @Override
    public String getVariableName(final Object identifier) {
	if (!(identifier instanceof Target)) {
	    return null;
	}

	return ((Target) identifier).getName();
    }

    @Override
    public void reset() {
	debugInfo = new DebugInfo();
    }

    @Override
    public void handle(final Throwable e) throws RobotRuntimeException {
	handler.handle(e);
    }

    @Override
    public void setErrorHander(final ErrorHandlingPolicy handler) {
	this.handler = handler;
    }

    @Override
    public List<Instruction> getStackTrace() {
	return currentStack;
    }

    @Override
    public Debugger createChild() {
	return new NullDebugger();
    }

}
