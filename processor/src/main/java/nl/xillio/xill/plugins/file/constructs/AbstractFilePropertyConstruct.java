package nl.xillio.xill.plugins.file.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * This class represents an abstract implementation of a construct that will check a property of a file, e.g. canWrite, creationDate.
 * NOTE: The input file will be checked for existence. If the file does not exist, this construct will fail
 *
 * @param <T> the type of property to extract
 * @author Thomas biesaart
 */
abstract class AbstractFilePropertyConstruct<T> extends Construct {
    @Override
    public ConstructProcessor prepareProcess(ConstructContext context) {
        return new ConstructProcessor(
                path -> process(context, path),
                new Argument("path", ATOMIC)
        );
    }

    private MetaExpression process(ConstructContext context, MetaExpression path) {
        Path file = getPath(context, path);

        // Read javadoc, this is not the same as Files.notExists
        if (!Files.exists(file)) {
            throw new RobotRuntimeException(file + " does not exist");
        }

        try {
            return parse(process(file));
        } catch (IOException e) {
            throw new RobotRuntimeException(file + " could not be read", e);
        }
    }

    /**
     * Check the concrete property for the passed path.
     * This path has been checked for existence so no need to check that.
     *
     * @param path the path
     * @return true if the passed files holds the concrete property
     * @throws IOException if the path could not be read
     */
    protected abstract T process(Path path) throws IOException;

    /**
     * Parse a result from {@link this#process(Path)} to a {@link MetaExpression}.
     *
     * @param input the input
     * @return the expression
     */
    protected abstract MetaExpression parse(T input);
}
