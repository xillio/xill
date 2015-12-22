package nl.xillio.exiftool.process;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * This class represents the base implementation of the ExifToolProcess.
 *
 * @author Thomas Biesaart
 */
abstract class AbstractExifToolProcess implements ExifToolProcess {
    private final Path nativeBinary;
    private final URL embeddedBinaryPath;
    private Status status = Status.NEW;
    private Process process;

    protected AbstractExifToolProcess(File nativeBinary, URL embeddedBinaryPath) {
        this.embeddedBinaryPath = embeddedBinaryPath;
        this.nativeBinary = nativeBinary.toPath();
    }

    @Override
    public boolean isDeployed() {
        return Files.exists(nativeBinary);
    }

    @Override
    public void deploy() throws IOException {
        if (isDeployed()) {
            return;
        }

        try (InputStream stream = embeddedBinaryPath.openStream()) {
            Files.copy(stream, nativeBinary, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    @Override
    public void close() {

    }

    @Override
    public void run() {
        if (status != Status.NEW) {
            throw new IllegalStateException("This process is not new. It is either running or has been closed");
        }

        status = Status.STARTING;

        ProcessBuilder processBuilder = new ProcessBuilder();
        this.process = buildProcess(processBuilder);

        status = Status.IDLE;
    }

    protected abstract Process buildProcess(ProcessBuilder processBuilder);

    @Override
    public boolean isRunning() {
        return status == Status.RUNNING;
    }

    @Override
    public boolean isClosed() {
        return status == Status.CLOSED;
    }

    @Override
    public boolean isAvailable() {
        return status == Status.IDLE;
    }

    protected Path getNativeBinary() {
        return nativeBinary;
    }

    private enum Status {
        NEW,
        STARTING,
        RUNNING,
        IDLE,
        CLOSING,
        CLOSED
    }
}
