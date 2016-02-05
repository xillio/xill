package nl.xillio.xill.plugins.file.services.files;

import com.google.inject.ImplementedBy;
import nl.xillio.xill.services.XillService;

import java.io.FileNotFoundException;
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
     * Determine creation time (ctime) of specified file.
     *
     * @param file the file
     * @return ctime
     * @throws IOException if the file does not exist, file statistics do not work, or some I/O operation failed.
     */
    FileTime getCreationDate(Path file) throws IOException;

    /**
     * Determine last modified time (mtime) of specified file.
     *
     * @param file the file
     * @return mtime
     * @throws IOException if the file does not exist, file statistics do not work, or some I/O operation failed.
     */
    FileTime getLastModifiedDate(Path file) throws IOException;

    /**
     * Verifies whether an authenticated user has read access to a specified file/folder.
     *
     * @param file The associated file object.
     * @return True if an authenticated user has read access to the specified file. Otherwise, false.
     * @throws FileNotFoundException If the file does not exist.
     */
    boolean canRead(Path file) throws IOException;

    /**
     * Verifies whether an authenticated user has write access to a specified file/folder.
     *
     * @param file The associated file object.
     * @return True if an authenticated user has write access to a specified file. Otherwise, false.
     */
    boolean canWrite(Path file) throws IOException;

    /**
     * Verifies whether an authenticated user has executability rights to the specified file/folder.
     *
     * @param file The associated file object.
     * @return True if an authenticated user has executability rights to a specified file. Otherwise, false.
     * @throws FileNotFoundException If the file does not exist.
     */
    boolean canExecute(Path file) throws IOException;

    /**
     * Verifies whether a file/folder is hidden or not.
     *
     * @param file The associated file object.
     * @return True if the file is hidden. Otherwise, false.
     * @throws IOException
     */
    boolean isHidden(Path file) throws IOException;

    /**
     * Tests whether the file denoted by this abstract pathname is a normal file. A file is normal if it is not a
     * directory and, in addition, satisfies other system-dependent criteria. Any non-directory file created by a Java
     * application is guaranteed to be a normal file. This method does follow symbolic links.
     *
     * @param file The associated file object.
     * @return true if and only if the file denoted by this abstract pathname exists and is a normal file; false otherwise
     * @throws IOException
     */
    boolean isFile(Path file) throws IOException;

    /**
     * Tests whether the file denoted by this abstract pathname is a folder. This method does follow symbolic links.
     *
     * @param file The associated file object.
     * @return true if and only if the file denoted by this abstract pathname exists and is a directory; false otherwise
     * @throws IOException
     */
    boolean isFolder(Path file) throws IOException;

    /**
     * Tests whether a file is a symbolic link.
     *
     * @param file The associated file object.
     * @return true if the file is a symbolic link; false if the file does not exist, is not a symbolic link, or it
     * cannot be determined if the file is a symbolic link or not.
     * @throws IOException
     */
    boolean isLink(Path file) throws IOException;
}
