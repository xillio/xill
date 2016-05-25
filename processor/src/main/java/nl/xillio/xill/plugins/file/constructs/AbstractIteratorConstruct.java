package nl.xillio.xill.plugins.file.constructs;

import com.google.inject.Inject;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.OperationFailedException;
import nl.xillio.xill.plugins.file.services.FileSystemIterator;

import java.io.IOException;
import java.nio.file.FileSystemException;
import java.nio.file.Path;
import java.util.Iterator;

/**
 * This class provides a base for constructs that provide an iterator.
 *
 * @author Thomas Biesaart
 * @see IterateFoldersConstruct
 * @see IterateFilesConstruct
 */
abstract class AbstractIteratorConstruct extends Construct {

    private FileSystemIterator fileIterator;

    @Inject
    void setFileIterator(FileSystemIterator iterator) {
        this.fileIterator = iterator;
    }

    @Override
    public ConstructProcessor prepareProcess(final ConstructContext context) {
        return new ConstructProcessor(
                (uri, recursive) -> process(context, uri, recursive),
                new Argument("uri", ATOMIC),
                new Argument("recursive", FALSE, ATOMIC));
    }


    private MetaExpression process(ConstructContext context, MetaExpression uri, MetaExpression recursive) {
        Path file = getPath(context, uri);
        boolean isRecursive = recursive.getBooleanValue();

        return tryBuildIterator(file, isRecursive);
    }

    private MetaExpression tryBuildIterator(Path file, boolean isRecursive) {
        try {
            return buildIterator(file, isRecursive);
        } catch (FileSystemException e) {
            throw new OperationFailedException("read " + e.getFile(), e.getMessage(), e);
        } catch (IOException e) {
            throw new OperationFailedException("iterate " + file, e.getMessage(), e);
        }
    }


    protected abstract MetaExpression buildIterator(Path file, boolean isRecursive) throws IOException;


    protected Iterator<Path> iterateFiles(Path folder, boolean recursive) throws IOException {
        return fileIterator.iterateFiles(folder, recursive);
    }

    protected Iterator<Path> iterateFolders(Path folder, boolean recursive) throws IOException {
        return fileIterator.iterateFolders(folder, recursive);
    }
}
