package nl.xillio.xill.plugins.stream.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.InvalidUserInputException;
import nl.xillio.xill.api.errors.OperationFailedException;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.charset.Charset;

import static nl.xillio.xill.plugins.stream.utils.StreamUtils.getInputStream;
import static nl.xillio.xill.plugins.stream.utils.StreamUtils.getOutputStream;

/**
 * This construct will read data from an input stream and pass it to an output stream.
 * It has a limit parameter that allows a user to limit the amount of data that is streamed.
 *
 * @author Thomas biesaart
 */
class WriteConstruct extends Construct {

    @Override
    public ConstructProcessor prepareProcess(ConstructContext context) {
        return new ConstructProcessor(
                this::process,
                new Argument("source", ATOMIC),
                new Argument("target", ATOMIC),
                new Argument("limit", fromValue(-1), ATOMIC),
                new Argument("outputCharset", NULL, ATOMIC),
                new Argument("inputCharset", NULL, ATOMIC)
        );
    }

    @SuppressWarnings("squid:UnusedPrivateMethod") // Sonar does not detect method references
    private MetaExpression process(MetaExpression source, MetaExpression target, MetaExpression limit, MetaExpression outputCharset, MetaExpression inputCharset) {

        if (Double.isNaN(limit.getNumberValue().doubleValue())) {
            throw new InvalidUserInputException("The passed 'limit' parameter is not a valid number.", limit.getStringValue(), "A valid number.",
                    "use File, Stream;\n" +
                    "var target = File.openWrite(\"./target.txt\");\n" +
                    "Stream.write(\"Hello World\\n\", target, 100)");
        }

        InputStream inputStream = openInputStream(source);
        OutputStream outputStream = getOutputStream(target, "target");

        // Ignore input encoding if the input is a string (not a stream)
        String inputCharsetName = source.getBinaryValue().hasInputStream() ? inputCharset.getStringValue() : null;
        String outputCharsetName = outputCharset.getStringValue();

        long dataCount = write(inputStream, outputStream, outputCharsetName, inputCharsetName,
                limit.getNumberValue().longValue());
        return fromValue(dataCount);
    }

    private InputStream openInputStream(MetaExpression source) {
        if (source.getBinaryValue().hasInputStream()) {
            return getInputStream(source, "source");
        }
        return IOUtils.toInputStream(source.getStringValue());
    }

    private long write(InputStream inputStream, OutputStream outputStream, String outputCharset, String inputCharset, long limit) {
        try {
            // Copy the buffer one-to-one if no charset conversion is required
            if (outputCharset==null && inputCharset==null) {
                return IOUtils.copyLarge(inputStream, outputStream, 0, limit);
            }
            else {
                // Create readers to convert between character sets
                InputStreamReader inputStreamReader = inputCharset==null ?
                        new InputStreamReader(inputStream) : new InputStreamReader(inputStream, inputCharset);
                OutputStreamWriter outputStreamWriter = outputCharset==null ?
                        new OutputStreamWriter(outputStream) : new OutputStreamWriter(outputStream, outputCharset);
                return IOUtils.copyLarge(inputStreamReader, outputStreamWriter, 0, limit);
            }
        } catch (IOException e) {
            throw new OperationFailedException("write to stream", e.getMessage(), e);
        }
    }


}
