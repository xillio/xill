package nl.xillio.exiftool.process;


import java.io.IOException;

/**
 * This is the osx implementation of the {@link ExifToolProcess}.
 *
 * @author Thomas Biesaart
 */
public class OSXExifToolProcess extends AbstractExifToolProcess {

    @Override
    protected Process buildProcess(ProcessBuilder processBuilder) throws IOException {
        processBuilder.command("/usr/bin/exiftool", "-stay_open", "True", "-@", "-");
        return processBuilder.start();
    }

    @Override
    public boolean needInit() {
        return false;
    }

    @Override
    public void init() throws IOException {
        // No need to deploy. We just need the exiftool installed
    }
}
