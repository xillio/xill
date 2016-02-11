package nl.xillio.xill.plugins.file.constructs;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Determines whether a file or folder is hidden or not.
 *
 * @author Anwar
 * @author Thomas biesaart
 */
public class IsHiddenConstruct extends AbstractFlagConstruct {

    @Override
    protected Boolean process(Path path) throws IOException {
        return Files.isHidden(path);
    }
}
