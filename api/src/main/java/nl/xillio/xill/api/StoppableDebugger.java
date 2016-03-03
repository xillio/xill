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
     * @return whether an error occurred
     */
    public boolean hasErrorOccurred() {
        return errorOccurred;
    }

    /**
     * Sets whether the debugger should stop if an error occurs.
     *
     * @param stopOnError whether the robot will stop when an error occurs
     */
    public void setStopOnError(boolean stopOnError) {
        this.stopOnError = stopOnError;
    }
}
