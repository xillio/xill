package nl.xillio.xill.plugins.web.services.web;

import java.io.File;
import java.io.IOException;

import com.google.inject.ImplementedBy;

/**
 * Provides an interface for the {@link FileService} interface.
 */
@ImplementedBy(FileServiceImpl.class)
public interface FileService {
	
	/**
	 * Copies a source File to a destination File.
	 * @param sourceFile
	 * 						The source File.
	 * @param destinationFile
	 * 						The destination File.
	 */
	public void copyFile(File sourceFile, File destinationFile) throws IOException;
	
	/**
	 * Makes a file with a given name.
	 * @param fileName
	 * 						The name of the File.
	 * @return
	 * 				A new File.
	 */
	public File makeFile(String fileName);

}
