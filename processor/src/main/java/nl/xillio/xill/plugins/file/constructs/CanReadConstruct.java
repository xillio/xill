package nl.xillio.xill.plugins.file.constructs;

import com.google.inject.Inject;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.file.services.files.FileUtilities;

import java.io.File;
import java.io.IOException;

/**
 * Determines whether a file or folder is readable or not.
 *
 * Created by Anwar on 11/30/2015.
 */
public class CanReadConstruct extends Construct {

    @Inject
    private FileUtilities fileUtilities;

    @Override
    public ConstructProcessor prepareProcess(ConstructContext context) {
        return new ConstructProcessor(
                uri -> process(context, fileUtilities, uri),
                new Argument("uri", ATOMIC)
        );
    }

    static MetaExpression process(final ConstructContext constructContext, final FileUtilities fileUtilities,
                                  final MetaExpression uri) {
        try {
            File file = getFile(constructContext, uri.getStringValue());
            return fromValue(fileUtilities.canRead(file));
        } catch (IOException e) {
            throw new RobotRuntimeException("File not found, or not accessible", e);
        }
    }

}