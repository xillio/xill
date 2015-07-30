package nl.xillio.xill.plugins.file.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;
import java.util.function.Predicate;

/**
 * This class represents the base for the iterators used by the File plugin.
 *
 * @see FileIterator
 * @see FolderIterator
 */
public abstract class FileSystemIterator {
    private static final Logger logger = LogManager.getLogger();
    private final Stack<DirectoryStreamWithIterator> stack = new Stack<>();
    private Path nextValue;
    private final boolean recursive;
    private final Predicate<Path> resultChecker;
    private final Set<Path> noAccessPaths = new HashSet<>();

    /**
     * Create a new FileIterator and add the rootFolder to the stream
     *
     * @param rootFolder the root folder
     * @param recursive  weather the stream should also list files in sub folders
     * @throws IOException if the rootFolder does not exist
     */
    public FileSystemIterator(File rootFolder, boolean recursive, Predicate<Path> resultChecker) throws IOException {
        this.recursive = recursive;
        this.resultChecker = resultChecker;
        addFolder(rootFolder.toPath());
    }

    private void addFolder(Path folder) throws IOException {
        try {
            stack.push(new DirectoryStreamWithIterator(Files.newDirectoryStream(folder)));
        } catch (AccessDeniedException e) {
            logger.error("No access to " + folder.toAbsolutePath());
            noAccessPaths.add(folder);
        }
    }

    public boolean hasNext() {
        selectNext();

        return nextValue != null;
    }

    private void selectNext() {
        //We should try to select the next value
        while (!stack.isEmpty() && nextValue == null) {
            if (stack.peek().iterator().hasNext()) {
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

    protected Path getNextValue() {
        if (!hasNext()) {
            throw new IllegalStateException("No next file present");
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

    /**
     * Check if the path was accessible
     *
     * @param path the path to check
     * @return true if and only if the path was denied access to while iterating
     */
    protected boolean isAccessible(Path path) {
        return !noAccessPaths.contains(path);
    }
}
