package nl.xillio.xill.api;

/**
 * This class represents a debugger that can be stopped (this is only behaviour that is supported).
 * Debugger can stop if error occurs (this is optional).
 */
public class StoppableDebugger extends NullDebugger {

    private boolean stop = false;
    private boolean errorOccurred = false;
    private boolean stopOnError = false;

    @Override
    public void stop() {
        stop = true;
    }

    @Override
    public boolean shouldStop() {
        return stop;
    }

    @Override
    public void pause(boolean userAction) {
        if (!userAction) {
            errorOccurred = true;
            if (stopOnError) {
                stop = true;
            }
        }
    }

    /**
     * @return true if error occurred
     */
    public boolean hasErrorOccurred() {
        return errorOccurred;
    }

    /**
     * Set the debugger behaviour in a way that if error occurred then stop the running robot
     *
     * @param stopOnError true if robot will stop on error
     */
    public void setStopOnError(boolean stopOnError) {
        this.stopOnError = stopOnError;
    }
}
