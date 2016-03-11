package nl.xillio.xill.components.expressions.parallel;

import nl.xillio.xill.api.components.MetaExpression;

/**
 * This interface represents an object that can digest an iterator and result in an aggregation.
 *
 * Generally this expression will be part of the end of a pipeline and summarize the results.
 * Example
 * <code>
 *     var input = ...; //Some iterable input
 *     var map &lt;myFunction&gt;(input);
 *     var result = reduce&lt;aggregateFunction&gt;(map);
 * </code>
 *
 * @author Thomas Biesaart
 * @author Andrea Parrilli
 */
public interface PipelineTerminal {

    /**
     * Digest an iterable input using an abstract reduction function which results in an aggregation.
     *
     * @param input the iterable input
     * @return the aggregation
     */
    MetaExpression reduce(Iterable<MetaExpression> input);

}
