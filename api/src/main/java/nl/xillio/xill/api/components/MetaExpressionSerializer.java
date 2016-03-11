package nl.xillio.xill.api.components;

/**
 * This interface represents an object that can convert a MetaExpression to an Object.
 *
 * @author Thomas Biesaart
 * @author Titus Nachbauer
 */
public interface MetaExpressionSerializer {

    Object extractValue(MetaExpression metaExpression);

    /**
     * A null implementation.
     */
    MetaExpressionSerializer NULL = a -> null;
}
