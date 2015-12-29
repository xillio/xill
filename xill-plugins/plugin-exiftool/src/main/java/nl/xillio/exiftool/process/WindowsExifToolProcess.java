package nl.xillio.exiftool.process;


import nl.xillio.exiftool.ProcessPool;

import java.io.File;
import java.io.IOException;

/**
 * This is the Windows implementation of the {@link ExifToolProcess}.
 *
 * @author Thomas Biesaart
 */
public class WindowsExifToolProcess extends AbstractExifToolProcess {

    public WindowsExifToolProcess(File nativeBinary) {
        super(nativeBinary, ProcessPool.class.getResource("/exiftool.exe"));
    }

    @Override
    protected Process buildProcess(ProcessBuilder processBuilder) throws IOException {
        processBuilder.command(getNativeBinary().toAbsolutePath().toString(), "-stay_open", "True", "-@", "-");
        return processBuilder.start();
    }
}
