package nl.xillio.xill.plugins.concurrency.data;

import nl.xillio.xill.api.components.MetaExpression;

/**
 * This implementation of the XillQueue logs every item pushed into it to the console and
 * iterates results from a list.
 *
 * @author Thomas Biesaart
 * @author Titus Nachbauer
 */
public class MockXillQueue extends XillQueue {

    public MockXillQueue(Iterable<MetaExpression> input) {
        super(10);
    }
}
