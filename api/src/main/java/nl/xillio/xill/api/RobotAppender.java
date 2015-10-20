package nl.xillio.xill.api;

import java.io.Serializable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;

import nl.xillio.events.Event;
import nl.xillio.events.EventHost;
import nl.xillio.xill.api.components.RobotID;

/**
 * This class is a wrapper which is used to get loggers for robots.
 */
@Plugin(name = "RobotAppender", category = "Core", elementType = "appender", printObject = true)
public class RobotAppender extends AbstractAppender {
	private static final long serialVersionUID = -4849648276752490865L;
	private static final EventHost<LogEvent> messageLogged = new EventHost<>();

	/**
	 * Create a {@link RobotAppender}
	 *
	 * @param name
	 *        the name
	 * @param filter
	 *        the filter
	 * @param layout
	 *        the layout
	 */
	protected RobotAppender(final String name, final Filter filter, final Layout<? extends Serializable> layout) {
		super(name, filter, layout, true);
	}

	/**
	 * The prefix used to identify a virtual 'robot' package. This way log4j can use custom appenders for robots
	 */
	public static final String ROBOT_LOGGER_PREFIX = "robot.";

	/**
	 * Creates a new logger for a robot.
	 *
	 * @param id
	 *        the robot id
	 * @return the logger
	 */
	public static Logger getLogger(final RobotID id) {
		return LogManager.getLogger(ROBOT_LOGGER_PREFIX + id.toString());
	}

	@Override
	public void append(final LogEvent event) {
		messageLogged.invoke(event);
	}

	/**
	 * @return the messagelogged
	 */
	public static Event<LogEvent> getMessagelogged() {
		return messageLogged.getEvent();
	}

	/**
	 * Create a new {@link RobotAppender}
	 * 
	 * @param name
	 *        the name
	 * @param layout
	 *        the layout
	 * @param filter
	 *        the filters
	 * @return the {@link RobotAppender}
	 */
	@PluginFactory
	public static RobotAppender createAppender(@PluginAttribute("name") final String name,
					@PluginElement("Layout") Layout<? extends Serializable> layout,
					@PluginElement("Filters") final Filter filter) {

		if (name == null) {
			LOGGER.error("No name provided for MyCustomAppenderImpl");
			return null;
		}
		if (layout == null) {
			layout = PatternLayout.createDefaultLayout();
		}

		return new RobotAppender(name, filter, layout);
	}

}
