package nl.xillio.xill.plugins.file.services.fileUtils;

import java.io.File;
import java.io.IOException;

import com.google.inject.ImplementedBy;

import nl.xillio.xill.services.XillService;

/**
 * This {@link XillService} is responsible for various file operations
 *
 */
@ImplementedBy(FileUtilitiesImpl.class)
public interface FileUtilities extends XillService {

	/**
	 * Copy a source file to a target destination
	 *
	 * @param source
	 *        the source file
	 * @param target
	 *        the target file
	 * @throws IOException
	 *         when the operation failed
	 */
	public void copy(File source, File target) throws IOException;

	/**
	 * Create a folder at the specific location if it does not exist
	 * 
	 * @param folder
	 *        the folder
	 * @throws IOException
	 *         when the operation failed
	 */
	public void createFolder(File folder) throws IOException;

	/**
	 * Returns true if the file exists
	 * 
	 * @param file
	 *        the file to check
	 * @return true if and only if the file exists
	 */
	public boolean exists(File file);

	/**
	 * Check the size of a file
	 * 
	 * @param file
	 *        the file to check
	 * @return the size in bytes
	 */
	public long getByteSize(File file);

	/**
	 * Delete a file or folder
	 * 
	 * @param file
	 *        the file
	 * @throws IOException
	 *         when the operation failed
	 */
	public void delete(File file) throws IOException;
	
	/**
	 * Create the required folders and save content to a file
	 * @param content the content to save
	 * @param file the target file
	 * @throws IOException when the operation failed
	 */
	public void saveStringToFile(String content, File file) throws IOException;
	
	/**
	 * Create the required folders and append content to a file
	 * @param content the content to append
	 * @param file the target file
	 * @throws IOException when the operation failed
	 */
	public void appendStringToFile(String content, File file) throws IOException;
}
