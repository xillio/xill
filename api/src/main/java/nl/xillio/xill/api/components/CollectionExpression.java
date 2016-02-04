package nl.xillio.xill.api.components;

/**
 * Abstraction for Objects and Lists since they have commonalities.
 *
 * @author Pieter Dirk Soels
 */
public abstract class CollectionExpression extends MetaExpression {

    @Override
    public String getStringValue() {
        return toString();
    }

    @Override
    public boolean getBooleanValue() {
        return true;
    }

    @Override
    public boolean isNull() {
        return false;
    }
}
