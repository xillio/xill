package nl.xillio.xill.plugins.file.services.files;

import com.google.inject.ImplementedBy;
import nl.xillio.xill.services.XillService;

import java.io.IOException;
import java.nio.file.Path;
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
     * Delete a file or folder
     *
     * @param file the file
     * @throws IOException when the operation failed
     */
    void delete(Path file) throws IOException;

}
