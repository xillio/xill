package nl.xillio.xill.plugins.file.constructs;

import com.google.inject.Singleton;
import nl.xillio.xill.api.components.MetaExpression;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * This construct returns the size of a file or throws an error when the file was not found
 */
@Singleton
public class GetSizeConstruct extends AbstractFilePropertyConstruct<Long> {

    @Override
    protected Long process(Path path) throws IOException {
        return Files.size(path);
    }

    @Override
    protected MetaExpression parse(Long input) {
        return fromValue(input);
    }
}
