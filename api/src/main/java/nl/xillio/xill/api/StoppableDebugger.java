package nl.xillio.xill.api;

/**
 * This class represents a debugger that can be stopped (this is only behaviour that is supported)
 */
public class StoppableDebugger extends NullDebugger {

    private boolean stop = false;

    @Override
    public void stop() {
        stop = true;
    }

    @Override
    public boolean shouldStop() {
        return stop;
    }
}
