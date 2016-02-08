package nl.xillio.xill.plugins.file.constructs;

import com.google.inject.Singleton;
import nl.xillio.xill.api.components.MetaExpression;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Singleton
public class GetMimeTypeConstruct extends AbstractFilePropertyConstruct<String> {


    @Override
    protected String process(Path path) throws IOException {
        return Files.probeContentType(path);
    }

    @Override
    protected MetaExpression parse(String input) {
        return fromValue(input);
    }
}
