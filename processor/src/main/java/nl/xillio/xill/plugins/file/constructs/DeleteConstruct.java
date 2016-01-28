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
 * This class deletes a file or folder. Returns the absolute path to the folder
 */
@Singleton
public class DeleteConstruct extends Construct {

    @Inject
    private FileUtilities fileUtils;

    @Override
    public ConstructProcessor prepareProcess(final ConstructContext context) {
        return new ConstructProcessor(
                uri -> process(context, fileUtils, uri),
                new Argument("uri", ATOMIC));
    }

    static MetaExpression process(final ConstructContext context, final FileUtilities fileUtils, final MetaExpression uri) {

        File file = getFile(context, uri.getStringValue());
        try {
            fileUtils.delete(file);
        } catch (IOException e) {
            context.getRootLogger().error("Failed to delete " + file.getAbsolutePath(), e);
        }

        return fromValue(file.getAbsolutePath());
    }
}
