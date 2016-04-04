package nl.xillio.xill.api.components;


import nl.xillio.xill.api.data.MetadataExpression;

import java.util.Iterator;
import java.util.function.Function;

/**
 * This Iterator represents a type of metadata that can be stored in an ATOMIC MetaExpression. Whenever a foreach
 * construction loops over an ATOMIC value holding this element, it will iterate over all the values in this
 * MetaExpressionIterator instead.
 */
public class MetaExpressionIterator<E> implements Iterator<MetaExpression>, MetadataExpression, AutoCloseable {
    private final Iterator<E> iterator;
    private final Function<E, MetaExpression> transformer;

    /**
     * Creates a new metaExpression iterator from a source and no transformation.
     * Note that this will require implementation of the transform method since it will
     * cause a {@link NullPointerException}.
     *
     * @param source the source Iterator
     */
    protected MetaExpressionIterator(Iterator<E> source) {
        this(source, null);
    }

    /**
     * Creates a new metaExpression iterator from a source and a transformation.
     *
     * @param source      the source Iterator
     * @param transformer the function used to transform the elements of the source to usable MetaExpression
     */
    public MetaExpressionIterator(Iterator<E> source, Function<E, MetaExpression> transformer) {
        this.iterator = source;
        this.transformer = transformer;
    }

    protected MetaExpression transform(E item) {
        return transformer.apply(item);
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public MetaExpression next() {
        return transform(iterator.next());
    }

    @Override
    public void close() throws Exception {
        if (iterator instanceof AutoCloseable) {
            ((AutoCloseable) iterator).close();
        }
    }
}
