package nl.xillio.xill.api.components;

/**
 * This interface represents an object that can convert an Object to a MetaExpression.
 *
 * @author Thomas Biesaart
 * @author Titus Nachbauer
 */
public interface MetaExpressionDeserializer {

    MetaExpression parseObject(Object object);

    /**
     * A null implementation.
     */
    MetaExpressionDeserializer NULL = a -> null;
}
