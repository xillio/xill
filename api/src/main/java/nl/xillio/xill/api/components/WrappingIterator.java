package nl.xillio.xill.api.components;

import java.util.Iterator;

/**
 * This class represents a base implementation of an iterator that contains a child expression. This child should be
 * kept available as long as this - its parent - is still accessible.
 *
 * @author Thomas Biesaart
 * @author Andrea Parrilli
 */
public abstract class WrappingIterator extends MetaExpressionIterator<MetaExpression> {

    private final MetaExpression host;

    public WrappingIterator(MetaExpression host) {
        this(host, host.getMeta(MetaExpressionIterator.class));
    }

    public WrappingIterator(MetaExpression host, Iterator<MetaExpression> source) {
        super(source);
        this.host = host;
        host.registerReference();
    }

    @Override
    protected final MetaExpression transform(MetaExpression item) {
        return transformItem(item);
    }

    protected abstract MetaExpression transformItem(MetaExpression item);

    @Override
    public void close() throws Exception {
        host.releaseReference();
    }
}
