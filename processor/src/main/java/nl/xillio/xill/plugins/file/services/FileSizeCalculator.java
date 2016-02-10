package nl.xillio.xill.plugins.file.services;

import com.google.inject.ImplementedBy;
import nl.xillio.xill.plugins.file.services.files.FileUtilitiesImpl;

import java.io.IOException;
import java.nio.file.Path;

/**
 * This interface represents an object that can calculate the size of a file or folder.
 *
 * @author Thomas Biesaart
 */
@ImplementedBy(FileUtilitiesImpl.class)
public interface FileSizeCalculator {
    /**
     * This method will get the size of a file or folder.
     * In the case of a folder it will iterate through all the files in the folder and return the size.
     *
     * @param path the path
     * @return the size
     * @throws IOException if calculating the file size fails
     */
    long getSize(Path path) throws IOException;
}
