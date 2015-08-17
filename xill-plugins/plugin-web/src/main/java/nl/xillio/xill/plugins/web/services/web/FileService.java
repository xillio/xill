package nl.xillio.xill.plugins.web.services.web;

import java.io.File;
import java.io.IOException;

import com.google.inject.ImplementedBy;

/**
 * Provides an interface for file services.
 * @author Ivor van der Hoog
 * @author Thomas Biesaart
 */
@ImplementedBy(FileServiceImpl.class)
public interface FileService {

	/**
	 * Copies a source File to a destination File.
	 *
	 * @param sourceFile
	 *        The source File.
	 * @param destinationFile
	 *        The destination File.
	 * @throws IOException when copying the file failed
	 */
	void copyFile(File sourceFile, File destinationFile) throws IOException;

	/**
	 * Makes a file with a given name.
	 *
	 * @param fileName
	 *        The name of the File.
	 * @return
	 *         A new File.
	 */
	File makeFile(String fileName);

	/**
	 * Returns the absolute pathname as a String.
	 *
	 * @param file
	 *        The file we want the absolute path from.
	 * @return
	 *         The absolute path of the file.
	 */
	String getAbsolutePath(File file);

	/**
	 * Creates a temporary {@link File} with a given prefix and suffix.
	 *
	 * @param prefix
	 *        The prefix.
	 * @param suffix
	 *        The suffix.
	 * @return
	 *         Returns a temporary file.
	 * @throws IOException when an error occurs
	 */
	File createTempFile(String prefix, String suffix) throws IOException;

	/**
	 * Writes a string to a {@link File}.
	 *
	 * @param file
	 *        The file we want to write to.
	 * @param text
	 *        The text we're writing.
	 * @throws IOException then a writing error occurs
	 */
	void writeStringToFile(File file, String text) throws IOException;

}
