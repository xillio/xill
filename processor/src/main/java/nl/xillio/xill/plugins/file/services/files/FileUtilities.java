package nl.xillio.xill.plugins.file.services.files;

import com.google.inject.ImplementedBy;
import nl.xillio.xill.services.XillService;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.Iterator;

/**
 * This {@link XillService} is responsible for various file operations
 */
@ImplementedBy(FileUtilitiesImpl.class)
public interface FileUtilities extends XillService {

    /**
     * Copy a source file to a target destination, overwriting it if it exists
     *
     * @param source the source file
     * @param target the target file
     * @throws IOException when the operation failed
     */
    void copy(Path source, Path target) throws IOException;

    /**
     * Create a folder at the specific location if it does not exist
     *
     * @param folder the folder
     * @throws IOException when the operation failed
     */
    void createFolder(Path folder) throws IOException;

    /**
     * Returns true if the file exists
     *
     * @param file the file to check
     * @return true if and only if the file exists
     */
    boolean exists(Path file);

    /**
     * Check the size of a file
     *
     * @param file the file to check
     * @return the size in bytes
     */
    long getByteSize(Path file) throws IOException;

    /**
     * Delete a file or folder
     *
     * @param file the file
     * @throws IOException when the operation failed
     */
    void delete(Path file) throws IOException;

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

    /**
     * Determine creation time of specified file.
     *
     * @param file the file
     * @return the time the file was created
     * @throws IOException if the file does not exist, file statistics do not work, or some I/O operation failed.
     */
    FileTime getCreationDate(Path file) throws IOException;

    /**
     * Determine last modified time of specified file.
     *
     * @param file the file
     * @return the time the file was last modified
     * @throws IOException if the file does not exist, file statistics do not work, or some I/O operation failed.
     */
    FileTime getLastModifiedDate(Path file) throws IOException;
}
