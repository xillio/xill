package nl.xillio.xill.plugins.file.constructs;

import com.google.inject.Inject;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.file.services.files.FileUtilities;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Tests whether the file denoted by this abstract pathname is a folder.
 *
 * @author Paul van der Zandt, Xillio
 */
public class IsFolderConstruct extends Construct {

    @Inject
    private FileUtilities fileUtilities;

    @Override
    public ConstructProcessor prepareProcess(ConstructContext context) {
        return new ConstructProcessor(
                uri -> process(context, fileUtilities, uri),
                new Argument("uri", ATOMIC)
        );
    }

    static MetaExpression process(final ConstructContext constructContext, final FileUtilities fileUtilities,
                                  final MetaExpression uri) {
        try {
            Path file = getPath(constructContext, uri);
            return fromValue(fileUtilities.isFolder(file));
        } catch (IOException e) {
            throw new RobotRuntimeException("File not found, or not accessible", e);
        }
    }
}
