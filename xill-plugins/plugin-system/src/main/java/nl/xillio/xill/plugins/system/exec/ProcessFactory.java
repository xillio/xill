package nl.xillio.xill.plugins.system.exec;

import java.io.IOException;
import java.util.function.Function;

import nl.xillio.xill.services.inject.FactoryBuilderException;

/**
 * This class builds {@link Process} it is in here to improve testability
 */
public class ProcessFactory implements Function<ProcessDescription, Process> {

	@Override
	public Process apply(final ProcessDescription description) {
		ProcessBuilder builder = new ProcessBuilder(description.getCommands());

		if (description.getWorkdingDirectory() != null) {
			builder.directory(description.getWorkdingDirectory());
		}
		try {
			return builder.start();
		} catch (IOException e) {
			throw new FactoryBuilderException("Could not build a " + Process.class.getName(), e);
		}
	}
}
