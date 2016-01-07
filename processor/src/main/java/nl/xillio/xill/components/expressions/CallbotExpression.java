package nl.xillio.xill.components.expressions;

import nl.xillio.plugins.PluginLoader;
import nl.xillio.plugins.XillPlugin;
import nl.xillio.xill.XillProcessor;
import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.RobotAppender;
import nl.xillio.xill.api.Xill;
import nl.xillio.xill.api.components.InstructionFlow;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.Processable;
import nl.xillio.xill.api.components.RobotID;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.components.ExpressionBuilderHelper;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.api.errors.XillParsingException;
import nl.xillio.xill.services.files.FileResolver;
import nl.xillio.xill.services.files.FileResolverImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

/**
 * This class represents calling another robot
 */
public class CallbotExpression implements Processable {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Logger robotLogger;
    private final Processable path;
    private final RobotID robotID;
    private final PluginLoader<XillPlugin> pluginLoader;
    private Processable argument;
    private final FileResolver resolver;

    /**
     * Create a new {@link CallbotExpression}
     *
     * @param path         the path of the called bot
     * @param robotID      the root robot of this tree
     * @param pluginLoader the current plugin loader
     */
    public CallbotExpression(final Processable path, final RobotID robotID, final PluginLoader<XillPlugin> pluginLoader) {
        this.path = path;
        this.robotID = robotID;
        this.pluginLoader = pluginLoader;
        robotLogger = RobotAppender.getLogger(robotID);
        resolver = new FileResolverImpl();
    }

    @Override
    public InstructionFlow<MetaExpression> process(final Debugger debugger) {
        MetaExpression pathExpression = path.process(debugger).get();

        File otherRobot = resolver.buildFile(new ConstructContext(robotID, robotID, null, null, null, null, null), pathExpression.getStringValue());

        LOGGER.debug("Evaluating callbot for " + otherRobot.getAbsolutePath());

        if (debugger.getStackTrace().size() > Xill.MAX_STACK_SIZE) {
            throw new RobotRuntimeException("Callbot went into too many recursions.");
        }
        if (!otherRobot.exists()) {
            throw new RobotRuntimeException("Called robot " + otherRobot.getAbsolutePath() + " does not exist.");
        }

        if (!otherRobot.getName().endsWith(Xill.FILE_EXTENSION)) {
            throw new RobotRuntimeException("Can only call robots with the ." + Xill.FILE_EXTENSION + " extension.");
        }

        // Process the robot
        try {
            Debugger childDebugger = debugger.createChild();
            XillProcessor processor = new XillProcessor(robotID.getProjectPath(), otherRobot, pluginLoader, childDebugger);

            processor.compileAsSubRobot(robotID);

            try {
                nl.xillio.xill.api.components.Robot robot = processor.getRobot();

                if (argument != null) {
                    InstructionFlow<MetaExpression> argumentResult = argument.process(debugger);

                    robot.setArgument(argumentResult.get());
                }

                InstructionFlow<MetaExpression> result = processor.getRobot().process(childDebugger);

                if (result.hasValue()) {
                    return InstructionFlow.doResume(result.get());
                }
            } catch (Exception e) {
                if (e instanceof RobotRuntimeException) {
                    throw (RobotRuntimeException) e;
                }
                throw new RobotRuntimeException("An exception occurred while evaluating " + otherRobot.getAbsolutePath(), e);
            } finally {
                debugger.removeChild(childDebugger);
            }

        } catch (IOException e) {
            throw new RobotRuntimeException("Error while calling robot: " + e.getMessage(), e);
        } catch (XillParsingException e) {
            throw new RobotRuntimeException("Error while parsing robot: " + e.getMessage(), e);
        } catch (Exception e) {
            debugger.handle(e);
        }

        return InstructionFlow.doResume(ExpressionBuilderHelper.NULL);
    }

    @Override
    public Collection<Processable> getChildren() {
        return Collections.singletonList(path);
    }

    /**
     * @return the robotLogger
     */
    public Logger getRobotLogger() {
        return robotLogger;
    }

    /**
     * Set the argument that will be passed to the called robot
     *
     * @param argument
     */
    public void setArgument(final Processable argument) {
        this.argument = argument;
    }

}
