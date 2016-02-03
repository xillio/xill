package nl.xillio.xill.services.files;

import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.errors.RobotRuntimeException;

import java.io.File;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * <p>
 * This class is the main implementation of the FileResolver
 * </p>
 *
 * @author Thomas Biesaart
 * @since 5-8-2015
 */
public class FileResolverImpl implements FileResolver {

    @Override
    public Path buildPath(ConstructContext context, String path) {
        //First check if the provided path is absolute
        Path file = tryPath(path);
        if (!file.isAbsolute()) {
            //It's not absolute so we make it relative to the robot
            file = context.getRobotID().getProjectPath().toPath().resolve(file);
        }
        return file;
    }

    @Override
    public File buildFile(ConstructContext context, String path) {
        return buildPath(context, path).toFile();
    }

    private Path tryPath(String path) {
        try {
            return Paths.get(path);
        } catch (InvalidPathException e) {
            throw new RobotRuntimeException("Invalid path: " + e.getMessage(), e);
        }
    }
}
