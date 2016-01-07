package nl.xillio.xill.debugging;

import nl.xillio.events.Event;
import nl.xillio.xill.api.Breakpoint;
import nl.xillio.xill.api.DebugInfo;
import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.NullDebugger;
import nl.xillio.xill.api.components.*;
import nl.xillio.xill.api.errors.ErrorHandlingPolicy;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.api.events.RobotContinuedAction;
import nl.xillio.xill.api.events.RobotPausedAction;
import nl.xillio.xill.api.events.RobotStartedAction;
import nl.xillio.xill.api.events.RobotStoppedAction;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.List;

/**
 * This class contains all information and controls required for debugging.
 *
 * @author Thomas Biesaart
 */
public class ErrorBlockDebugger extends DelegateDebugger {

    public void setDebug(Debugger debugger){
        super.setDebugger(debugger);
    }
    private boolean hasError = false;

    @Override
    public void handle(Throwable e) throws RobotRuntimeException {
        setError(true);

        if (e instanceof RobotRuntimeException) {
            throw (RobotRuntimeException) e;
        }

        throw new RobotRuntimeException("Exception in robot.", e);
    }

    @Override
    public boolean shouldStop() {
        return false;
    }

    public boolean hasError(){
        return this.hasError;
    }

    public void setError(boolean error) {this.hasError = error;}

}