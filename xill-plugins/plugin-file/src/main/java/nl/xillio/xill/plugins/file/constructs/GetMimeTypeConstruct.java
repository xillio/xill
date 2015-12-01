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

@Singleton
public class GetMimeTypeConstruct extends Construct {

    @Override
    public ConstructProcessor prepareProcess(final ConstructContext context) {
        return new ConstructProcessor(
                (uri) -> process(context, uri),
                new Argument("uri", ATOMIC)
        );
    }

    private static MetaExpression process(final ConstructContext context, final MetaExpression uri) {
        File file = getFile(context, uri.getStringValue());

        try {
            // The result is either null or a string containing the MIME type.
            String result = Files.probeContentType(file.toPath());
            return MetaExpression.parseObject(result);
        } catch (IOException | SecurityException e) {
            throw new RobotRuntimeException("Failed to read MIME type: " + e.getMessage(), e);
        }
    }
}
