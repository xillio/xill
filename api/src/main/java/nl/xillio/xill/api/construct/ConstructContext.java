package nl.xillio.xill.api.construct;

import nl.xillio.events.EventHost;
import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.LogUtil;
import nl.xillio.xill.api.XillEnvironment;
import nl.xillio.xill.api.XillProcessor;
import nl.xillio.xill.api.components.EventEx;
import nl.xillio.xill.api.components.RobotID;
import nl.xillio.xill.api.events.RobotStartedAction;
import nl.xillio.xill.api.events.RobotStoppedAction;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * This class represents a context in which a construct can be processed.
 */
public class ConstructContext {

    private final RobotID robotID;
    private Logger robotLogger;
    private final RobotID rootRobot;
    private Logger rootLogger;
    private Debugger debugger;
    private final UUID compilerSerialId;

    /**
     * Events for notifying constructs that robots have started or stopped.
     * Example uses are initialization and cleanup.
     */
    private final EventHost<RobotStartedAction> robotStartedEvent;
    private final EventHost<RobotStoppedAction> robotStoppedEvent;

    /**
     * Creates a new {@link ConstructContext} for a specific robot.
     *
     * @param robot             the robotID of the current robot
     * @param rootRobot         the robotID of the root robot
     * @param construct         the construct that will be using this context
     * @param debugger          the debugger that is being used
     * @param compilerSerialId  the serial id of the compiler instance
     * @param robotStartedEvent the event host for started robots
     * @param robotStoppedEvent the event host for stopped robots
     */
    public ConstructContext(final RobotID robot, final RobotID rootRobot, final Construct construct, final Debugger debugger, UUID compilerSerialId, final EventHost<RobotStartedAction> robotStartedEvent,
                            final EventHost<RobotStoppedAction> robotStoppedEvent) {
        robotID = robot;
        this.rootRobot = rootRobot;
        this.compilerSerialId = compilerSerialId;
        this.robotStartedEvent = robotStartedEvent;
        this.robotStoppedEvent = robotStoppedEvent;
        this.debugger = debugger;
    }

    /**
     * @return the robotID
     */
    public RobotID getRobotID() {
        return robotID;
    }

    /**
     * @return the robotID for the Root Robot
     */
    public RobotID getRootRobot() {
        return rootRobot;
    }

    /**
     * @return the robotLogger
     */
    public Logger getLogger() {

        // Make sure the logger is set
        if (robotLogger == null) {
            robotLogger = LogUtil.getLogger(robotID);
        }

        return robotLogger;
    }

    /**
     * @return the root RobotAppender
     */
    public Logger getRootLogger() {

        // Make sure the logger is set
        if (rootLogger == null) {
            rootLogger = LogUtil.getLogger(rootRobot);
        }

        return rootLogger;
    }

    /**
     * Adds a listener that will be called when a robot is started.
     *
     * @param listener the listener to add
     */
    public void addRobotStartedListener(Consumer<RobotStartedAction> listener) {
        if (robotStartedEvent != null) {
            robotStartedEvent.getEvent().addListener(listener);
        }
    }

    /**
     * Adds a listener that will be called when a robot is stopped.
     *
     * @param listener the listener to add
     */
    public void addRobotStoppedListener(Consumer<RobotStoppedAction> listener) {
        if (robotStoppedEvent != null) {
            robotStoppedEvent.getEvent().addListener(listener);
        }
    }

    /**
     * @return event that is invoked when the debugger is being stopped
     */
    public EventEx<Object> getOnRobotInterrupt() {
        if (debugger == null) {
            return null;
        } else {
            return debugger.getOnRobotInterrupt();
        }
    }

    /**
     * Gets the serial number of the compiler used to compile the script of this context.
     *
     * @return the serial number
     */
    public UUID getCompilerSerialId() {
        return compilerSerialId;
    }

    /**
     * Create a processor using the current debugger as the parent.
     *
     * @param robot           the robot that should be compiled
     * @param xillEnvironment the xill environment
     * @return the processor
     * @throws IOException if an IO error occurs
     */
    public XillProcessor createChildProcessor(Path robot, XillEnvironment xillEnvironment) throws IOException {
        return xillEnvironment.buildProcessor(robotID.getProjectPath().toPath(), robot, debugger.createChild());
    }
}
