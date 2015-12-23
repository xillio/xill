package nl.xillio.exiftool;

import me.biesaart.utils.Log;
import nl.xillio.exiftool.process.ExifToolProcess;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * This class represents a pool that can run low level actions on available processes.
 *
 * @author Thomas Biesaart
 */
public class ProcessPool implements AutoCloseable {
    private static final Logger LOGGER = Log.get();
    /**
     * These are all the processes that have been built.
     */
    private List<ExifToolProcess> processes = new ArrayList<>();
    /**
     * These processes have been given away but not yet returned.
     */
    private List<ExifToolProcess> leasedProcesses = new ArrayList<>();
    private final Supplier<ExifToolProcess> processBuilder;

    public ProcessPool(Supplier<ExifToolProcess> processBuilder) {
        this.processBuilder = processBuilder;
    }

    public synchronized ExifTool getAvailable() {
        ExifToolProcess process = processes.stream()
                .filter(proc -> !leasedProcesses.contains(proc))
                .findAny()
                .orElse(null);

        if (process == null) {
            process = createNew();
        }

        leasedProcesses.add(process);

        LOGGER.info("Giving out {}", process);

        return new ExifTool(process, this::release);
    }

    private ExifToolProcess createNew() {
        LOGGER.info("Creating new exiftool process");
        ExifToolProcess process = processBuilder.get();
        processes.add(process);
        return process;
    }

    private void release(ExifToolProcess process) {
        LOGGER.info("Releasing {}", process);
        leasedProcesses.remove(process);
    }

    @Override
    public void close() {
        processes.forEach(ExifToolProcess::close);
    }
}
