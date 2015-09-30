package nl.xillio.xill.api.components;

import nl.xillio.util.xill.api.data.MetadataExpression;

import java.util.Iterator;
import java.util.function.Function;

/**
 * This Iterator represents a type of metadata that can be stored in an ATOMIC MetaExpression. Whenever a foreach
 * construction loops over an ATOMIC value holding this element it will iterate over all the values in this
 * MetaExpressionIterator instead
 */
public class MetaExpressionIterator<E> implements Iterator<MetaExpression>, MetadataExpression {
	private final Iterator<E> iterator;
	private final Function<E, MetaExpression> transformer;

	/**
	 * Create a new MetaExpressionIterator from a source and a transformation
	 *
	 * @param source      the source Iterator
	 * @param transformer the function used to transform the elements of the source to usable MetaExpression
	 */
	public MetaExpressionIterator(Iterator<E> source, Function<E, MetaExpression> transformer) {
		this.iterator = source;
		this.transformer = transformer;
	}

	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}

	@Override
	public MetaExpression next() {
		return transformer.apply(iterator.next());
	}
}
