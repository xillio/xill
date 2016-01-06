package nl.xillio.xill.debugging;

import nl.xillio.events.Event;
import nl.xillio.events.EventHost;
import nl.xillio.xill.api.Breakpoint;
import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.NullDebugger;
import nl.xillio.xill.api.StoppableDebugger;
import nl.xillio.xill.api.components.*;
import nl.xillio.xill.api.errors.ErrorHandlingPolicy;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.api.events.RobotContinuedAction;
import nl.xillio.xill.api.events.RobotPausedAction;
import nl.xillio.xill.api.events.RobotStartedAction;
import nl.xillio.xill.api.events.RobotStoppedAction;
import nl.xillio.xill.components.instructions.CompoundInstruction;
import nl.xillio.xill.components.instructions.FunctionDeclaration;
import nl.xillio.xill.components.instructions.Instruction;
import nl.xillio.xill.components.instructions.VariableDeclaration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xill.lang.xill.Target;

import java.util.*;
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
    private final EventHostEx<Object> onRobotInterrupt = new EventHostEx<>();
    private ErrorHandlingPolicy handler = new NullDebugger();
    private final Stack<nl.xillio.xill.api.components.Instruction> currentStack = new Stack<>();
    private final Stack<CounterWrapper> functionStack = new Stack<>();
    private Mode mode = Mode.RUNNING;
    private final LinkedList<Debugger> childDebuggers = new LinkedList<>();


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
        onRobotInterrupt.invoke(null);
        childDebuggers.forEach(e -> e.stop());
    }

    @Override
    public void resume() {
        mode = Mode.RUNNING;
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
    public void startInstruction(final nl.xillio.xill.api.components.Instruction instruction) {
        Instruction internalInstruction = (Instruction) instruction;
        currentStack.add(internalInstruction);
        checkBreakpoints(internalInstruction);
        checkStepIn();
        checkPause(internalInstruction);
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
    public void endInstruction(final nl.xillio.xill.api.components.Instruction instruction, final InstructionFlow<MetaExpression> result) {
        Instruction internalInstruction = (Instruction) instruction;
        checkPause(internalInstruction);
        checkStepOver(internalInstruction);
        currentStack.pop();
    }

    private void checkStepOver(Instruction instruction) {
        if (mode == Mode.STEP_OVER) {
            if (isCompoundInstruction(pausedOnInstruction)) {
                mode = Mode.PAUSED;
            } else if (instruction == pausedOnInstruction) {
                mode = Mode.PAUSED;
            }
        }
    }

    /**
     * Check if this instruction is a compound instruction.
     *
     * @param instruction the instruction to check
     * @return true if and only if the instruction is a compound instruction AND not a FunctionCall
     */
    private boolean isCompoundInstruction(Instruction instruction) {
        return instruction instanceof CompoundInstruction;
    }

    /**
     * Check if the robot should pause.
     *
     * @param instruction The instruction to pause on (i.e. pass to the pause event)
     */
    private void checkPause(final Instruction instruction) {
        if (mode == Mode.PAUSED) {
            pausedOnInstruction = instruction;
            onRobotPaused.invoke(new RobotPausedAction(instruction));
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
        onRobotInterrupt.getListeners().clear();
        onRobotStarted.invoke(new RobotStartedAction(robot));
        resume();
        currentStack.clear();
    }

    @Override
    public void robotFinished(final Robot robot) {
        onRobotStopped.invoke(new RobotStoppedAction(robot, robot.getCompilerSerialId()));

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
    public EventEx<Object> getOnRobotInterrupt() {
        return onRobotInterrupt.getEvent();
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

    @SuppressWarnings("squid:S1166") // Ignored exception is correct here
    @Override
    public Collection<Object> getVariables(nl.xillio.xill.api.components.Instruction instruction) {
        if (mode != Mode.PAUSED) {
            throw new IllegalStateException("Cannot get variables if not paused.");
        }

        if (instruction == null) {
            // No instruction == no results
            return Collections.emptyList();
        }

        nl.xillio.xill.components.instructions.InstructionSet instructionSet = ((nl.xillio.xill.components.instructions.Instruction) instruction).getHostInstruction();

        if (instructionSet == null) {
            LOGGER.warn("No instruction set found for " + instruction);
            return Collections.emptyList();
        }

        return getVariableTokens((Instruction) instruction);
    }

    private List<Object> getVariableTokens(Instruction instruction) {
        return debugInfo.getVariables()
                .entrySet()
                .stream()
                .map(Map.Entry::getValue)
                .filter(dec -> isVisible(dec, instruction))
                .sorted((a, b) -> Integer.compare(a.getLineNumber(), b.getLineNumber()))
                .map(debugInfo::getTarget)
                .collect(Collectors.toList());
    }

    private boolean isVisible(VariableDeclaration variableDeclaration, Instruction instruction) {
        return variableDeclaration.getRobotID() == instruction.getRobotID() && isInScope(variableDeclaration, instruction);
    }

    private boolean isInScope(VariableDeclaration variableDeclaration, Instruction checkInstruction) {
        if (variableDeclaration.getHostInstruction() instanceof Robot) {
            return variableDeclaration.hasValue();
        }

        if (variableDeclaration.getHostInstruction() == checkInstruction.getHostInstruction()) {
            return variableDeclaration.hasValue();
        }

        nl.xillio.xill.components.instructions.InstructionSet instructionSet = checkInstruction.getHostInstruction();
        Instruction parentInstruction = instructionSet.getParentInstruction();

        // We have no parent... move on
        if (parentInstruction == null) {
            LOGGER.debug("No parent instruction found for set around " + checkInstruction);
            return false;
        }

        return isInScope(variableDeclaration, parentInstruction);
    }

    @Override
    public MetaExpression getVariableValue(final Object identifier, int stackPosition) {
        VariableDeclaration dec = debugInfo.getVariables().get(identifier);

        int index = countRecursion(dec, currentStack.size() - stackPosition);

        return dec.peek(index);
    }

    private int countRecursion(VariableDeclaration dec, int stackPosition) {
        int count = -1;
        FunctionDeclaration declaration = getParentFunction(dec);

        if (declaration == null) {
            return 0;
        }

        for (CounterWrapper wrapper : functionStack) {
            if (wrapper.getProcessable() == declaration && wrapper.getStackSize() <= stackPosition) {
                count++;
            }
        }
        return count;
    }

    private FunctionDeclaration getParentFunction(Instruction instruction) {
        nl.xillio.xill.components.instructions.InstructionSet set = instruction.getHostInstruction();
        Instruction parentInstruction = set.getParentInstruction();

        if (parentInstruction == null) {
            // No parent instruction found for instruction
            return null;
        }

        if (parentInstruction instanceof FunctionDeclaration) {
            return (FunctionDeclaration) parentInstruction;
        }

        return getParentFunction(parentInstruction);
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
    public List<nl.xillio.xill.api.components.Instruction> getStackTrace() {
        return currentStack;
    }

    @Override
    public Debugger createChild() {
        Debugger debugger = new StoppableDebugger();
        childDebuggers.add(debugger);
        return debugger;
    }

    @Override
    public void removeChild(final Debugger debugger) {
        childDebuggers.remove(debugger);
    }

    @Override
    public void startFunction(Processable functionDeclaration) {
        functionStack.push(new CounterWrapper(functionDeclaration, currentStack.size()));
    }

    @Override
    public void endFunction(Processable functionDeclaration) {
        functionStack.pop();
    }

    private enum Mode {
        RUNNING,
        STEP_IN,
        STEP_OVER,
        PAUSED,
        STOPPED
    }

    private class CounterWrapper {
        private final Processable processable;
        private final int stackSize;

        private CounterWrapper(Processable processable, int stackSize) {
            this.processable = processable;
            this.stackSize = stackSize;
        }

        public Processable getProcessable() {
            return processable;
        }

        public int getStackSize() {
            return stackSize;
        }
    }
}
