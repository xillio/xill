package nl.xillio.xill.plugins.file.constructs;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.biesaart.utils.FileUtilsService;
import me.biesaart.utils.IOUtilsService;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * Write content from a source to a target.
 * Returns the number of written bytes.
 *
 * @author Thomas biesaart
 */
@Singleton
public class WriteConstruct extends Construct {

    private final FileUtilsService fileUtilsService;
    private final IOUtilsService ioUtilsService;

    @Inject
    public WriteConstruct(FileUtilsService fileUtilsService, IOUtilsService ioUtilsService) {
        this.fileUtilsService = fileUtilsService;
        this.ioUtilsService = ioUtilsService;
    }

    @Override
    public ConstructProcessor prepareProcess(final ConstructContext context) {
        return new ConstructProcessor(
                (uri, content) -> process(context, uri, content),
                new Argument("target", ATOMIC),
                new Argument("content", NULL, ATOMIC));
    }

    MetaExpression process(final ConstructContext context, final MetaExpression target, final MetaExpression content) {
        // First we open up the streams
        try (OutputStream output = getStream(target, context); InputStream input = getInput(content)) {

            // Then we copy the input stream to the output stream
            long count = ioUtilsService.copyLarge(input, output);
            return fromValue(count);
        } catch (AccessDeniedException e) {
            throw new RobotRuntimeException("Access denied: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new RobotRuntimeException("Could not write to " + target + ": " + e.getMessage(), e);
        }


    }

    private InputStream getInput(MetaExpression content) throws IOException {
        if (content.getBinaryValue().hasInputStream()) {
            return content.getBinaryValue().openInputStream();
        }

        if (content.isNull()) {
            return ioUtilsService.toInputStream("");
        }

        return ioUtilsService.toInputStream(content.getStringValue());
    }

    private OutputStream getStream(MetaExpression target, ConstructContext context) throws IOException {
        if (target.getBinaryValue().hasOutputStream()) {
            return target.getBinaryValue().openOutputStream();
        }

        throw new RobotRuntimeException("Path as target not implemented yet");
    }
}
