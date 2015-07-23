package nl.xillio.xill.plugins.system.exec;

import java.util.List;

/**
 * The output of a processor
 *
 */
public class ProcessOutput {

	private final List<String> output;
	private final List<String> errors;

	/**
	 * Create a new {@link ProcessOutput}
	 *
	 * @param output
	 *        the output from stdout
	 * @param errors
	 *        the output from stderr
	 */
	public ProcessOutput(final List<String> output, final List<String> errors) {
		this.output = output;
		this.errors = errors;

	}

	/**
	 * @return the output
	 */
	public List<String> getOutput() {
		return output;
	}

	/**
	 * @return the errors
	 */
	public List<String> getErrors() {
		return errors;
	}

}
