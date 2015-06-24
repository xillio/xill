package nl.xillio.xill.components.expressions;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import nl.xillio.plugins.PluginLoader;
import nl.xillio.xill.XillProcessor;
import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.PluginPackage;
import nl.xillio.xill.api.RobotLogger;
import nl.xillio.xill.api.Xill;
import nl.xillio.xill.api.components.InstructionFlow;
import nl.xillio.xill.api.components.Literal;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.Processable;
import nl.xillio.xill.api.components.RobotID;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.api.errors.XillParsingException;

import org.apache.log4j.Logger;

/**
 * This class represents calling another robot
 */
public class CallbotExpression implements Processable {

	private final Logger logger;
	private final Processable path;
	private final RobotID robotID;
	private final PluginLoader<PluginPackage> pluginLoader;

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
		logger = RobotLogger.getLogger(robotID);
	}

	@Override
	public InstructionFlow<MetaExpression> process(final Debugger debugger) throws RobotRuntimeException {
		MetaExpression pathExpression = path.process(debugger).get();

		File currentRobotDir = robotID.getPath().getParentFile();
		File otherRobot = new File(currentRobotDir, pathExpression.getStringValue());

		if (!otherRobot.exists()) {
			logger.error("Called robot " + otherRobot.getAbsolutePath() + " does not exist.");
			return InstructionFlow.doResume(Literal.NULL);
		}

		if (!otherRobot.getName().endsWith(Xill.FILE_EXTENSION)) {
			logger.error("Can only call robots with the ." + Xill.FILE_EXTENSION + " extension.");
			return InstructionFlow.doResume(Literal.NULL);
		}

		// Process the robot
		try {
			XillProcessor processor = new XillProcessor(robotID.getProjectPath(), otherRobot, pluginLoader, debugger);

			processor.compile();

			InstructionFlow<MetaExpression> result = processor.getRobot().process(debugger);

			if (result.hasValue()) {
				return InstructionFlow.doResume(result.get());
			}

		} catch (IOException e) {
			logger.error("Error while calling robot: " + e.getMessage());
		} catch (XillParsingException e) {
			logger.error("Error while parsing robot: " + e.getMessage());
		} catch (Exception e) {
			debugger.handle(e);
		}

		return InstructionFlow.doResume(Literal.NULL);
	}

	@Override
	public Collection<Processable> getChildren() {
		return Arrays.asList(path);
	}

}
