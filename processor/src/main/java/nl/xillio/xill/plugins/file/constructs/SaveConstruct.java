package nl.xillio.xill.plugins.file.constructs;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.plugins.file.services.files.FileUtilities;

import java.io.File;
import java.io.IOException;

/**
 * Override a potentially existing file or create a new file with the provided contents. Returns the absolute path of
 * the affected file if the file was created, otherwise false
 */
@Singleton
public class SaveConstruct extends Construct {

    @Inject
    private FileUtilities fileUtils;

    @Override
    public ConstructProcessor prepareProcess(final ConstructContext context) {
        return new ConstructProcessor(
                (uri, content) -> process(context, fileUtils, uri, content),
                new Argument("uri", ATOMIC),
                new Argument("content", NULL, ATOMIC));
    }

    static MetaExpression process(final ConstructContext context, final FileUtilities fileUtils, final MetaExpression uri, final MetaExpression content) {
        File file = getFile(context, uri.getStringValue());
        try {
            fileUtils.saveStringToFile(content.getStringValue(), file);
        } catch (IOException e) {
            context.getRootLogger().error("Failed to write to file: " + e.getMessage(), e);
            return FALSE;
        }
        return fromValue(file.getAbsolutePath());
    }
}
