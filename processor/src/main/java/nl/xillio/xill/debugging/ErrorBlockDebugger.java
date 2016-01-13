package nl.xillio.xill.debugging;

import me.biesaart.utils.Log;
import nl.xillio.xill.api.Debugger;
import org.slf4j.Logger;

/**
 * This class contains all information and controls required for debugging.
 *
 * @author Thomas Biesaart
 */
public class ErrorBlockDebugger extends DelegateDebugger {

    private static final Logger LOGGER = Log.get();
    private Throwable error;

    private boolean hasError = false;

    public void setDebug(Debugger debugger) {
        super.setDebugger(debugger);
    }

    @Override
    public void handle(Throwable e) {
        this.hasError = true;
        this.error = e;

        LOGGER.error("Caught exception in error handler", e);
    }

    @Override
    public boolean shouldStop() {
        return super.shouldStop() || hasError();
    }

    public boolean hasError() {
        return this.hasError;
    }

    public Throwable getError() {
        if (hasError) {
            return this.error;
        }
        return null;
    }

}
