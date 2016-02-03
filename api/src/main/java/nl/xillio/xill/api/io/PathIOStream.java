package nl.xillio.xill.api.io;

import me.biesaart.utils.ArrayUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Objects;

/**
 * This class represents an IOStream implementation that references a path.
 *
 * @author Thomas biesaart
 */
public class PathIOStream implements IOStream {
    private final Path path;
    private OpenOption[] openOptions;

    public PathIOStream(Path path, OpenOption... openOptions) {
        this.openOptions = openOptions;
        Objects.requireNonNull(path);
        this.path = path;
    }

    @Override
    public boolean hasInputStream() {
        return ArrayUtils.contains(openOptions, StandardOpenOption.READ);
    }

    @Override
    public InputStream openInputStream() throws IOException {
        return Files.newInputStream(path, openOptions);
    }

    @Override
    public boolean hasOutputStream() {
        return ArrayUtils.contains(openOptions, StandardOpenOption.WRITE);
    }

    @Override
    public OutputStream openOutputStream() throws IOException {
        if (path.getParent() != null) {
            Files.createDirectories(path.getParent());
        }

        return Files.newOutputStream(path, openOptions);
    }
}
