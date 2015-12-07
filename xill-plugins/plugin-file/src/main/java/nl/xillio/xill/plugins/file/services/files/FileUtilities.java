package nl.xillio.xill.plugins.file.services.files;

import com.google.inject.ImplementedBy;
import nl.xillio.xill.plugins.file.utils.Folder;
import nl.xillio.xill.services.XillService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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
    void copy(File source, File target) throws IOException;

    /**
     * Create a folder at the specific location if it does not exist
     *
     * @param folder the folder
     * @throws IOException when the operation failed
     */
    boolean createFolder(File folder) throws IOException;

    /**
     * Returns true if the file exists
     *
     * @param file the file to check
     * @return true if and only if the file exists
     */
    boolean exists(File file);

    /**
     * Check the size of a file
     *
     * @param file the file to check
     * @return the size in bytes
     */
    long getByteSize(File file) throws IOException;

    /**
     * Delete a file or folder
     *
     * @param file the file
     * @throws IOException when the operation failed
     */
    void delete(File file) throws IOException;

    /**
     * Create the required folders and save content to a file
     *
     * @param content the content to save
     * @param file    the target file
     * @throws IOException when the operation failed
     */
    void saveStringToFile(String content, File file) throws IOException;

    /**
     * Create the required folders and append content to a file
     *
     * @param content the content to append
     * @param file    the target file
     * @throws IOException when the operation failed
     */
    void appendStringToFile(String content, File file) throws IOException;

    /**
     * Create an Iterator that will cover only files in a specific directory
     *
     * @param folder    the folder to list files from
     * @param recursive if this is set to true the iterator will also contain all files in all subdirectories
     * @return the iterator
     * @throws IOException when the folder does not exist or is not a folder at all
     */
    Iterator<File> iterateFiles(File folder, boolean recursive) throws IOException;

    /**
     * Create an Iterator that will cover only folders in a specific directory
     *
     * @param folder    the folder to list files from
     * @param recursive if this is set to true the iterator will also contain all files in all subdirectories
     * @return the iterator
     * @throws IOException when the folder does not exist or is not a folder at all
     */
    Iterator<Folder> iterateFolders(File folder, boolean recursive) throws IOException;

    /**
     * Determine creation time (ctime) of specified file.
     *
     * @param file the file
     * @return ctime
     * @throws IOException if the file does not exist, file statistics do not work, or some I/O operation failed.
     */
    FileTime getCreationDate(File file) throws IOException;

    /**
     * Determine last modified time (mtime) of specified file.
     *
     * @param file the file
     * @return mtime
     * @throws IOException if the file does not exist, file statistics do not work, or some I/O operation failed.
     */
    FileTime getLastModifiedDate(File file) throws IOException;

    /**
     * Verifies whether an authenticated user has read access to a specified file/folder.
     *
     * @param file The associated file object.
     * @return True if an authenticated user has read access to the specified file. Otherwise, false.
     * @throws FileNotFoundException If the file does not exist.
     */
    boolean canRead(File file) throws FileNotFoundException;

    /**
     * Verifies whether an authenticated user has write access to a specified file/folder.
     *
     * @param file The associated file object.
     * @return True if an authenticated user has write access to a specified file. Otherwise, false.
     */
    boolean canWrite(File file) throws FileNotFoundException;

    /**
     * Verifies whether an authenticated user has executability rights to the specified file/folder.
     *
     * @param file The associated file object.
     * @return True if an authenticated user has executability rights to a specified file. Otherwise, false.
     * @throws FileNotFoundException If the file does not exist.
     */
    boolean canExecute(File file) throws FileNotFoundException;

    /**
     * Verifies whether a file/folder is hidden or not.
     *
     * @param file The associated file object.
     * @return True if the file is hidden. Otherwise, false.
     * @throws IOException
     */
    boolean isHidden(File file) throws IOException;
}
