package nl.xillio.xill.plugins.system.exec;

import java.util.List;

/**
 * The output of a processor
 *
 */
public class ProcessOutput {

	private final List<String> output;
	private final List<String> errors;
	private final InputStreamListener outputListener;
	private final InputStreamListener errorListener;

	/**
	 * Create a new {@link ProcessOutput}
	 *
	 * @param output
	 *        the output from stdout
	 * @param errors
	 *        the output from stderr
	 * @param outputListener
	 *        the output {@link InputStreamListener}
	 * @param errorListener
	 *        the error {@link InputStreamListener}
	 */
	public ProcessOutput(final List<String> output, final List<String> errors, final InputStreamListener outputListener, final InputStreamListener errorListener) {
		this.output = output;
		this.errors = errors;
		this.outputListener = outputListener;
		this.errorListener = errorListener;

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

	/**
	 * @return true if and only if either error or output streams are still alive
	 */
	public boolean isAlive() {
		return outputListener.isAlive() || errorListener.isAlive();
	}

}
