package nl.xillio.xill.plugins.stream.utils;


import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.errors.RobotRuntimeException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StreamUtils {
    private static final int BUFFER_SIZE = 1024 * 4;

    private StreamUtils() {
        // No one shall have an instance
    }

    /**
     * This method is a wrapper that allows you to get a stream from an expression.
     *
     * @param expression    the expression
     * @param parameterName the parameter name
     * @return the stream
     * @throws RobotRuntimeException if the provided expression does not have an input stream
     */
    public static InputStream getInputStream(MetaExpression expression, String parameterName) {
        if (!expression.getBinaryValue().hasInputStream()) {
            throw new RobotRuntimeException("Expected a reading data stream for parameter " + parameterName + " but found: " + expression);
        }

        try {
            return expression.getBinaryValue().getInputStream();
        } catch (IOException e) {
            throw new RobotRuntimeException("Could not open stream: " + e.getMessage(), e);
        }
    }

    /**
     * This method is a wrapper that allows you to get a stream from an expression.
     *
     * @param expression    the expression
     * @param parameterName the parameter name
     * @return the stream
     * @throws RobotRuntimeException if the provided expression does not have an output stream
     */
    public static OutputStream getOutputStream(MetaExpression expression, String parameterName) {
        if (!expression.getBinaryValue().hasOutputStream()) {
            throw new RobotRuntimeException("Expected a reading data stream for parameter " + parameterName + " but found: " + expression);
        }

        try {
            return expression.getBinaryValue().getOutputStream();
        } catch (IOException e) {
            throw new RobotRuntimeException("Could not open stream: " + e.getMessage(), e);
        }
    }
}
