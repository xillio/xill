package nl.xillio.xill.plugins.file.constructs;

import com.google.inject.Singleton;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.MetaExpressionIterator;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;

/**
 * Creates an iterator that iterates over all files in a specific folder. If recursive is set to true the iterator will
 * also cover all files in the subdirectories
 */
@Singleton
public class IterateFilesConstruct extends AbstractIteratorConstruct {

    @Override
    protected MetaExpression buildIterator(Path file, boolean isRecursive) throws IOException {
        MetaExpression result = fromValue("List files " + (isRecursive ? "recursively " : "") + "in " + file);
        Iterator<Path> iterator = iterateFiles(file, isRecursive);
        result.storeMeta(new MetaExpressionIterator<>(iterator, entry -> fromValue(entry.toString())));
        return result;
    }
}
