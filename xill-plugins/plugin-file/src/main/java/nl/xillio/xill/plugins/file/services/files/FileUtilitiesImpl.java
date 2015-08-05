package nl.xillio.xill.plugins.file.services.files;

import com.google.inject.Singleton;
import nl.xillio.xill.plugins.file.utils.FileIterator;
import nl.xillio.xill.plugins.file.utils.Folder;
import nl.xillio.xill.plugins.file.utils.FolderIterator;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

/**
 * This is the main implementation of the {@link FileUtilities} service
 */
@Singleton
public class FileUtilitiesImpl implements FileUtilities {
	private static final Logger log = LogManager.getLogger();

	@Override
	public void copy(final File source, final File target) throws IOException {
		if (source.isDirectory()) {
			FileUtils.copyDirectory(source, target);
		} else {
			FileUtils.copyFile(source, target);
		}
	}

	@Override
	public boolean createFolder(final File folder) throws IOException {
		if (folder.isFile()) {
			throw new IOException(folder.getAbsolutePath() + " is not a folder.");
		}

		boolean madeFolders = folder.mkdirs();
		if (!folder.exists()) {
			throw new IOException("Could not create folder " + folder.getAbsolutePath());
		}

		return madeFolders;
	}

	@Override
	public boolean exists(final File file) {
		return file.exists();
	}

	@Override
	public long getByteSize(final File file) throws IOException {
		try {
			return FileUtils.sizeOf(file);
		} catch (IllegalArgumentException e) {
			throw new IOException(e.getMessage(), e);
		}
	}

	@Override
	public void delete(final File file) throws IOException {
		try {
			FileUtils.forceDelete(file);
		} catch (FileNotFoundException e) {
			log.info("Failed to delete " + file.getAbsolutePath(), e);
		}
	}

	@Override
	public void saveStringToFile(final String content, final File file) throws IOException {
		FileUtils.writeStringToFile(file, content, false);
	}

	@Override
	public void appendStringToFile(final String content, final File file) throws IOException {
		FileUtils.writeStringToFile(file, content, true);
	}

	@Override
	public Iterator<File> iterateFiles(File folder, boolean recursive) throws IOException {
		return new FileIterator(folder, recursive);
	}

	@Override
	public Iterator<Folder> iterateFolders(File folder, boolean recursive) throws IOException {
		return new FolderIterator(folder, recursive);
	}

}
