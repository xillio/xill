package nl.xillio.xill.plugins.system.constructs;

import me.biesaart.utils.Log;
import nl.xillio.xill.api.components.Expression;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.InvalidUserInputException;
import nl.xillio.xill.api.errors.OperationFailedException;
import nl.xillio.xill.plugins.system.exec.InputStreamListener;
import nl.xillio.xill.plugins.system.exec.ProcessDescription;
import nl.xillio.xill.plugins.system.exec.ProcessFactory;
import nl.xillio.xill.plugins.system.exec.ProcessOutput;
import nl.xillio.xill.services.inject.FactoryBuilderException;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.time.StopWatch;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Runs an application and waits for it to complete
 */
public class ExecConstruct extends Construct {

    private static final org.slf4j.Logger LOGGER = Log.get();

    private final ProcessFactory processFactory = new ProcessFactory();

    @Override
    public ConstructProcessor prepareProcess(final ConstructContext context) {

        return new ConstructProcessor(
                (program, directory) -> process(program, directory, processFactory),
                new Argument("arguments", ATOMIC, LIST),
                new Argument("directory", NULL, ATOMIC));
    }

    static MetaExpression process(final MetaExpression arguments, final MetaExpression directory, final ProcessFactory processFactory) {

        // Get description
        ProcessDescription processDescription = parseInput(arguments, directory);

        // Start stopwatch
        StopWatch sw = new StopWatch();
        sw.start();

        // Start process
        Process process = startProcess(processFactory, processDescription);

        // Subscribe to output
        ProcessOutput output = listenToStreams(process.getInputStream(), process.getErrorStream());

        // Wait for the process to stop
        int exitCode = -1;
        try {
            exitCode = process.waitFor();
        } catch (InterruptedException e) {
            LOGGER.error("Execution interrupted: " + e.getMessage(), e);
        }

        while (output.isAlive()) {
            // Wait for the program to finish.
        }

        // Stop stopwatch
        sw.stop();

        // Return results
        return parseResult(output, sw.getTime(), exitCode);
    }

    /**
     * Parse the {@link MetaExpression} input to a {@link ProcessDescription} so that the {@link ProcessFactory} can use it
     *
     * @param command   the command input
     * @param directory the directory input
     * @return the created {@link ProcessDescription}
     */
    private static ProcessDescription parseInput(final MetaExpression command, final MetaExpression directory) {

        ProcessDescription description;
        File workingDir = null;

        if (directory != NULL) {
            workingDir = new File(directory.getStringValue());
        }

        if (command.getType() == LIST) {
            // Multiple arguments
            @SuppressWarnings("unchecked")
            List<MetaExpression> args = (List<MetaExpression>) command.getValue();
            if (args.isEmpty()) {
                throw new InvalidUserInputException("The input cannot be empty.", "[]", "A list of arguments.",
                        "use System;\n\n" +
                        "var result = System.exec([\"cmd.exe\", \"/C\", \"echo test\"]);\n" +
                        "System.print(result);");
            }
            String[] commands = args.stream().map(Expression::getStringValue).toArray(String[]::new);
            description = new ProcessDescription(workingDir, commands);
            description.setFriendlyName(FilenameUtils.getName(args.get(0).getStringValue()));

        } else {
            String[] commands = new String[]{command.getStringValue()};
            description = new ProcessDescription(workingDir, commands);
            description.setFriendlyName(FilenameUtils.getName(command.getStringValue()));
        }

        return description;
    }

    /**
     * Create and start a {@link Process} using a {@link ProcessFactory} that builds the {@link Process} from a {@link ProcessDescription}
     *
     * @param factory     the factory to use
     * @param description the description of the {@link Process}
     * @return the started {@link Process}
     */
    private static Process startProcess(final ProcessFactory factory, final ProcessDescription description) {

        Process process;
        try {
            process = factory.apply(description);
        } catch (FactoryBuilderException e) {
            throw new OperationFailedException("run " + description.getFriendlyName(), e.getMessage(), "Check the executable path and name.", e);
        }

        return process;
    }

    /**
     * Start listening to the stderr and stdout streams of a {@link Process}
     *
     * @param out the stdout stream
     * @param err the stdin stream
     * @return an {@link ProcessOutput} object that holds the currently streamed output
     */
    private static ProcessOutput listenToStreams(final InputStream out, final InputStream err) {

        // Read from input and output as soon as a line is available
        // Not reading from output and error streams fast enough could cause the process to hang

        List<String> errors = new ArrayList<>();
        InputStreamListener errListener = new InputStreamListener(err);
        errListener.getOnLineComplete().addListener(errors::add);
        errListener.start();

        List<String> output = new ArrayList<>();
        InputStreamListener outListener = new InputStreamListener(out);
        outListener.getOnLineComplete().addListener(output::add);
        outListener.start();

        return new ProcessOutput(output, errors, outListener, errListener);
    }

    /**
     * Parse the result of running a {@link Process} to a {@link MetaExpression}
     *
     * @param output   the {@link ProcessOutput} from the streams
     * @param timeMS   the time in milliseconds it took the processor to run
     * @param exitCode The exit code of the process
     * @return the {@link MetaExpression}
     */
    private static MetaExpression parseResult(final ProcessOutput output, final long timeMS, int exitCode) {
        List<MetaExpression> outputList = output.getOutput().stream().map(ExecConstruct::fromValue).collect(Collectors.toList());
        List<MetaExpression> errorList = output.getErrors().stream().map(ExecConstruct::fromValue).collect(Collectors.toList());

        LinkedHashMap<String, MetaExpression> result = new LinkedHashMap<>();
        result.put("errors", fromValue(errorList));
        result.put("output", fromValue(outputList));
        result.put("runtime", fromValue(timeMS));
        result.put("exitCode", fromValue(exitCode));
        return fromValue(result);
    }
}
