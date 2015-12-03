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
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.util.LinkedHashMap;

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
                (uri) -> process(context, fileUtilities, uri),
                new Argument("uri", ATOMIC)
        );
    }

    static MetaExpression process(final ConstructContext constructContext, final FileUtilities fileUtilities,
                                  final MetaExpression uri) {
        File file = getFile(constructContext, uri.getStringValue());

        try {
            fileUtilities.canRead(file);
            return createMetaExpression(file);
        } catch (FileNotFoundException e) {
            throw new RobotRuntimeException(e.getMessage(), e);
        }
    }

    /**
     * Create the expression.
     *
     * @param file The file object
     * @return Specified metaexpression
     */
    private static MetaExpression createMetaExpression(File file) {
        LinkedHashMap<String, MetaExpression> result = new LinkedHashMap<>();
        result.put("file/folder: ", fromValue(file.getAbsolutePath()));
        result.put("can read: ", fromValue(Files.isReadable(file.toPath())));
        return fromValue(result);
    }
}