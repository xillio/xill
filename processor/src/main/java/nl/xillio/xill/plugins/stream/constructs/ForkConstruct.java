package nl.xillio.xill.plugins.stream.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.InvalidUserInputException;
import nl.xillio.xill.api.errors.OperationFailedException;
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

    @SuppressWarnings("squid:UnusedPrivateMethod") // Sonar does not detect method references
    private MetaExpression process(MetaExpression outputs) {
        List<MetaExpression> outputsValue = outputs.getValue();

        if (outputsValue.size() < 2) {
            String outputProvided = "";
            if (outputsValue.size() == 1) {
                outputProvided = outputsValue.get(0).getStringValue();
            }
            throw new InvalidUserInputException("Too few output provided.", outputProvided, "At least two outputs.", "use File, Stream;\n" +
                    "var source = File.openRead(\"./source.txt\");\n" +
                    "var output = Stream.fork([\n" +
                    "    File.openWrite(\"./source-copy.txt\"),\n" +
                    "    File.openWrite(\"./source-copy-2.txt\")\n" +
                    "]);\n" +
                    "Stream.write(source, output);");
        }

        OutputStream outputStream = tryGetStreams(outputs);
        IOStream ioStream = new SimpleIOStream(outputStream, "forked: " + outputs);
        return fromValue(ioStream);
    }

    private OutputStream tryGetStreams(MetaExpression expression) {
        try {
            return StreamUtils.fork(expression);
        } catch (IOException e) {
            throw new OperationFailedException("create fork", e.getMessage(), e);
        }
    }


}
