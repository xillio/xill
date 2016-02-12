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
import nl.xillio.xill.api.io.IOStream;
import nl.xillio.xill.plugins.file.services.files.FileStreamFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;

/**
 * Read text from a plain text file using the given encoding.
 *
 * @author Thomas biesaart
 */
@Singleton
public class GetTextConstruct extends Construct {

    private final FileStreamFactory fileStreamFactory;
    private final IOUtilsService ioUtilsService;

    @Inject
    GetTextConstruct(FileStreamFactory fileStreamFactory, IOUtilsService ioUtilsService) {
        this.fileStreamFactory = fileStreamFactory;
        this.ioUtilsService = ioUtilsService;
    }


    @Override
    public ConstructProcessor prepareProcess(final ConstructContext context) {
        return new ConstructProcessor(
                (file, encoding) -> process(context, file, encoding),
                new Argument("input", ATOMIC),
                new Argument("encoding", NULL, ATOMIC));
    }

    MetaExpression process(final ConstructContext context, final MetaExpression source, final MetaExpression encoding) {
        // Get the charset safely, if it was given
        Charset charset = getCharset(encoding);

        // Else read the provided path
        Path path = getPath(context, source);
        return fromValue(toString(buildStream(path), charset));
    }

    private IOStream buildStream(Path path) {
        try {
            return fileStreamFactory.openRead(path);
        } catch (IOException e) {
            throw new RobotRuntimeException("Failed to open file " + path + ": " + e.getMessage(), e);
        }
    }

    private String toString(IOStream stream, Charset charset) {
        try {
            return ioUtilsService.toString(stream.getInputStream(), charset);
        } catch (IOException e) {
            throw new RobotRuntimeException("Failed to get text: " + e.getMessage(), e);
        }
    }

    private Charset getCharset(MetaExpression encoding) {
        if (!encoding.isNull()) {
            try {
                return Charset.forName(encoding.getStringValue());
            } catch (IllegalArgumentException e) {
                throw new RobotRuntimeException("Encoding not supported: " + e.getMessage(), e);
            }
        }

        return Charset.defaultCharset();
    }
}
