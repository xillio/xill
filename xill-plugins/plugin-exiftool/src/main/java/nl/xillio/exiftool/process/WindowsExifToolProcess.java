package nl.xillio.exiftool.process;


import nl.xillio.exiftool.ProcessPool;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

/**
 * This is the Windows implementation of the {@link ExifToolProcess}.
 *
 * @author Thomas Biesaart
 */
public class WindowsExifToolProcess extends AbstractExifToolProcess {
    private static final URL EMBEDDED_BINARY = ProcessPool.class.getResource("/exiftool.exe");
    private final Path nativeBinary;

    public WindowsExifToolProcess(Path nativeBinary) {
        Objects.nonNull(nativeBinary);
        this.nativeBinary = nativeBinary.toAbsolutePath();
    }

    @Override
    public boolean needInit() {
        return Files.exists(nativeBinary);
    }


    @Override
    public void init() throws IOException {
        if (needInit()) {
            return;
        }

        try (InputStream stream = EMBEDDED_BINARY.openStream()) {
            Files.copy(stream, nativeBinary, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    @Override
    protected Process buildProcess(ProcessBuilder processBuilder) throws IOException {
        processBuilder.command(nativeBinary.toString(), "-stay_open", "True", "-@", "-");
        return processBuilder.start();
    }
}
