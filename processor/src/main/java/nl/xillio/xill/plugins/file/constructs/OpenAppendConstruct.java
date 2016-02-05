package nl.xillio.xill.plugins.file.constructs;

import nl.xillio.xill.api.io.IOStream;

import java.io.IOException;
import java.nio.file.Path;

/**
 * This construct will open a file handle with write and append permissions.
 *
 * @author Thomas biesaart
 */
public class OpenAppendConstruct extends AbstractOpenConstruct {

    @Override
    protected IOStream open(Path path) throws IOException {
        return fileStreamFactory.openAppend(path);
    }
}
