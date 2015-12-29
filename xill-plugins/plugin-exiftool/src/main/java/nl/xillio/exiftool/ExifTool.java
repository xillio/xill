package nl.xillio.exiftool;

import me.biesaart.utils.Log;
import nl.xillio.exiftool.process.ExifToolProcess;
import nl.xillio.exiftool.process.WindowsExifToolProcess;
import nl.xillio.exiftool.query.*;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Consumer;

/**
 * This class represents an easy-to-use facade around the ExifTool wrapper library.
 *
 * @author Thomas Biesaart
 */
public class ExifTool implements AutoCloseable {
    private static final Logger LOGGER = Log.get();

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

    public static ProcessPool buildPool(File binaryLocation) {
        return new ProcessPool(() -> new WindowsExifToolProcess(new File("D:\\temp\\exif.exe")));
    }

    public static ProcessPool buildPool() {
        return buildPool(new File("D:\\temp\\exif.exe"));
    }

    public ExifReadResult readFieldsForFolder(Path path, Projection projection, FolderQueryOptions folderQueryOptions) throws IOException {
        LOGGER.info("Reading tags for files in " + path);

        ScanFolderQuery scanFolderQuery = new ScanFolderQueryImpl(path, projection, folderQueryOptions);

        return scanFolderQuery.run(process);
    }

    public ExifTags readFieldsForFile(Path file, Projection projection, FileQueryOptions options) throws IOException {
        LOGGER.info("Reading tags for " + file);
        ScanFileQuery scanFileQuery = new ScanFileQueryImpl(file, projection, options);
        return scanFileQuery.run(process);
    }
}
