package nl.xillio.xill.plugins.stream.constructs;


import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
                new Argument("limit", fromValue(-1), ATOMIC)
        );
    }

    private MetaExpression process(MetaExpression source, MetaExpression target, MetaExpression limit) {

        if (Double.isNaN(limit.getNumberValue().doubleValue())) {
            throw new RobotRuntimeException(limit + " is not a valid number");
        }

        InputStream inputStream = openInputStream(source);
        OutputStream outputStream = getOutputStream(target, "target");

        long dataCount = write(inputStream, outputStream, limit.getNumberValue().longValue());
        return fromValue(dataCount);
    }

    private InputStream openInputStream(MetaExpression source) {
        if (source.getBinaryValue().hasInputStream()) {
            return getInputStream(source, "source");
        }
        return IOUtils.toInputStream(source.getStringValue());
    }

    private long write(InputStream inputStream, OutputStream outputStream, long limit) {
        try {
            return IOUtils.copyLarge(inputStream, outputStream, 0, limit);
        } catch (IOException e) {
            throw new RobotRuntimeException("Failed to write to stream: " + e.getMessage(), e);
        }
    }


}
