package nl.xillio.xill.plugins.file.services.files;

import com.google.inject.Singleton;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.api.io.IOStream;
import nl.xillio.xill.api.io.SimpleIOStream;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * This class builds IOStreams from Paths.
 *
 * @author Thomas biesaart
 */
@Singleton
public class FileStreamFactory {

    public IOStream openAppend(Path path) throws IOException {
        assertNotDirectory(path, "append");

        if (!Files.isWritable(path) && Files.exists(path)) {
            throw new RobotRuntimeException("Cannot append to " + path);
        }

        if (path.getParent() != null) {
            Files.createDirectories(path.getParent());
        }

        return new SimpleIOStream(Files.newInputStream(path, StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.APPEND), path.toString());
    }

    public IOStream openRead(Path path) throws IOException {
        assertNotDirectoryAndExists(path, "read");


        if (!Files.isReadable(path)) {
            throw new RobotRuntimeException(path + " is not readable");
        }

        return new SimpleIOStream(Files.newInputStream(path, StandardOpenOption.READ), path.toString());

    }

    public IOStream openWrite(Path path) throws IOException {
        assertNotDirectory(path, "write");

        if (!Files.isWritable(path) && Files.exists(path)) {
            throw new RobotRuntimeException("Cannot write to " + path);
        }

        if (path.getParent() != null) {
            Files.createDirectories(path.getParent());
        }

        return new SimpleIOStream(Files.newOutputStream(path, StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING), path.toString());
    }

    private void assertNotDirectoryAndExists(Path target, String targetType) {
        assertExists(target);

        assertNotDirectory(target, targetType);
    }

    private void assertNotDirectory(Path target, String targetType) {
        if (Files.isDirectory(target)) {
            throw new RobotRuntimeException("Cannot create " + targetType + " target for a directory");
        }
    }

    private void assertExists(Path target) {
        if (!Files.exists(target)) {
            throw new RobotRuntimeException("Could not find " + target);
        }
    }
}
