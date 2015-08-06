package nl.xillio.xill.plugins.web.services.web;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.google.inject.Singleton;

/**
 * The implementation of the {@link FileService} interface.
 */
@Singleton
public class FileServiceImpl implements FileService {

	@Override
	public void copyFile(File sourceFile, File destinationFile) throws IOException {
		FileUtils.copyFile(sourceFile, destinationFile);
	}

	@Override
	public File makeFile(String fileName) {
		return new File(fileName);
	}

	@Override
	public String getAbsolutePath(File file) {
		return file.getAbsolutePath();
	}

	@Override
	public File createTempFile(String prefix, String suffix) throws IOException {
		File file = File.createTempFile(prefix, suffix);
		file.deleteOnExit();
		return file;
	}

	@Override
	public void writeStringToFile(File file, String text) throws IOException {
		FileUtils.writeStringToFile(file, text);	
	}

}
