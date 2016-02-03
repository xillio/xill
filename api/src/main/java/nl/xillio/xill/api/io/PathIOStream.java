package nl.xillio.xill.api.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * This class represents an IOStream implementation that references a path.
 *
 * @author Thomas biesaart
 */
public class PathIOStream implements IOStream {
    private final Path path;
    private final List<OpenOption> writeOpenOptions;
    private final List<OpenOption> readOpenOptions;

    public PathIOStream(Path path) {
        Objects.requireNonNull(path);
        this.writeOpenOptions = Arrays.asList(StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        this.readOpenOptions = Collections.singletonList(StandardOpenOption.READ);
        this.path = path;
    }

    @Override
    public boolean hasInputStream() {
        return true;
    }

    @Override
    public InputStream openInputStream() throws IOException {
        return Files.newInputStream(path, readOpenOptions.toArray(new OpenOption[readOpenOptions.size()]));
    }

    @Override
    public boolean hasOutputStream() {
        return true;
    }

    @Override
    public OutputStream openOutputStream() throws IOException {
        return Files.newOutputStream(path, writeOpenOptions.toArray(new OpenOption[writeOpenOptions.size()]));
    }
}
