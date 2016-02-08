package nl.xillio.xill.plugins.file.constructs;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;

/**
 * Construct for accessing creating time (ctime) on a file system.
 *
 * @author Folkert van Verseveld
 * @author Thomas biesaart
 */
public class GetCreationDate extends AbstractDateFilePropertyConstruct {

    @Override
    protected FileTime process(Path path) throws IOException {
        return attributes(path).creationTime();
    }
}
