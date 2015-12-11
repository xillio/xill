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
 * Determines whether a file or folder is executable or not.
 *
 * Created by Anwar on 11/30/2015.
 */
public class CanExecuteConstruct extends Construct {

    @Inject
    private FileUtilities fileUtilities;

    @Override
    public ConstructProcessor prepareProcess(final ConstructContext context) {
        return new ConstructProcessor(
                (uri) -> process(context, fileUtilities, uri),
                new Argument("uri", ATOMIC)
        );
    }

    static MetaExpression process(final ConstructContext constructContext, final FileUtilities fileUtilities,
                                  final MetaExpression uri) {
        File file = getFile(constructContext, uri.getStringValue());

        try {
            fileUtilities.canExecute(file);
            return createMetaExpression(file);
        } catch (FileNotFoundException e) {
            throw new RobotRuntimeException(e.getMessage(), e);
        }
    }

    /**
     * Create the expression.
     *
     * @param file The file object
     * @return Specified meta-expression
     */
    private static MetaExpression createMetaExpression(File file) {
        LinkedHashMap<String, MetaExpression> result = new LinkedHashMap<>();
        result.put("file/folder: ", fromValue(file.getAbsolutePath()));
        result.put("can execute: ", fromValue(Files.isExecutable(file.toPath())));
        return fromValue(result);
    }
}