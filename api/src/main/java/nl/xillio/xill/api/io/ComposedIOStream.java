package nl.xillio.xill.api.io;

import nl.xillio.xill.api.components.MetaExpression;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * This implementation of the {@link IOStream} extends the {@link SimpleIOStream} but also registers a
 * reference to a {@link nl.xillio.xill.api.components.MetaExpression} and closes it when this stream is closed.
 *
 * @author Thomas Biesaart
 */
public class ComposedIOStream extends SimpleIOStream {
    private final MetaExpression composedExpression;

    public ComposedIOStream(InputStream stream, String description, MetaExpression composedExpression) {
        this(stream, null, description, composedExpression);
    }

    public ComposedIOStream(OutputStream stream, String description, MetaExpression composedExpression) {
        this(null, stream, description, composedExpression);
    }

    public ComposedIOStream(InputStream inputStream, OutputStream outputStream, String description, MetaExpression composedExpression) {
        super(inputStream, outputStream, description);
        this.composedExpression = composedExpression;
        composedExpression.registerReference();
    }

    @Override
    public void close() {
        super.close();
        composedExpression.releaseReference();
    }
}
