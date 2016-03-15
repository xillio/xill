package nl.xillio.xill.api.components;

import nl.xillio.xill.api.errors.NotImplementedException;

import java.util.*;

import static nl.xillio.xill.api.components.ExpressionBuilderHelper.fromValue;

/**
 * This class represents a base implementation of an iterator that contains a child expression. This child should be
 * kept available as long as this - its parent - is still accessible.
 *
 * @author Thomas Biesaart
 * @author Andrea Parrilli
 */
public abstract class WrappingIterator extends MetaExpressionIterator<MetaExpression> {

    private final MetaExpression host;

    private static Iterator<MetaExpression> iterate(MetaExpression expression) {
        if (expression.isNull()) {
            return Collections.emptyIterator();
        }

        switch (expression.getType()) {
            case ATOMIC:
                if (expression.hasMeta(MetaExpressionIterator.class)) {
                    return expression.getMeta(MetaExpressionIterator.class);
                }
                return Collections.singletonList(expression).iterator();
            case LIST:
                return expression.<List<MetaExpression>>getValue().iterator();
            case OBJECT:
                return expression.<Map<String, MetaExpression>>getValue()
                        .entrySet()
                        .stream()
                        .map(WrappingIterator::mapEntry)
                        .iterator();
            default:
                throw new NotImplementedException("An unknown type " + expression.getType() + " was passed");
        }

    }

    /**
     * Create a simple implementation of the WrappingIterator that will not touch the elements themselves.
     *
     * @param input the iterable input
     * @return the iterator
     */
    public static WrappingIterator identity(MetaExpression input) {
        return new WrappingIterator(input) {
            @Override
            protected MetaExpression transformItem(MetaExpression item) {
                return item;
            }
        };
    }

    private static MetaExpression mapEntry(Map.Entry<String, MetaExpression> entry) {
        LinkedHashMap<String, MetaExpression> result = new LinkedHashMap<>();
        result.put(entry.getKey(), entry.getValue());
        return fromValue(result);
    }

    public WrappingIterator(MetaExpression host) {
        this(host, iterate(host));
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
    public void close() {
        host.releaseReference();
    }
}
