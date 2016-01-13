package nl.xillio.xill.debugging;

import me.biesaart.utils.Log;
import nl.xillio.xill.api.Debugger;
import org.slf4j.Logger;

import java.util.NoSuchElementException;

/**
 * This class contains all information and controls required for debugging.
 *
 * @author Thomas Biesaart
 */
public class ErrorBlockDebugger extends DelegateDebugger {

    private static final Logger LOGGER = Log.get();
    private Throwable error;

    public void setDebug(Debugger debugger) {
        super.setDebugger(debugger);
    }

    @Override
    public void handle(Throwable e) {
        this.error = e;
        LOGGER.error("Caught exception in error handler", e);
    }

    @Override
    public boolean shouldStop() {
        return super.shouldStop() || hasError();
    }

    public boolean hasError() {
        return error != null;
    }

    public Throwable getError() {
        if (hasError()) {
            return this.error;
        }
        throw new NoSuchElementException("No error was caught");
    }

}
