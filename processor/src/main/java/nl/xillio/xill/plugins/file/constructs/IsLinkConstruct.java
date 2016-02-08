package nl.xillio.xill.plugins.file.constructs;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Tests whether a file is a symbolic link.
 *
 * @author Paul van der Zandt, Xillio
 * @author Thomas biesaart
 */
public class IsLinkConstruct extends AbstractFlagConstruct {

    @Override
    protected Boolean process(Path path) throws IOException {
        return Files.isSymbolicLink(path);
    }
}
