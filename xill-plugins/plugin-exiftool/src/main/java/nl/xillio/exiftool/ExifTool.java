package nl.xillio.exiftool;

import me.biesaart.utils.Log;
import nl.xillio.exiftool.process.ExecutionResult;
import nl.xillio.exiftool.process.ExifToolProcess;
import nl.xillio.exiftool.process.WindowsExifToolProcess;
import nl.xillio.exiftool.query.ExifReadResult;
import nl.xillio.exiftool.query.ExifTags;
import nl.xillio.exiftool.query.Projection;
import nl.xillio.exiftool.query.ScanFolderQuery;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
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

    public ExifTags readFieldsForFile(Path path) throws IOException {
        LOGGER.info("Reading all fields of " + path);

        if (!Files.exists(path)) {
            throw new NoSuchFileException("Could not find file " + path);
        }

        if (!Files.isRegularFile(path)) {
            throw new IllegalArgumentException(path + " is not a file");
        }

        Iterator<String> lines = tryRun(Collections.singletonList(path.toAbsolutePath().toString()));
        ExifTags result = new ExifTagsImpl();

        while (lines.hasNext()) {
            String line = lines.next();

            int separator = line.indexOf(TAG_SEPARATOR);

            if (separator == -1) {
                LOGGER.error("Failed to parse [{}] as a field", line);
                continue;
            }

            String key = line.substring(0, separator).trim();
            String value = line.substring(separator + TAG_SEPARATOR.length()).trim();

            result.put(key, value);
        }

        return result;
    }

    private ExecutionResult tryRun(List<String> arguments) {
        try {
            return process.run(arguments.toArray(new String[arguments.size()]));
        } catch (IOException e) {
            throw new IllegalStateException("Failed to run arguments", e);
        }
    }

    public ExifReadResult readFieldsForFolder(Path path, Projection projection) throws IOException {
        LOGGER.info("Reading all fields of files in " + path);

        ScanFolderQuery scanFolderQuery = new ScanFolderQueryImpl(path, projection);

        return scanFolderQuery.run(process);
    }
}
