package nl.xillio.xill.plugins.file.constructs;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;

/**
 * This construct will extract the last modification date from a file.
 *
 * @author Folkert van Verseveld
 * @author Thomas biesaart
 */
public class GetLastModifiedDate extends AbstractDateFilePropertyConstruct {

    @Override
    protected FileTime process(Path path) throws IOException {
        return attributes(path).creationTime();
    }
}
