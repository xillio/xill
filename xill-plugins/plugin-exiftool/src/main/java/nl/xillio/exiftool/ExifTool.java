package nl.xillio.exiftool;

import me.biesaart.utils.Log;
import nl.xillio.exiftool.process.ExifToolProcess;
import nl.xillio.exiftool.process.WindowsExifToolProcess;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * This class represents an easy-to-use facade around the ExifTool wrapper library.
 *
 * @author Thomas Biesaart
 */
public class ExifTool implements AutoCloseable {
    private static final Logger LOGGER = Log.get();
    private static final String TAG_SEPARATOR = ":";

    private final ExifToolProcess process;
    private final Consumer<ExifToolProcess> releaseMethod;

    ExifTool(ExifToolProcess process, Consumer<ExifToolProcess> releaseMethod) {
        this.process = process;
        this.releaseMethod = releaseMethod;
    }

    @Override
    public void close() {
        LOGGER.debug("Releasing exiftool process");
        releaseMethod.accept(process);
    }

    public static ProcessPool buildPool() {
        return new ProcessPool(() -> new WindowsExifToolProcess(new File("D:\\TMP\\exif.exe")));
    }

    public Map<String, String> readFields(File file) {
        LOGGER.info("Reading all fields of " + file);

        List<String> lines = tryRun(file.getAbsolutePath());
        Map<String, String> result = new HashMap<>();

        for(String line : lines) {
            int separator = line.indexOf(TAG_SEPARATOR);

            if(separator == -1) {
                LOGGER.error("Failed to parse [{}] as a field", line);
                continue;
            }

            String key = line.substring(0, separator).trim();
            String value = line.substring(separator + TAG_SEPARATOR.length()).trim();

            result.put(key, value);
        }

        return result;
    }

    private List<String> tryRun(String... arguments) {
        try {
            return process.run(arguments);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to run arguments", e);
        }
    }
}
