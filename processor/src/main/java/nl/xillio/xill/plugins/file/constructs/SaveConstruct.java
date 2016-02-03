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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Save content to a target file or binary location.
 * If the operation fails this will return null.
 * If the target was a file this will return the path to that file.
 * If the target was a binary location this will return true.
 *
 * @author Thomas biesaart
 */
@Singleton
public class SaveConstruct extends Construct {

    private final FileUtilsService fileUtilsService;
    private final IOUtilsService ioUtilsService;

    @Inject
    public SaveConstruct(FileUtilsService fileUtilsService, IOUtilsService ioUtilsService) {
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
            ioUtilsService.copy(input, output);

        } catch (IOException e) {
            throw new RobotRuntimeException("Could not write to " + target + ": " + e.getMessage(), e);
        }

        // Only return the path if we did not write to binary content
        if (target.getBinaryValue().hasOutputStream()) {
            return TRUE;
        } else {
            return fromValue(getFile(context, target.getStringValue()).getAbsolutePath());
        }
    }

    private InputStream getInput(MetaExpression content) throws IOException {
        if (content.getBinaryValue().hasInputStream()) {
            return content.getBinaryValue().openInputStream();
        }

        return ioUtilsService.toInputStream(content.getStringValue());
    }

    private OutputStream getStream(MetaExpression uri, ConstructContext context) throws IOException {
        if (uri.getBinaryValue().hasOutputStream()) {
            return uri.getBinaryValue().openOutputStream();
        }

        File target = getFile(context, uri.getStringValue());

        return fileUtilsService.openOutputStream(target);
    }
}
