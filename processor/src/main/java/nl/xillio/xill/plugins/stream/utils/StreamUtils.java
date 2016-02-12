package nl.xillio.xill.plugins.stream.utils;


import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import org.apache.commons.io.output.ByteArrayOutputStream;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

public class StreamUtils {
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
    public static BufferedInputStream getInputStream(MetaExpression expression, String parameterName) {
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

    /**
     * This method will read a single line from a buffered input stream.
     * The end of a line is defined as <code>\n</code>, <code>\r</code>, <code>\r\n</code> or <code>EOF</code>.
     *
     * @param inputStream the input stream
     * @param charset     the character encoding
     * @return the line or null if the
     * @throws IOException
     */
    public static String readLine(BufferedInputStream inputStream, Charset charset) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        int c = inputStream.read();
        if (c == -1) {
            throw new IOException("The end of the stream has been reached");
        }

        while (c != '\n' && c != '\r' && c != -1) {
            output.write(c);
            c = inputStream.read();
        }

        if (c == '\r') {
            // The last character was a return character, if this
            // is followed by the \n character then that is part of the newline

            inputStream.mark(1);
            c = inputStream.read();

            if (c != '\n') {
                // That character was not a newline, put it back
                inputStream.reset();
            }
        }

        return output.toString(charset.name());
    }

    public static OutputStream fork(MetaExpression hostingExpression, OutputStream... streams) {
        return new ForkingOutputStream(hostingExpression, streams);
    }
}
