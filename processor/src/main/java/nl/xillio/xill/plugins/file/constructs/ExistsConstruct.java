package nl.xillio.xill.plugins.file.constructs;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.plugins.file.services.files.FileUtilities;

import java.nio.file.Path;

/**
 * This Construct checks if a file or folder exists and only returns true if it does, otherwise false
 */
@Singleton
public class ExistsConstruct extends Construct {

    @Inject
    private FileUtilities fileUtils;

    @Override
    public ConstructProcessor prepareProcess(final ConstructContext context) {
        return new ConstructProcessor(
                uri -> process(context, fileUtils, uri),
                new Argument("uri", ATOMIC));
    }

    static MetaExpression process(final ConstructContext context, final FileUtilities fileUtils, final MetaExpression uri) {
        Path file = getPath(context, uri);
        return fromValue(fileUtils.exists(file));
    }
}
