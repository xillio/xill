package nl.xillio.xill.plugins.web.services.web;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.google.inject.Singleton;

/**
 * The implementation of the FileService interface.
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

}
