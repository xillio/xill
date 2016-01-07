package nl.xillio.xill.debugging;

import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.errors.RobotRuntimeException;

/**
 * This class contains all information and controls required for debugging.
 *
 * @author Thomas Biesaart
 */
public class ErrorBlockDebugger extends DelegateDebugger {

    private boolean hasError = false;

    public void setDebug(Debugger debugger) {
        super.setDebugger(debugger);
    }

    @Override
    public void handle(Throwable e) {
        this.hasError = true;

        if (e instanceof RobotRuntimeException) {
            throw (RobotRuntimeException) e;
        }

        throw new RobotRuntimeException("Exception in robot.", e);
    }

    @Override
    public boolean shouldStop() {
        return false;
    }

    public boolean hasError() {
        return this.hasError;
    }

}