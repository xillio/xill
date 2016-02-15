package nl.xillio.xill.plugins.file.constructs;

import com.google.inject.Singleton;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.MetaExpressionIterator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 * Creates an iterator that iterates over all files in a specific folder. If recursive is set to true the iterator will
 * also cover all files in the subdirectories.
 *
 * @author Thomas biesaart
 */
@Singleton
public class IterateFoldersConstruct extends AbstractIteratorConstruct {

    @Override
    protected MetaExpression buildIterator(Path file, boolean isRecursive) throws IOException {
        MetaExpression result = fromValue("List folders " + (isRecursive ? "recursively " : "") + "in " + file);
        Iterator<Path> iterator = iterateFolders(file, isRecursive);
        result.storeMeta(new MetaExpressionIterator<>(iterator, IterateFoldersConstruct::createExpression));
        return result;
    }

    /**
     * Build a MetaExpression from a Folder
     *
     * @param folder the folder
     * @return the MetaExpression
     */
    @SuppressWarnings("squid:UnusedPrivateMethod") // Sonar doesn't do lambdas
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
