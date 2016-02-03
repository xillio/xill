package nl.xillio.xill.plugins.file.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.api.io.IOStream;
import nl.xillio.xill.api.io.PathIOStream;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * This construct will open a file handle with write permissions.
 *
 * @author Thomas biesaart
 */
public class OpenWriteConstruct extends Construct {
    @Override
    public ConstructProcessor prepareProcess(ConstructContext context) {
        return new ConstructProcessor(
                path -> process(path, context),
                new Argument("path", ATOMIC)
        );
    }

    private MetaExpression process(MetaExpression pathVar, ConstructContext context) {
        Path path = getPath(context, pathVar.getStringValue());

        if (Files.isDirectory(path)) {
            throw new RobotRuntimeException("Cannot create write target for a directory");
        }

        if (!Files.isWritable(path) && Files.exists(path)) {
            throw new RobotRuntimeException("Cannot write to " + path);
        }

        IOStream streamBuilder = new PathIOStream(path, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
        return fromValue(streamBuilder, path.toAbsolutePath().toString());
    }
}
