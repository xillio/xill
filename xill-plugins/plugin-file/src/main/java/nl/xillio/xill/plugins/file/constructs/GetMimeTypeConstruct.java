package nl.xillio.xill.plugins.file.constructs;

import com.google.inject.Singleton;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Singleton
public class GetMimeTypeConstruct extends Construct {

    @Override
    public ConstructProcessor prepareProcess(final ConstructContext context) {
        return new ConstructProcessor(
                uri -> process(this, context, uri),
                new Argument("uri", ATOMIC)
        );
    }

    static MetaExpression process(final GetMimeTypeConstruct construct, final ConstructContext context, final MetaExpression uri) {
        File file = getFile(context, uri.getStringValue());

        try {
            // The result is either null or a string containing the MIME type.
            return MetaExpression.parseObject(construct.getMimeType(file.toPath()));
        } catch (IOException | SecurityException e) {
            throw new RobotRuntimeException("Failed to read MIME type: " + e.getMessage(), e);
        }
    }

    String getMimeType(Path path) throws java.io.IOException {
        return Files.probeContentType(path);
    }
}
