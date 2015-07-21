package nl.xillio.xill.plugins.system.constructs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.log4j.Logger;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.system.utils.InputStreamListener;

/**
 * Runs an application and waits for it to complete
 */
public class ExecConstruct extends Construct {
	private static final String DEFAULT_LABEL = "System.exec";

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(
			(program, directory) -> process(program, directory, context.getRootLogger()),
			new Argument("program"),
			new Argument("directory", NULL));
	}

	@SuppressWarnings("unchecked")
	private static MetaExpression process(final MetaExpression program, final MetaExpression directory, final Logger log) {

		// Initialize builder
		ProcessBuilder processBuilder;

		// Get a friendly name
		String friendlyName = DEFAULT_LABEL;

		if (program.getType() == LIST) {
			// Multiple arguments
			List<MetaExpression> args = (List<MetaExpression>) program.getValue();
			processBuilder = new ProcessBuilder(args.stream().map(exp -> exp.getStringValue()).toArray(i -> new String[i]));
			friendlyName = FilenameUtils.getName(args.get(0).getStringValue());
		} else {
			processBuilder = new ProcessBuilder(program.getStringValue());
			friendlyName = FilenameUtils.getName(program.getStringValue());
		}

		// Set working directory
		if (!directory.isNull()) {
			processBuilder.directory(new File(directory.getStringValue()));
		}

		// Start
		Process process;
		StopWatch sw = new StopWatch();
		sw.start();
		try {
			process = processBuilder.start();
		} catch (IOException e) {
			throw new RobotRuntimeException("Failed to run " + program.getStringValue() + ": " + e.getMessage(), e);
		}

		List<MetaExpression> errors = new ArrayList<>();

		// Listen to errors
		InputStreamListener err = new InputStreamListener(process.getInputStream());
		String label = friendlyName;
		err.getOnLineComplete().addListener(line -> {
			log.error(label + ": " + line);
			errors.add(fromValue(line));
		});
		err.start();

		List<MetaExpression> output = new ArrayList<>();
		new InputStreamListener(process.getErrorStream());
		err.getOnLineComplete().addListener(line -> {
			output.add(fromValue(line));
		});
		err.start();

		try {
			process.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		sw.stop();

		LinkedHashMap<String, MetaExpression> result = new LinkedHashMap<>();
		result.put("errors", fromValue(errors));
		result.put("output", fromValue(output));
		result.put("runtime", fromValue(sw.getTime()));

		return fromValue(result);
	}

}
