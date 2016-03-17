package nl.xillio.xill.plugins.concurrency.services;

import com.google.inject.Inject;
import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.NullDebugger;
import nl.xillio.xill.api.XillEnvironment;
import nl.xillio.xill.api.XillProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.api.errors.XillParsingException;
import nl.xillio.xill.plugins.concurrency.data.Worker;
import nl.xillio.xill.plugins.concurrency.data.WorkerConfiguration;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * This class is responsible for creating threads from {@link nl.xillio.xill.plugins.concurrency.data.WorkerConfiguration}.
 *
 * @author Titus Nachbauer
 * @author Thomas Biesaart
 */
public class WorkerThreadFactory {
    private final XillEnvironment xillEnvironment;

    @Inject
    public WorkerThreadFactory(XillEnvironment xillEnvironment) {
        this.xillEnvironment = xillEnvironment;
    }

    public Worker build(WorkerConfiguration workerConfiguration) {
        Path projectPath = Paths.get(".");
        Path robot = getRobotPath(workerConfiguration.getRobot());
        XillProcessor processor = getProcessor(projectPath, robot, new NullDebugger);

        compile(processor);
        //TODO: add output Queue
        return new Worker(processor.getRobot(), processor.getDebugger());
    }

    private void compile(XillProcessor processor) {
        try {
            processor.compile();
        } catch (IOException e) {
            throw new RobotRuntimeException("An IO error occurred while parsing a robot: " + e.getMessage(), e);
        } catch (XillParsingException e) {
            throw new RobotRuntimeException("Syntax error in "+ processor.getRobotID().getPath() + ": " + e.getMessage(), e);
        }
    }

    private XillProcessor getProcessor(Path projectPath, Path robot, Debugger debugger) {
        try {
            return xillEnvironment.buildProcessor(projectPath, robot, debugger);
        } catch (IOException e) {
            throw new RobotRuntimeException("An IO error occurred while compiling a robot: " + e.getMessage(), e);
        }
    }

    private Path getRobotPath(String path) {
        Path robot = Paths.get(path);
        return robot;
    }

}
