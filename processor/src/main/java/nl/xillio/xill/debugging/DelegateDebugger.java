package nl.xillio.xill.debugging;

import nl.xillio.events.Event;
import nl.xillio.xill.api.Breakpoint;
import nl.xillio.xill.api.DebugInfo;
import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.components.*;
import nl.xillio.xill.api.errors.ErrorHandlingPolicy;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.api.events.RobotContinuedAction;
import nl.xillio.xill.api.events.RobotPausedAction;
import nl.xillio.xill.api.events.RobotStartedAction;
import nl.xillio.xill.api.events.RobotStoppedAction;

import java.util.Collection;
import java.util.List;

/**
 * This class contains all information and controls required for debugging.
 *
 * @author Thomas Biesaart
 */
public class DelegateDebugger implements Debugger {
    private Debugger debugger;

    protected void setDebugger(Debugger debugger) {
        this.debugger = debugger;
    }

    @Override
    public void pause() {
        debugger.pause();
    }

    @Override
    public void stop() {
        debugger.stop();
    }

    @Override
    public void resume() {
        debugger.resume();
    }

    @Override
    public void stepIn() {
        debugger.stepIn();
    }

    @Override
    public void stepOver() {
        debugger.stepOver();
    }

    @Override
    public void addBreakpoint(Breakpoint breakpoint) {
        debugger.addBreakpoint(breakpoint);
    }

    @Override
    public void setBreakpoints(List<Breakpoint> breakpoints) {
        debugger.setBreakpoints(breakpoints);
    }

    @Override
    public void startInstruction(nl.xillio.xill.api.components.Instruction instruction) {
        debugger.startInstruction(instruction);
    }

    @Override
    public void endInstruction(nl.xillio.xill.api.components.Instruction instruction, InstructionFlow<MetaExpression> result) {
        debugger.endInstruction(instruction, result);
    }

    @Override
    public void returning(InstructionSet instructionSet, InstructionFlow<MetaExpression> result) {
        debugger.returning(instructionSet, result);
    }

    @Override
    public void robotStarted(Robot robot) {
        debugger.robotStarted(robot);
    }

    @Override
    public void robotFinished(Robot robot) {
        debugger.robotFinished(robot);
    }

    @Override
    public void addDebugInfo(DebugInfo info) {
        debugger.addDebugInfo(info);
    }

    @Override
    public Event<RobotStartedAction> getOnRobotStart() {
        return debugger.getOnRobotStart();
    }

    @Override
    public Event<RobotStoppedAction> getOnRobotStop() {
        return debugger.getOnRobotStop();
    }

    @Override
    public Event<RobotPausedAction> getOnRobotPause() {
        return debugger.getOnRobotPause();
    }

    @Override
    public Event<RobotContinuedAction> getOnRobotContinue() {
        return debugger.getOnRobotContinue();
    }

    @Override
    public EventEx<Object> getOnRobotInterrupt() {
        return debugger.getOnRobotInterrupt();
    }

    @Override
    public boolean shouldStop() {
        return debugger.shouldStop();
    }

    @Override
    public Collection<Object> getVariables(nl.xillio.xill.api.components.Instruction instruction) {
        return debugger.getVariables(instruction);
    }

    @Override
    public MetaExpression getVariableValue(Object identifier, int stackPosition) {
        return debugger.getVariableValue(identifier, stackPosition);
    }

    @Override
    public String getVariableName(Object identifier) {
        return debugger.getVariableName(identifier);
    }

    @Override
    public void reset() {
        debugger.reset();
    }

    @Override
    public void setErrorHandler(ErrorHandlingPolicy handler) {
        debugger.setErrorHandler(handler);
    }

    @Override
    public List<nl.xillio.xill.api.components.Instruction> getStackTrace() {
        return debugger.getStackTrace();
    }

    @Override
    public Debugger createChild() {
        return debugger.createChild();
    }

    @Override
    public void startFunction(Processable functionDeclaration) {
        debugger.startFunction(functionDeclaration);
    }

    @Override
    public void endFunction(Processable functionDeclaration) {
        debugger.endFunction(functionDeclaration);
    }

    @Override
    public void handle(Throwable e) throws RobotRuntimeException {
        debugger.handle(e);
    }

    @Override
    public void removeChild(Debugger debug) {
        debugger.removeChild(debug);
    }
}