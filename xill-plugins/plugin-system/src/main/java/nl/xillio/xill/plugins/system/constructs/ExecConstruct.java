package nl.xillio.xill.plugins.system.constructs;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.log4j.Logger;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.api.inject.FactoryBuilderException;
import nl.xillio.xill.plugins.system.exec.InputStreamListener;
import nl.xillio.xill.plugins.system.exec.ProcessDescription;
import nl.xillio.xill.plugins.system.exec.ProcessFactory;
import nl.xillio.xill.plugins.system.exec.ProcessOutput;

/**
 * Runs an application and waits for it to complete
 */
public class ExecConstruct extends Construct {

	private final ProcessFactory processFactory = new ProcessFactory();

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {

		return new ConstructProcessor(
			(program, directory) -> process(program, directory, context.getRootLogger(), processFactory),
			new Argument("arguments"),
			new Argument("directory", NULL));
	}

	static MetaExpression process(final MetaExpression arguments, final MetaExpression directory, final Logger log, final ProcessFactory processFactory) {

		//Get description
		ProcessDescription processDescription = parseInput(arguments, directory);

		//Start stopwatch
		StopWatch sw = new StopWatch();
		sw.start();

		//Start process
		Process process = startProcess(processFactory, processDescription);

		//Subscribe to output
		ProcessOutput output = listenToStreams(process.getInputStream(), process.getErrorStream(), processDescription, log);

		//Wait for the process to stop
		try {
			process.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//Stop stopwatch
		sw.stop();

		//Return results
		return parseResult(output, sw.getTime());
	}

	/**
	 * Parse the {@link MetaExpression} input to a {@link ProcessDescription} so that the {@link ProcessFactory} can use it
	 * 
	 * @param command
	 *        the command input
	 * @param directory
	 *        the directory input
	 * @return the created {@link ProcessDescription}
	 */
	static ProcessDescription parseInput(final MetaExpression command, final MetaExpression directory) {
		assertNotType(command, "arguments", OBJECT);
		assertType(directory, "directory", ATOMIC);

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
				throw new RobotRuntimeException("input cannot be empty");
			}
			String[] commands = args.stream().map(exp -> exp.getStringValue()).toArray(i -> new String[i]);
			description = new ProcessDescription(workingDir, commands);
			description.setFriendlyName(FilenameUtils.getName(args.get(0).getStringValue()));

		} else {
			String[] commands = new String[] {command.getStringValue()};
			description = new ProcessDescription(workingDir, commands);
			description.setFriendlyName(FilenameUtils.getName(command.getStringValue()));
		}

		return description;
	}

	/**
	 * Create and start a {@link Process} using a {@link ProcessFactory} that builds the {@link Process} from a {@link ProcessDescription}
	 * 
	 * @param factory
	 *        the factory to use
	 * @param description
	 *        the description of the {@link Process}
	 * @return the started {@link Process}
	 */
	private static Process startProcess(final ProcessFactory factory, final ProcessDescription description) {

		Process process;
		try {
			process = factory.apply(description);
		} catch (FactoryBuilderException e) {
			throw new RobotRuntimeException("Failed to run " + description.getFriendlyName() + ": " + e.getMessage(), e);
		}

		return process;
	}

	/**
	 * Start listening to the stderr and stdout streams of a {@link Process}
	 * 
	 * @param out
	 *        the stdout stream
	 * @param err
	 *        the stdin stream
	 * @param description
	 *        the {@link ProcessDescription} for the {@link Process} hosting the two streams
	 * @param log
	 *        the logger to log to when an error occurs
	 * @return an {@link ProcessOutput} object that holds the currently streamed output
	 */
	private static ProcessOutput listenToStreams(final InputStream out, final InputStream err, final ProcessDescription description, final Logger log) {
		List<String> errors = new ArrayList<>();

		// Listen to errors
		InputStreamListener errListener = new InputStreamListener(err);
		errListener.getOnLineComplete().addListener(line -> {
			log.error(description.getFriendlyName() + ": " + line);
			errors.add(line);
		});
		errListener.start();

		List<String> output = new ArrayList<>();
		InputStreamListener outListener = new InputStreamListener(out);
		outListener.getOnLineComplete().addListener(line -> {
			output.add(line);;
		});
		outListener.start();

		return new ProcessOutput(output, errors);
	}

	/**
	 * Parse the result of running a {@link Process} to a {@link MetaExpression}
	 * 
	 * @param output
	 *        the {@link ProcessOutput} from the streams
	 * @param timeMS
	 *        the time in milliseconds it took the processor to run
	 * @return the {@link MetaExpression}
	 */
	private static MetaExpression parseResult(final ProcessOutput output, final long timeMS) {
		List<MetaExpression> outputList = output.getOutput().stream().map(ExecConstruct::fromValue).collect(Collectors.toList());
		List<MetaExpression> errorList = output.getErrors().stream().map(ExecConstruct::fromValue).collect(Collectors.toList());

		LinkedHashMap<String, MetaExpression> result = new LinkedHashMap<>();
		result.put("errors", fromValue(errorList));
		result.put("output", fromValue(outputList));
		result.put("runtime", fromValue(timeMS));
		return fromValue(result);
	}
}
