package nl.xillio.xill.plugins.file.constructs;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.biesaart.utils.IOUtilsService;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.file.services.files.FileStreamFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.AccessDeniedException;

/**
 * Write content from a source to a target.
 * Returns the number of written bytes.
 *
 * @author Thomas biesaart
 */
@Singleton
public class WriteConstruct extends Construct {

    private final IOUtilsService ioUtilsService;
    private final FileStreamFactory fileStreamFactory;

    @Inject
    public WriteConstruct(IOUtilsService ioUtilsService, FileStreamFactory fileStreamFactory) {
        this.ioUtilsService = ioUtilsService;
        this.fileStreamFactory = fileStreamFactory;
    }

    @Override
    public ConstructProcessor prepareProcess(final ConstructContext context) {
        return new ConstructProcessor(
                (source, target) -> process(context, source, target),
                new Argument("source", ATOMIC),
                new Argument("target", ATOMIC)
        );
    }

    MetaExpression process(final ConstructContext context, final MetaExpression source, final MetaExpression target) {

        try {
            return fromValue(write(source, target, context));
        } catch (AccessDeniedException e) {
            throw new RobotRuntimeException("Access denied: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new RobotRuntimeException("Could not write to " + target + ": " + e.getMessage(), e);
        }

    }

    private long write(MetaExpression source, MetaExpression target, ConstructContext context) throws IOException {
        // If we have a stream, write to it
        if (target.getBinaryValue().hasOutputStream()) {
            return write(source, target.getBinaryValue().getOutputStream());
        }

        // Otherwise we try to write to the path
        try (OutputStream outputStream = fileStreamFactory.openWrite(getPath(context, target)).getOutputStream()) {
            return write(source, outputStream);
        }
    }

    private long write(MetaExpression source, OutputStream target) throws IOException {
        // If we have a stream, read it
        if (source.getBinaryValue().hasInputStream()) {
            return ioUtilsService.copy(source.getBinaryValue().getInputStream(), target);
        }

        // Otherwise we read the text
        try (InputStream stream = ioUtilsService.toInputStream(source.getStringValue())) {
            return ioUtilsService.copy(stream, target);
        }
    }
}
