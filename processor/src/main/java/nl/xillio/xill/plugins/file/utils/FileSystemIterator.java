package nl.xillio.xill.plugins.file.utils;

import me.biesaart.utils.Log;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Stack;
import java.util.function.Predicate;

/**
 * This class represents the base for the iterators used by the File plugin.
 *
 * @see FileIterator
 * @see FolderIterator
 */
abstract class FileSystemIterator implements Iterator<Path> {
    private static final Logger LOGGER = Log.get();
    private final Stack<DirectoryStreamWithIterator> stack = new Stack<>();
    private Path nextValue;
    private final boolean recursive;
    private final Predicate<Path> resultChecker;

    /**
     * Create a new FileIterator and add the rootFolder to the stream
     *
     * @param rootFolder the root folder
     * @param recursive  weather the stream should also list files in sub folders
     * @throws IOException if the rootFolder does not exist
     */
    FileSystemIterator(Path rootFolder, boolean recursive, Predicate<Path> resultChecker) throws IOException {
        this.recursive = recursive;
        this.resultChecker = resultChecker;
        addFolder(rootFolder);
    }

    private void addFolder(Path folder) throws IOException {
        stack.push(new DirectoryStreamWithIterator(Files.newDirectoryStream(folder)));
    }

    /**
     * Check if there is a next value in this iterator
     *
     * @return true if there is a next value
     */
    @Override
    public boolean hasNext() {
        selectNext();

        return nextValue != null;
    }

    private void selectNext() {
        //We should try to select the next value
        while (!stack.isEmpty() && nextValue == null) {
            tryCacheNext();
        }
    }

    private void tryCacheNext() {
        if (stack.peek().iterator().hasNext()) {
            cacheNext();
        } else {
            try {
                stack.pop().close();
            } catch (IOException e) {
                LOGGER.error("Failed to close the stream.", e);
            }
        }
    }

    private void cacheNext() {
        Path current = stack.peek().iterator().next();

        if (resultChecker.test(current)) {
            //Found the next hit!
            nextValue = current;
        }

        if (Files.isDirectory(current) && recursive) {
            //We should go recursive
            try {
                addFolder(current);
            } catch (IOException e) {
                LOGGER.error("Failed to open " + current.toAbsolutePath(), e);
            }
        }
    }

    @Override
    public Path next() {
        if (!hasNext()) {
            throw new NoSuchElementException("No next file present");
        }

        Path next = nextValue;
        nextValue = null;
        return next;
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
