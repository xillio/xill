package nl.xillio.xill.components.expressions;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import org.apache.log4j.Logger;

import nl.xillio.plugins.PluginLoader;
import nl.xillio.xill.XillProcessor;
import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.PluginPackage;
import nl.xillio.xill.api.RobotLogger;
import nl.xillio.xill.api.Xill;
import nl.xillio.xill.api.components.ExpressionBuilder;
import nl.xillio.xill.api.components.InstructionFlow;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.Processable;
import nl.xillio.xill.api.components.RobotID;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.api.errors.XillParsingException;

/**
 * This class represents calling another robot
 */
public class CallbotExpression implements Processable {
	private static final Logger log = Logger.getLogger(CallbotExpression.class);
	private final Logger robotLogger;
	private final Processable path;
	private final RobotID robotID;
	private final PluginLoader<PluginPackage> pluginLoader;
	private Processable argument;

	/**
	 * Create a new {@link CallbotExpression}
	 *
	 * @param path
	 * @param robotID
	 * @param pluginLoader
	 * @param debugger
	 */
	public CallbotExpression(final Processable path, final RobotID robotID, final PluginLoader<PluginPackage> pluginLoader) {
	this.path = path;
	this.robotID = robotID;
	this.pluginLoader = pluginLoader;
	robotLogger = RobotLogger.getLogger(robotID);
	}

	@Override
	public InstructionFlow<MetaExpression> process(final Debugger debugger) throws RobotRuntimeException {
	MetaExpression pathExpression = path.process(debugger).get();

	File currentRobotDir = robotID.getPath().getParentFile();
	File otherRobot = new File(currentRobotDir, pathExpression.getStringValue());

	log.debug("Evaluating callbot for " + otherRobot.getAbsolutePath());

	if (!otherRobot.exists()) {
		throw new RobotRuntimeException("Called robot " + otherRobot.getAbsolutePath() + " does not exist.");
	}

	if (!otherRobot.getName().endsWith(Xill.FILE_EXTENSION)) {
		throw new RobotRuntimeException("Can only call robots with the ." + Xill.FILE_EXTENSION + " extension.");
	}

	// Process the robot
	try {
		XillProcessor processor = new XillProcessor(robotID.getProjectPath(), otherRobot, pluginLoader, debugger);

		processor.compile();

		try {
		nl.xillio.xill.api.components.Robot robot = processor.getRobot();

		if (argument != null) {
			InstructionFlow<MetaExpression> argumentResult = argument.process(debugger);
			
			robot.setArgument(argumentResult.get());
		}

		InstructionFlow<MetaExpression> result = processor.getRobot().process(debugger.createChild());

		if (result.hasValue()) {
			return InstructionFlow.doResume(result.get());
		}
		} catch (Exception e) {
		if (e instanceof RobotRuntimeException) {
			throw (RobotRuntimeException) e;
		}
		throw new RobotRuntimeException("An exception occured while evaluating " + otherRobot.getAbsolutePath(), e);
		}

	} catch (IOException e) {
		throw new RobotRuntimeException("Error while calling robot: " + e.getMessage());
	} catch (XillParsingException e) {
		throw new RobotRuntimeException("Error while parsing robot: " + e.getMessage(), e);
	} catch (Exception e) {
		debugger.handle(e);
	}

	return InstructionFlow.doResume(ExpressionBuilder.NULL);
	}

	@Override
	public Collection<Processable> getChildren() {
	return Arrays.asList(path);
	}

	/**
	 * @return the robotLogger
	 */
	public Logger getRobotLogger() {
	return robotLogger;
	}

	/**
	 * Set the argument that will be passed to the called robot
	 * @param argument
	 */
	public void setArgument(final Processable argument) {
	this.argument = argument;
	}

}
