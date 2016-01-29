package nl.xillio.xill.api;

import nl.xillio.events.Event;
import nl.xillio.xill.api.components.*;
import nl.xillio.xill.api.errors.ErrorHandlingPolicy;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.api.events.RobotContinuedAction;
import nl.xillio.xill.api.events.RobotPausedAction;
import nl.xillio.xill.api.events.RobotStartedAction;
import nl.xillio.xill.api.events.RobotStoppedAction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This class represents a debugger that does absolutely nothing
 */
public class NullDebugger implements Debugger {

    @Override
    public void stepIn() {
    }

    @Override
    public void stepOver() {
    }

    @Override
    public void startInstruction(final Instruction instruction) {
    }

    @Override
    public void endInstruction(final Instruction instruction, final InstructionFlow<MetaExpression> result) {
    }

    @Override
    public void returning(final InstructionSet instructionSet, final InstructionFlow<MetaExpression> result) {
    }

    @Override
    public void resume() {
    }

    @Override
    public void robotStarted(final Robot robot) {
    }

    @Override
    public void robotFinished(final Robot robot) {
    }

    @Override
    public void addDebugInfo(final DebugInfo info) {
    }

    @Override
    public Event<RobotStartedAction> getOnRobotStart() {
        return null;
    }

    @Override
    public Event<RobotStoppedAction> getOnRobotStop() {
        return null;
    }

    @Override
    public Event<RobotPausedAction> getOnRobotPause() {
        return null;
    }

    @Override
    public Event<RobotContinuedAction> getOnRobotContinue() {
        return null;
    }

    @Override
    public EventEx<Object> getOnRobotInterrupt() {
        return null;
    }

    @Override
    public void addBreakpoint(final Breakpoint breakpoint) {
    }

    @Override
    public void setBreakpoints(final List<Breakpoint> breakpoints) {
    }

    @Override
    public void pause(boolean userAction) {
    }

    @Override
    public void stop() {
    }

    @Override
    public boolean shouldStop() {
        return false;
    }

    @Override
    public Collection<Object> getVariables(Instruction instruction) {
        return new ArrayList<>();
    }

    @Override
    public MetaExpression getVariableValue(final Object identifier, int stackPosition) {
        return ExpressionBuilderHelper.fromValue(false);
    }

    @Override
    public String getVariableName(final Object identifier) {
        return "";
    }

    @Override
    public void reset() {
    }

    @Override
    public void handle(final Throwable e) throws RobotRuntimeException {
        if (e instanceof RobotRuntimeException) {
            throw (RobotRuntimeException) e;
        }

        throw new RobotRuntimeException("Exception in robot.", e);
    }

    @Override
    public void setErrorHandler(final ErrorHandlingPolicy handler) {
    }

    @Override
    public List<Instruction> getStackTrace() {
        return new ArrayList<>();
    }

    @Override
    public Debugger createChild() {
        return this;
    }

    @Override
    public void removeChild(final Debugger debugger) {
    }

    @Override
    public void startFunction(Processable functionDeclaration) {

    }

    @Override
    public void endFunction(Processable functionDeclaration) {

    }

}
