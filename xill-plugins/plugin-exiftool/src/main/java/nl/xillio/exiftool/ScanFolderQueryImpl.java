package nl.xillio.exiftool;

import nl.xillio.exiftool.process.ExecutionResult;
import nl.xillio.exiftool.process.ExifToolProcess;
import nl.xillio.exiftool.query.ExifReadResult;
import nl.xillio.exiftool.query.FolderQueryOptions;
import nl.xillio.exiftool.query.Projection;
import nl.xillio.exiftool.query.ScanFolderQuery;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the default implementation of the ScanFolderQuery.
 *
 * @author Thomas Biesaart
 */
public class ScanFolderQueryImpl implements ScanFolderQuery {

    private final Path folder;
    private final Projection projection;
    private final FolderQueryOptions folderQueryOptions;

    public ScanFolderQueryImpl(Path folder, Projection projection, FolderQueryOptions folderQueryOptions) throws NoSuchFileException {
        this.folder = folder;
        this.projection = projection;
        this.folderQueryOptions = folderQueryOptions;

        if (!Files.exists(folder)) {
            throw new NoSuchFileException("Could not find file " + folder);
        }

        if (!Files.isDirectory(folder)) {
            throw new IllegalArgumentException(folder + " is not a folder");
        }
    }

    @Override
    public List<String> buildExifArguments() {
        List<String> result = new ArrayList<>();
        result.add(folder.toAbsolutePath().toString());
        if (folderQueryOptions.isRecursive()) {
            result.add("-r");
        }
        result.add("-ext");
        result.add(folderQueryOptions.getExtensionFilter());
        result.addAll(projection.buildArguments());

        return result;
    }

    @Override
    public ExifReadResult run(ExifToolProcess process) throws IOException {
        List<String> arguments = buildExifArguments();
        ExecutionResult executionResult = process.run(arguments.toArray(new String[arguments.size()]));
        return new ExifReadResultImpl(executionResult, 100, folderQueryOptions.getTagNameConvention());
    }
}
