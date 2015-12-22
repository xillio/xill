package nl.xillio.exiftool.process;


import nl.xillio.exiftool.ProcessPool;

import java.io.File;
import java.io.IOException;

public class WindowsExifToolProcess extends AbstractExifToolProcess {

    public WindowsExifToolProcess(File nativeBinary) {
        super(nativeBinary, ProcessPool.class.getResource("/exiftool.exe"));
    }

    @Override
    protected Process buildProcess(ProcessBuilder processBuilder) throws IOException {
        return processBuilder.start();
    }
}
