package nl.xillio.xill.plugins.file.constructs;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Check if an existing file is writable.
 *
 * @author Thomas biesaart
 */
public class CanWriteConstruct extends AbstractFlagConstruct {

    @Override
    protected Boolean process(Path path) throws IOException {
        return Files.isWritable(path);
    }
}

