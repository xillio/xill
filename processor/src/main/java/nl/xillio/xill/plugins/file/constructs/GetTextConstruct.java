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
import nl.xillio.xill.plugins.stream.utils.StreamUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.regex.Pattern;

/**
 * Read text from a plain text file using the given encoding.
 *
 * @author Thomas biesaart
 */
@Singleton
public class GetTextConstruct extends Construct {
    private static final Pattern LEADING_BOM_PATTERN = Pattern.compile("^\uFEFF+");

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
        // Get the charset safely, if it was given.
        Charset charset = StreamUtils.getCharset(encoding);

        // Read the provided path.
        Path path = getPath(context, source);
        String text = toString(buildStream(path), charset);

        // Remove leading BOM characters.
        text = LEADING_BOM_PATTERN.matcher(text).replaceFirst("");

        return fromValue(text);
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
}
