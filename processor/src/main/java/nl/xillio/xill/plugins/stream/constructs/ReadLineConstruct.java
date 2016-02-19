package nl.xillio.xill.plugins.stream.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.stream.utils.StreamUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import static nl.xillio.xill.plugins.stream.utils.StreamUtils.getInputStream;

/**
 * This construct will read a single line from a stream without consuming more than just that line.
 *
 * @author Thomas biesaart
 */
class ReadLineConstruct extends Construct {

    @Override
    public ConstructProcessor prepareProcess(ConstructContext context) {
        return new ConstructProcessor(
                this::process,
                new Argument("stream", ATOMIC),
                new Argument("encoding", fromValue("UTF-8"), ATOMIC)
        );
    }

    @SuppressWarnings("squid:UnusedPrivateMethod") // Sonar does not detect method references
    private MetaExpression process(MetaExpression stream, MetaExpression encoding) {
        Charset charset = StreamUtils.getCharset(encoding);
        BufferedInputStream inputStream = getInputStream(stream, "stream");
        String line = readLine(inputStream, charset);
        return fromValue(line);
    }

    private String readLine(BufferedInputStream inputStream, Charset charset) {
        try {
            return StreamUtils.readLine(inputStream, charset);
        } catch (IOException e) {
            throw new RobotRuntimeException("Failed to read line: " + e.getMessage(), e);
        }
    }

}
