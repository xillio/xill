package nl.xillio.xill.plugins.file.services.files;

import com.google.inject.Singleton;
import nl.xillio.xill.plugins.file.utils.FileIterator;
import nl.xillio.xill.plugins.file.utils.FolderIterator;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Iterator;

/**
 * This is the main implementation of the {@link FileUtilities} service.
 */
@Singleton
public class FileUtilitiesImpl implements FileUtilities {

    @Override
    public void copy(final Path source, final Path target) throws IOException {
        if (!Files.exists(source)) {
            throw new NoSuchFileException("No such file: " + source.toAbsolutePath());
        }


        if (Files.isDirectory(source)) {
            copyDirectory(source, target);
        } else {
            createDir(target.getParent());
            copyFile(source, target);
        }
    }

    private void copyDirectory(Path source, Path target) throws IOException {
        Files.walkFileTree(source, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Path relative = source.relativize(file);
                copyFile(file, target.resolve(relative));
                return super.visitFile(file, attrs);
            }

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                Path relative = source.relativize(dir);
                Path targetDir = target.resolve(relative);
                createDir(targetDir);
                return super.preVisitDirectory(dir, attrs);
            }
        });
    }

    private void copyFile(Path source, Path target) throws IOException {
        Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
    }

    private void createDir(Path path) throws IOException {
        if (path != null) {
            Files.createDirectories(path);
        }
    }

    @Override
    public void createFolder(final Path folder) throws IOException {
        if (Files.isRegularFile(folder)) {
            throw new FileAlreadyExistsException("A file already exists at " + folder.toAbsolutePath());
        }
        Files.createDirectories(folder);
    }

    /**
     * Determine whether the specified file is present
     *
     * @param file the file to check
     * @return <tt>true</tt> if it exists. <tt>false</tt> otherwise.
     */
    @Override
    public boolean exists(final Path file) {
        return Files.exists(file);
    }

    @Override
    public long getByteSize(final Path file) throws IOException {
        try {
            return Files.size(file);
        } catch (IllegalArgumentException e) {
            throw new IOException(e.getMessage(), e);
        }
    }

    /**
     * Force remove the specified file.
     * If the file does not exist, it will log a failure message.
     *
     * @param file the file
     * @throws IOException If an I/O operation has failed.
     */
    @Override
    public void delete(final Path file) throws IOException {
        if (!Files.exists(file)) {
            return;
        }

        Files.walkFileTree(file, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return super.visitFile(file, attrs);
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return super.postVisitDirectory(dir, exc);
            }
        });
    }

    @Override
    public Iterator<Path> iterateFiles(Path folder, boolean recursive) throws IOException {
        return new FileIterator(folder, recursive);
    }

    @Override
    public Iterator<Path> iterateFolders(Path folder, boolean recursive) throws IOException {
        return new FolderIterator(folder, recursive);
    }

    @Override
    public FileTime getCreationDate(Path file) throws IOException {
        return stat(file).creationTime();
    }

    @Override
    public FileTime getLastModifiedDate(Path file) throws IOException {
        return stat(file).lastModifiedTime();
    }

    @Override
    public boolean canRead(Path file) throws IOException {
        return fileCheck(file, Files.isReadable(file));
    }

    @Override
    public boolean canWrite(Path file) throws IOException {
        return fileCheck(file, Files.isWritable(file));
    }

    @Override
    public boolean canExecute(Path file) throws IOException {

        return fileCheck(file, Files.isExecutable(file));
    }

    @Override
    public boolean isHidden(Path file) throws IOException {
        return fileCheck(file, Files.isHidden(file));
    }

    @Override
    public boolean isFile(Path file) throws IOException {
        return fileCheck(file, Files.isRegularFile(file));
    }

    @Override
    public boolean isFolder(Path file) throws IOException {
        return fileCheck(file, Files.isDirectory(file));
    }

    @Override
    public boolean isLink(Path file) throws IOException {
        return fileCheck(file, Files.isSymbolicLink(file));
    }

    private boolean fileCheck(Path file, boolean statement) throws FileNotFoundException {
        if (!Files.notExists(file)) {
            return statement;
        } else {
            throw new FileNotFoundException("The specified file folder does not exist.");
        }
    }

    private static BasicFileAttributes stat(Path file) throws IOException {
        return Files.readAttributes(file, BasicFileAttributes.class);
    }

}
