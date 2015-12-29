package nl.xillio.exiftool.process;


import java.io.File;
import java.io.IOException;

/**
 * This is the osx implementation of the {@link ExifToolProcess}.
 *
 * @author Thomas Biesaart
 */
public class OSXExifToolProcess extends AbstractExifToolProcess {

    public OSXExifToolProcess(File nativeBinary) {
        super(nativeBinary, null);
    }

    @Override
    protected Process buildProcess(ProcessBuilder processBuilder) throws IOException {
        processBuilder.command("exiftool", "-stay_open", "True", "-@", "-");
        return processBuilder.start();
    }
}
