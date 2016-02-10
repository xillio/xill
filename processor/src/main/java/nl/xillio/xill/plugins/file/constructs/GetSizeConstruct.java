package nl.xillio.xill.plugins.file.constructs;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.plugins.file.services.FileSizeCalculator;

import java.io.IOException;
import java.nio.file.Path;

/**
 * This construct returns the size of a file or throws an error when the file was not found.
 *
 * @author Thomas biesaart
 */
@Singleton
public class GetSizeConstruct extends AbstractFilePropertyConstruct<Long> {
    private final FileSizeCalculator fileSizeCalculator;

    @Inject
    GetSizeConstruct(FileSizeCalculator fileSizeCalculator) {
        this.fileSizeCalculator = fileSizeCalculator;
    }

    @Override
    protected Long process(Path path) throws IOException {
        return fileSizeCalculator.getSize(path);
    }

    @Override
    protected MetaExpression parse(Long input) {
        return fromValue(input);
    }
}
