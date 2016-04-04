package nl.xillio.xill.plugins.system.exec;

import java.util.List;

/**
 * The output of a processor
 */
public class ProcessOutput {

    private final String output;
    private final String errors;

    /**
     * Create a new {@link ProcessOutput}
     *
     * @param output         the output from stdout
     * @param errors         the output from stderr
     */
    public ProcessOutput(final String output, final String errors) {
        this.output = output;
        this.errors = errors;
    }

    /**
     * @return the output
     */
    public String getOutput() {
        return output;
    }

    /**
     * @return the errors
     */
    public String getErrors() {
        return errors;
    }
}
