package nl.xillio.xill.plugins.file.constructs;

import nl.xillio.xill.api.io.IOStream;

import java.io.IOException;
import java.nio.file.Path;

/**
 * This construct will open a file handle with write permissions.
 *
 * @author Thomas biesaart
 */
public class OpenWriteConstruct extends AbstractOpenConstruct {

    @Override
    protected IOStream open(Path path) throws IOException {
        return fileStreamFactory.openWrite(path);
    }
}
