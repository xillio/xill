package nl.xillio.xill.plugins.file.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Stack;

/**
 * This class iterates over files and provides information about every file
 */
public class FileIterator implements Iterator<File> {
	private static final Logger logger = LogManager.getLogger();
	private final Stack<DirectoryStreamWithIterator> stack = new Stack<>();
	private Path nextValue;
	private final boolean recursive;

	/**
	 * Create a new FileIterator and add the rootFolder to the stream
	 *
	 * @param rootFolder the root folder
	 * @param recursive  weather the stream should also list files in sub folders
	 *
	 * @throws IOException if the rootFolder does not exist
	 */
	public FileIterator(File rootFolder, boolean recursive) throws IOException {
		this.recursive = recursive;
		addFolder(rootFolder.toPath());
	}

	private void addFolder(Path folder) throws IOException {
		stack.push(new DirectoryStreamWithIterator(Files.newDirectoryStream(folder)));
	}

	@Override
	public boolean hasNext() {
		selectNext();

		return nextValue != null;
	}

	private void selectNext() {
		//We should try to select the next value
		while (!stack.isEmpty() && nextValue == null) {
			if (stack.peek().iterator().hasNext()) {
				Path current = stack.peek().iterator().next();

				if (Files.isRegularFile(current)) {
					//Found the next hit!
					nextValue = current;
				} else if (Files.isDirectory(current) && recursive) {
					//We should go recursive
					try {
						addFolder(current);
					} catch (IOException e) {
						logger.error("Failed to open " + current.toAbsolutePath(), e);
					}
				}
			} else {
				try {
					stack.pop().close();
				} catch (IOException e) {
					logger.error("Failed to close the stream.", e);
				}
			}
		}
	}

	@Override
	public File next() {
		if (!hasNext()) {
			throw new IllegalStateException("No next file present");
		}

		Path next = nextValue;
		nextValue = null;
		return next.toFile();
	}

	private class DirectoryStreamWithIterator {
		private final DirectoryStream<Path> stream;
		private Iterator<Path> iterator;

		public DirectoryStreamWithIterator(DirectoryStream<Path> stream) {
			this.stream = stream;
		}

		public Iterator<Path> iterator() {
			if (iterator == null) {
				iterator = stream.iterator();
			}

			return iterator;
		}

		public void close() throws IOException {
			stream.close();
		}
	}
}
