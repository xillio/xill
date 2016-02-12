package nl.xillio.xill.plugins.stream.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.api.io.IOStream;
import nl.xillio.xill.api.io.SimpleIOStream;
import nl.xillio.xill.plugins.stream.utils.StreamUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * This construct will return a new output stream that will forward all the data to multiple other output streams.
 *
 * @author Thomas biesaart
 */
class ForkConstruct extends Construct {
    @Override
    public ConstructProcessor prepareProcess(ConstructContext context) {
        return new ConstructProcessor(
                this::process,
                new Argument("outputs", LIST)
        );
    }

    private MetaExpression process(MetaExpression outputs) {
        List<MetaExpression> outputsValue = outputs.getValue();

        if (outputsValue.size() < 2) {
            throw new RobotRuntimeException("Please provide at least two outputs");
        }

        OutputStream[] streams = tryGetStreams(outputsValue);
        OutputStream outputStream = StreamUtils.fork(outputs, streams);
        IOStream ioStream = new SimpleIOStream(outputStream, "forked: " + outputs);
        return fromValue(ioStream);
    }

    private OutputStream[] tryGetStreams(List<MetaExpression> outputsValue) {
        try {
            return getStreams(outputsValue);
        } catch (IOException e) {
            throw new RobotRuntimeException("Could not create fork: " + e.getMessage(), e);
        }
    }

    private OutputStream[] getStreams(List<MetaExpression> outputsValue) throws IOException {
        OutputStream[] result = new OutputStream[outputsValue.size()];

        int i = 0;
        for (MetaExpression expression : outputsValue) {
            if (!expression.getBinaryValue().hasOutputStream()) {
                throw new RobotRuntimeException(expression + " does not contain binary data");
            }
            result[i++] = expression.getBinaryValue().getOutputStream();
        }

        return result;
    }
}
