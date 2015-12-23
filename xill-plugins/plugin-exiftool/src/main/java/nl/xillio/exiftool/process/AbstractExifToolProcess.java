package nl.xillio.exiftool.process;


import me.biesaart.utils.Log;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;

/**
 * This class represents the base implementation of the ExifToolProcess.
 *
 * @author Thomas Biesaart
 */
abstract class AbstractExifToolProcess implements ExifToolProcess {
    private static final Logger LOGGER = Log.get();
    private final Path nativeBinary;
    private final URL embeddedBinaryPath;
    private Status status = Status.NEW;
    private Process process;
    private IOStream streams;

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
        status = Status.CLOSING;
        streams.close();
        process.destroy();
        status = Status.CLOSED;
    }

    @Override
    public void start() throws IOException {
        if (status != Status.NEW) {
            throw new IllegalStateException("This process is not new. It is either running or has been closed");
        }
        LOGGER.debug("Starting exiftool process");

        status = Status.STARTING;

        try {
            deploy();
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command(nativeBinary.toAbsolutePath().toString(), "-stay_open", "True", "-@", "-");

            this.process = buildProcess(processBuilder);
            this.streams = new IOStream(process);
        } finally {
            // Set status to new in case of errors
            status = Status.NEW;
        }

        // We are done!
        status = Status.IDLE;
    }

    @Override
    public ExecutionResult run(String... arguments) throws IOException {
        requireRunning();

        LOGGER.debug("Running exiftool {}", Arrays.toString(arguments));

        status = Status.RUNNING;

        // Remove all old data
        //streams.flushReader();

        LOGGER.debug("Sending arguments");
        // Send arguments
        for (String argument : arguments) {
            streams.getWriter().write(argument + "\n");
        }
        streams.getWriter().write("-execute\n");
        streams.getWriter().flush();

        return new ExecutionResult(streams.getReader(), () -> status = Status.IDLE, "{ready}");
    }

    private void requireRunning() throws IOException {
        if (status == Status.NEW) {
            start();
        }

        if (!isAvailable()) {
            throw new IllegalStateException("This process is not available!");
        }
    }


    protected abstract Process buildProcess(ProcessBuilder processBuilder) throws IOException;

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
