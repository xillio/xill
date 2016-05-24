package nl.xillio.xill.plugins.file.constructs;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.services.resourceLibraries.ContentTypeLibrary;

import java.io.IOException;
import java.nio.file.Path;

@Singleton
public class GetMimeTypeConstruct extends AbstractFilePropertyConstruct<String> {

    @Inject
    ContentTypeLibrary contentTypeLibrary;

    @Override
    protected String process(Path path) throws IOException {
        return contentTypeLibrary.get(path).orElse(null);
    }

    @Override
    protected MetaExpression parse(String input) {
        return fromValue(input);
    }
}
