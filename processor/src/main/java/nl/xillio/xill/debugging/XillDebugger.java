package nl.xillio.xill.debugging;

import nl.xillio.events.Event;
import nl.xillio.events.EventHost;
import nl.xillio.xill.api.Breakpoint;
import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.NullDebugger;
import nl.xillio.xill.api.components.*;
import nl.xillio.xill.api.errors.ErrorHandlingPolicy;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.api.events.RobotContinuedAction;
import nl.xillio.xill.api.events.RobotPausedAction;
import nl.xillio.xill.api.events.RobotStartedAction;
import nl.xillio.xill.api.events.RobotStoppedAction;
import nl.xillio.xill.components.instructions.ForeachInstruction;
import nl.xillio.xill.components.instructions.VariableDeclaration;
import nl.xillio.xill.components.instructions.WhileInstruction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xill.lang.xill.Target;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * This class contains all information and controls required for debugging.
 *
 * @author Thomas Biesaart
 */
public class XillDebugger implements Debugger {
    private static final Logger LOGGER = LogManager.getLogger();
    private final List<Breakpoint> breakpoints;
    private DebugInfo debugInfo = new DebugInfo();
    private Instruction pausedOnInstruction = null;
    private final EventHost<RobotStartedAction> onRobotStarted = new EventHost<>();
    private final EventHost<RobotStoppedAction> onRobotStopped = new EventHost<>();
    private final EventHost<RobotPausedAction> onRobotPaused = new EventHost<>();
    private final EventHost<RobotContinuedAction> onRobotContinued = new EventHost<>();
    private ErrorHandlingPolicy handler = new NullDebugger();
    private final Stack<Instruction> currentStack = new Stack<>();
    private Mode mode = Mode.RUN;

    /**
     * Create a new {@link XillDebugger}.
     */
    public XillDebugger() {
        breakpoints = new ArrayList<>();
    }

    @Override
    public void pause() {
        mode = Mode.PAUSED;
    }

    @Override
    public void stop() {
        mode = Mode.STOPPED;
    }

    @Override
    public void resume() {
        mode = Mode.RUN;
    }

    @Override
    public void stepIn() {
        resume();
        mode = Mode.STEP_IN;
    }

    @Override
    public void stepOver() {
        resume();
        mode = Mode.STEP_OVER;
    }

    @Override
    public void startInstruction(final Instruction instruction) {
        checkBreakpoints(instruction);
        checkStepIn();
        checkPause(instruction);
    }

    private void checkStepIn() {
        if (mode == Mode.STEP_IN) {
            mode = Mode.PAUSED;
        }
    }

    private void checkBreakpoints(Instruction instruction) {
        breakpoints.forEach(bp -> {
            if (bp.matches(instruction)) {
                mode = Mode.PAUSED;
            }
        });
    }

    @Override
    public void endInstruction(final Instruction instruction, final InstructionFlow<MetaExpression> result) {
        checkPause(instruction);
        checkStepOver(instruction);
    }

    private void checkStepOver(Instruction instruction) {
        if (mode == Mode.STEP_OVER) {
            if (isLoopInstruction(pausedOnInstruction)) {
                mode = Mode.PAUSED;
            } else if (instruction == pausedOnInstruction) {
                mode = Mode.PAUSED;
            }
        }
    }

    private boolean isLoopInstruction(Instruction instruction) {
        return instruction instanceof WhileInstruction ||
                instruction instanceof ForeachInstruction;
    }

    /**
     * Check if the robot should pause.
     *
     * @param instruction The instruction to pause on (i.e. pass to the pause event)
     */
    private void checkPause(final Instruction instruction) {
        if (mode == Mode.PAUSED) {
            onRobotPaused.invoke(new RobotPausedAction(instruction));
            pausedOnInstruction = instruction;
            while (mode == Mode.PAUSED) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    LOGGER.error("Interrupted while sleeping", e);
                }
            }
            onRobotContinued.invoke(new RobotContinuedAction(instruction));
        }
    }

    @Override
    public void returning(final InstructionSet instructionSet, final InstructionFlow<MetaExpression> result) {
    }

    /**
     * Add breakpoint to this debugger.
     *
     * @param breakpoint the breakpoint
     */
    @Override
    public void addBreakpoint(final Breakpoint breakpoint) {
        breakpoints.add(breakpoint);
    }

    /**
     * Remove all breakpoints from this debugger.
     */
    public void clearBreakpoints() {
        breakpoints.clear();
    }

    @Override
    public void robotStarted(final Robot robot) {
        onRobotStarted.invoke(new RobotStartedAction(robot));
        resume();
        currentStack.clear();
    }

    @Override
    public void robotFinished(final Robot robot) {
        onRobotStopped.invoke(new RobotStoppedAction(robot));

        LOGGER.info("Robot finished.");
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
        return mode == Mode.STOPPED;
    }

    @Override
    public Collection<Object> getVariables() {
        if (mode != Mode.PAUSED) {
            throw new IllegalStateException("Cannot get variables if not paused.");
        }
        List<Object> filtered = new ArrayList<>();

        for (Entry<Target, VariableDeclaration> pair : debugInfo.getVariables().entrySet().stream()
                .sorted((a, b) -> Integer.compare(a.getValue().getLineNumber(), b.getValue().getLineNumber())).collect(Collectors.toList())) {

            try {
                if (pair.getValue().getVariable() != null && isVisible(pair.getKey(), pausedOnInstruction)) {
                    filtered.add(pair.getKey());
                }
            } catch (EmptyStackException ignored) {
            }
        }

        return filtered;
    }

    private boolean isVisible(final Target target, final Instruction instruction) {
        VariableDeclaration dec = debugInfo.getVariables().get(target);

        // Has to be in the same robot
        return dec.getRobotID() == instruction.getRobotID();

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
    public void setErrorHandler(final ErrorHandlingPolicy handler) {
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

    private enum Mode {
        RUN,
        STEP_IN,
        STEP_OVER,
        PAUSED, STOPPED
    }
}
