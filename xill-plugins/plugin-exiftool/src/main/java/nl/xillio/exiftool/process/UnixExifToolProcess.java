package nl.xillio.exiftool.process;


import java.io.IOException;

/**
 * This is the linux implementation of the {@link ExifToolProcess}.
 *
 * @author Thomas Biesaart
 */
public class UnixExifToolProcess extends AbstractExifToolProcess {

    @Override
    protected Process buildProcess(ProcessBuilder processBuilder) throws IOException {
        processBuilder.command("exiftool", "-stay_open", "True", "-@", "-");
        return processBuilder.start();
    }

    @Override
    public boolean needInit() {
        return false;
    }

    @Override
    public void init() throws IOException {

    }
}
