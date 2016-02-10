package nl.xillio.xill.plugins.file.constructs;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Tests whether the file denoted by this abstract pathname is a folder.
 *
 * @author Paul van der Zandt, Xillio
 * @author Thomas biesaart
 */
public class IsFolderConstruct extends AbstractFlagConstruct {

    @Override
    protected Boolean process(Path path) throws IOException {
        return Files.isDirectory(path);
    }
}
