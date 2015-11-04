package nl.xillio.xill.plugins.file.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;

/**
 * This class iterates over files and provides information about every file
 */
public class FolderIterator extends FileSystemIterator implements Iterator<Folder> {

    /**
     * Create a new FileIterator and add the rootFolder to the stream
     *
     * @param rootFolder the root folder
     * @param recursive  weather the stream should also list files in sub folders
     * @throws IOException if the rootFolder does not exist
     */
    public FolderIterator(File rootFolder, boolean recursive) throws IOException {
        super(rootFolder, recursive, path -> Files.isDirectory(path));
    }

    @Override
    public Folder next() {
        Path next = getNextValue();
        return new Folder(next.toString());
    }
}
