package nl.xillio.xill.services.files;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.errors.RobotRuntimeException;

import java.io.File;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static nl.xillio.xill.api.components.ExpressionBuilderHelper.fromValue;

/**
 * <p>
 * This class is the main implementation of the FileResolver.
 * </p>
 *
 * @author Thomas Biesaart
 * @since 5-8-2015
 */
public class FileResolverImpl implements FileResolver {

    @Override
    public Path buildPath(ConstructContext context, MetaExpression path) {
        //First check if the provided path is absolute
        if (path.isNull()) {
            throw new RobotRuntimeException("Provided path cannot be null");
        }
        Path file = tryPath(path.getStringValue());
        if (!file.isAbsolute()) {
            //It's not absolute so we make it relative to the robot
            file = context.getRobotID().getProjectPath().toPath().resolve(file);
        }
        return file.normalize().toAbsolutePath();
    }

    @Override
    public File buildFile(ConstructContext context, String path) {
        try (MetaExpression expression = fromValue(path)) {
            return buildPath(context, expression).toFile();
        }
    }

    private Path tryPath(String path) {
        try {
            return Paths.get(path);
        } catch (InvalidPathException e) {
            throw new RobotRuntimeException("Invalid path: " + e.getMessage(), e);
        }
    }
}
