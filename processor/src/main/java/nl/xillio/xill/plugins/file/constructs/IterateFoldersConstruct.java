package nl.xillio.xill.plugins.file.constructs;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.MetaExpressionIterator;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.file.services.FileSystemIterator;

import java.io.IOException;
import java.nio.file.*;
import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 * Creates an iterator that iterates over all files in a specific folder. If recursive is set to true the iterator will
 * also cover all files in the subdirectories
 */
@Singleton
public class IterateFoldersConstruct extends Construct {

    @Inject
    private FileSystemIterator fileUtils;

    @Override
    public ConstructProcessor prepareProcess(final ConstructContext context) {
        return new ConstructProcessor(
                (uri, content) -> process(context, fileUtils, uri, content),
                new Argument("uri", ATOMIC),
                new Argument("recursive", FALSE, ATOMIC));
    }

    static MetaExpression process(final ConstructContext context, final FileSystemIterator fileUtils,
                                  final MetaExpression uri, final MetaExpression recursive) {
        Path file = getPath(context, uri);
        boolean isRecursive = recursive.getBooleanValue();
        try {
            MetaExpression result = fromValue("List folders " + (isRecursive ? "recursively " : "") + "in " + file);
            Iterator<Path> iterator = fileUtils.iterateFolders(file, isRecursive);
            result.storeMeta(new MetaExpressionIterator<>(iterator, IterateFoldersConstruct::createExpression));

            return result;

        } catch (NoSuchFileException e) {
            throw new RobotRuntimeException("The specified folder does not exist:  " + e.getMessage(), e);
        } catch (AccessDeniedException e) {
            throw new RobotRuntimeException("Access to the specified folder is denied:  " + e.getMessage(), e);
        } catch (NotDirectoryException e) {
            throw new RobotRuntimeException("The specified folder is not a directory:  " + e.getMessage(), e);
        } catch (IOException e) {
            throw new RobotRuntimeException("An error occurred: " + e.getMessage(), e);
        }
    }

    /**
     * Build a MetaExpression from a Folder
     *
     * @param folder the folder
     * @return the MetaExpression
     */
    private static MetaExpression createExpression(Path folder) {
        LinkedHashMap<String, MetaExpression> value = new LinkedHashMap<>();

        boolean read = Files.isReadable(folder);
        boolean write = Files.isWritable(folder);

        value.put("path", fromValue(folder.toString()));
        value.put("canRead", fromValue(read));
        value.put("canWrite", fromValue(write));
        value.put("isAccessible", fromValue(read && write && Files.isExecutable(folder)));

        if (folder.getParent() != null) {
            value.put("parent", fromValue(folder.getParent().toString()));
        }

        return fromValue(value);
    }
}
