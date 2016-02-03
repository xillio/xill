package nl.xillio.xill.plugins.file.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.api.io.PathIOStream;

import java.io.File;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;

/**
 * This construct will create a binary reference to a target file.
 *
 * @author Thomas biesaart
 */
public class LoadConstruct extends Construct {
    @Override
    public ConstructProcessor prepareProcess(ConstructContext context) {
        return new ConstructProcessor(
                (uri) -> process(uri, context),
                new Argument("uri", ATOMIC)
        );
    }

    private MetaExpression process(MetaExpression uri, ConstructContext context) {
        Path target = getPath(context, uri);
        PathIOStream ioStream = new PathIOStream(target);
        return fromValue(ioStream, target.toAbsolutePath().toString());
    }

    private Path getPath(ConstructContext context, MetaExpression uri) {
        File target = getFile(context, uri.getStringValue());

        try {
            return target.toPath();
        } catch (InvalidPathException e) {
            throw new RobotRuntimeException("Invalid path: " + e.getMessage(), e);
        }
    }
}
