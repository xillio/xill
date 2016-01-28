package nl.xillio.xill.api.construct;

import com.google.inject.Inject;
import nl.xillio.plugins.XillPlugin;
import nl.xillio.xill.api.components.ExpressionBuilderHelper;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.data.MetadataExpression;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.services.files.FileResolver;
import org.apache.commons.lang3.text.WordUtils;

import java.io.File;
import java.net.URL;

/**
 * This interface contains the core functionality for all constructs
 */
public abstract class Construct extends ExpressionBuilderHelper implements HelpComponent {
    private final String defaultName;

    @Inject
    private static FileResolver fileResolver;

    /**
     * Instantiate a new {@link Construct} and pick a default name
     */
    public Construct() {
        // Set the default name
        String name = getClass().getSimpleName();
        String superName = Construct.class.getSimpleName();
        if (name.endsWith(superName)) {
            name = name.substring(0, name.length() - superName.length());
        }
        defaultName = WordUtils.uncapitalize(name);
    }

    /**
     * <p>
     * Returns the name of the construct. This name is also the command by which this construct can be called inside scripts.
     * </p>
     * <p>
     * By default the name of a {@link XillPlugin} is the concrete implementation name acquired using {@link Class#getSimpleName()} without the {@link XillPlugin} suffix. It is also uncapitalized using
     * {@link WordUtils#uncapitalize(String)}
     * </p>
     *
     * @return the name of the construct. This name is also the command by which
     * this construct can be called inside scripts
     */
    public String getName() {
        return defaultName;
    }

    /**
     * Build a new process and return it ready for input
     *
     * @param context The context for which to prepare the {@link Construct}
     * @return A prepared processor loaded with the {@link Construct} behaviour.
     */
    public abstract ConstructProcessor prepareProcess(final ConstructContext context);

    /**
     * Check if a {@link MetaExpression} is <b>NOT</b> null. This is checked by
     * calling {@link MetaExpression#isNull()}.
     *
     * @param expression   The expression to check
     * @param argumentName The name of the expression. This is used to generate understandable debug messages.
     * @throws RobotRuntimeException when the assertion fails
     */
    protected static void assertNotNull(final MetaExpression expression, final String argumentName) {
        if (expression.isNull()) {
            throw new RobotRuntimeException(argumentName + " cannot be null");
        }
    }

    /**
     * Check if a {@link MetaExpression} is null. This is checked by calling {@link MetaExpression#isNull()}.
     *
     * @param expression   The expression to check
     * @param argumentName The name of the expression. This is used to generate understandable debug messages.
     * @throws RobotRuntimeException when the assertion fails
     */
    protected static void assertIsNull(final MetaExpression expression, final String argumentName) {
        if (!expression.isNull()) {
            throw new RobotRuntimeException(argumentName + " must be null");
        }
    }

    /**
     * Check if the {@link MetaExpression} contains an instance of meta
     * information and fetch it
     *
     * @param <T>              The type of meta information to assert
     * @param expression       The expression to check
     * @param expressionName   The name of the expression. This would generally be a
     *                         parameter name in the construct. It is used to give the
     *                         developer an understandable message.
     * @param type             The {@link Class} of the meta object to fetch
     * @param friendlyTypeName The friendly name of the type. This will be used to generate
     *                         an understandable message
     * @return the requested meta object
     * @throws RobotRuntimeException when the assertion fails
     */
    protected static <T extends MetadataExpression> T assertMeta(final MetaExpression expression, final String expressionName, final Class<T> type, final String friendlyTypeName) {
        T value = expression.getMeta(type);
        if (value == null) {
            throw new RobotRuntimeException("Expected " + expressionName + " to be a " + friendlyTypeName);
        }

        return value;
    }

    @Override
    public URL getDocumentationResource() {
        String url = "/" + getClass().getName().replace('.', '/') + ".xml";
        return getClass().getResource(url);
    }

    /**
     * Uses the FileResolver service to get a File object from a path
     *
     * @param context the construct context
     * @param path    the path
     * @return the file
     */
    protected static File getFile(ConstructContext context, String path) {
        return fileResolver.buildFile(context, path);
    }

    /**
     * Hide this construct from documentation
     *
     * @return true if it should be hidden
     */
    public boolean hideDocumentation() {
        return false;
    }
}
