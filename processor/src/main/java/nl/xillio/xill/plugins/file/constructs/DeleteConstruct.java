package nl.xillio.xill.plugins.file.constructs;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.OperationFailedException;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.file.services.files.FileUtilities;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Path;

/**
 * This class deletes a file or folder. Returns the absolute path to the folder
 */
@Singleton
public class DeleteConstruct extends Construct {

    @Inject
    private FileUtilities fileUtils;

    @Override
    public ConstructProcessor prepareProcess(final ConstructContext context) {
        return new ConstructProcessor(
                uri -> process(context, fileUtils, uri),
                new Argument("uri", ATOMIC));
    }

    static MetaExpression process(final ConstructContext context, final FileUtilities fileUtils, final MetaExpression uri) {

        Path file = getPath(context, uri);
        try {
            fileUtils.delete(file);
        } catch (AccessDeniedException e) {
            throw new OperationFailedException("delete the target", "Access is denied to " + e.getFile(), e);
        } catch (IOException e) {
            throw new OperationFailedException("delete " + file.toAbsolutePath(), e.getMessage(), e);
        }

        return fromValue(file.toString());
    }
}
