package nl.xillio.xill.plugins.file.constructs;

import com.google.inject.Inject;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.api.io.IOStream;
import nl.xillio.xill.plugins.file.services.files.FileStreamFactory;

import java.io.IOException;
import java.nio.file.Path;

/**
 * This construct will open a stream to read.
 *
 * @author Thomas biesaart
 */
abstract class AbstractOpenConstruct extends Construct {
    protected FileStreamFactory fileStreamFactory;

    @Inject
    void setFileStreamFactory(FileStreamFactory fileStreamFactory) {
        this.fileStreamFactory = fileStreamFactory;
    }

    @Override
    public ConstructProcessor prepareProcess(ConstructContext context) {
        return new ConstructProcessor(
                path -> process(path, context),
                new Argument("path")
        );
    }

    MetaExpression process(MetaExpression pathVar, ConstructContext context) {
        Path path = getPath(context, pathVar);
        IOStream stream = tryOpen(path);

        return fromValue(stream);
    }

    protected IOStream tryOpen(Path path) {
        try {
            return open(path);
        } catch (IOException e) {
            throw new RobotRuntimeException("Could not open stream to " + path + ": " + e.getMessage(), e);
        }
    }

    protected abstract IOStream open(Path path) throws IOException;


}
