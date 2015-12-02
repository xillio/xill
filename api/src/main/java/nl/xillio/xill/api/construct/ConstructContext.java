package nl.xillio.xill.api.construct;

import java.util.function.Consumer;

import nl.xillio.events.Event;
import nl.xillio.events.EventHost;
import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.RobotAppender;
import nl.xillio.xill.api.components.RobotID;
import nl.xillio.xill.api.events.RobotStartedAction;
import nl.xillio.xill.api.events.RobotStoppedAction;

import org.apache.logging.log4j.Logger;

/**
 * This class represents a context in which a construct can be processed.
 */
public class ConstructContext {
	private final RobotID robotID;
	private Logger robotLogger;
	private final RobotID rootRobot;
	private Logger rootLogger;
    private Debugger debugger;

	/**
	 * Events for signalling to constructs that robots have started or stopped.
	 * Can be used for things like initialization and cleanup.
	 */
	private final EventHost<RobotStartedAction> robotStartedEvent;
	private final EventHost<RobotStoppedAction> robotStoppedEvent;

	/**
	 * Create a new {@link ConstructContext} for a specific robot
	 *
	 * @param robot
	 *        the robotID of the current robot
	 * @param rootRobot
	 *        the robotID of the root robot
	 * @param construct
	 *        the construct that will be using this context
	 * @param robotStartedEvent
	 *        The event host for started robots
	 * @param robotStoppedEvent
	 *        The event host for stopped robots
	 */
	public ConstructContext(final RobotID robot, final RobotID rootRobot, final Construct construct, final Debugger debugger, final EventHost<RobotStartedAction> robotStartedEvent,
			final EventHost<RobotStoppedAction> robotStoppedEvent) {
		robotID = robot;
		this.rootRobot = rootRobot;
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
			robotLogger = RobotAppender.getLogger(robotID);
		}

		return robotLogger;
	}

	/**
	 * @return the root RobotAppender
	 */
	public Logger getRootLogger() {

		// Make sure the logger is set
		if (rootLogger == null) {
			rootLogger = RobotAppender.getLogger(rootRobot);
		}

		return rootLogger;
	}

	/**
	 * Add a listener that will be called when a robot is started
	 * 
	 * @param listener
	 *        The listener to be added
	 */
	public void addRobotStartedListener(Consumer<RobotStartedAction> listener) {
		if (robotStartedEvent != null) {
			robotStartedEvent.getEvent().addListener(listener);
		}
	}

	/**
	 * Add a listener that will be called when a robot is stopped
	 * 
	 * @param listener
	 *        The listener to be added
	 */
	public void addRobotStoppedListener(Consumer<RobotStoppedAction> listener) {
		if (robotStoppedEvent != null) {
			robotStoppedEvent.getEvent().addListener(listener);
		}
	}

    /**
     * @return event that is invoked when the debugger is being stopped
     */
    public Event<Object> getOnRobotInterrupt() {
        if (debugger == null) {
            return null;
        } else {
            return debugger.getOnRobotInterrupt();
        }
    }
}
