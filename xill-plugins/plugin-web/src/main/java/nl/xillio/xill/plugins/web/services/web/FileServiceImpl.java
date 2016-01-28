package nl.xillio.xill.plugins.web.services.web;

import com.google.inject.Singleton;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * The implementation of the {@link FileService} interface.
 */
@Singleton
public class FileServiceImpl implements FileService {

    @Override
    public void copyFile(final File sourceFile, final File destinationFile) throws IOException {
        FileUtils.copyFile(sourceFile, destinationFile);
    }

    @Override
    public String getAbsolutePath(final File file) {
        return file.getAbsolutePath();
    }

    @Override
    public File createTempFile(final String prefix, final String suffix) throws IOException {
        File file = File.createTempFile(prefix, suffix);
        file.deleteOnExit();
        return file;
    }

    @Override
    public void writeStringToFile(final File file, final String text) throws IOException {
        FileUtils.writeStringToFile(file, text);
    }

}
