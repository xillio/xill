package nl.xillio.xill.plugins.file.services;

import com.google.inject.ImplementedBy;
import nl.xillio.xill.plugins.file.services.files.FileUtilitiesImpl;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;

/**
 * This interface represents an object that is capable of providing iterators for files.
 *
 * @author Thomas biesaart
 */
@ImplementedBy(FileUtilitiesImpl.class)
public interface FileSystemIterator {

    /**
     * Create an Iterator that will cover only files in a specific directory
     *
     * @param folder    the folder to list files from
     * @param recursive if this is set to true the iterator will also contain all files in all subdirectories
     * @return the iterator
     * @throws IOException when the folder does not exist or is not a folder at all
     */
    Iterator<Path> iterateFiles(Path folder, boolean recursive) throws IOException;

    /**
     * Create an Iterator that will cover only folders in a specific directory
     *
     * @param folder    the folder to list files from
     * @param recursive if this is set to true the iterator will also contain all files in all subdirectories
     * @return the iterator
     * @throws IOException when the folder does not exist or is not a folder at all
     */
    Iterator<Path> iterateFolders(Path folder, boolean recursive) throws IOException;
}
