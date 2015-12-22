package nl.xillio.xill.api.components;

/**
 *
 */
public interface MetaExpressionDeserializer {
    MetaExpression parseObject (Object object);

    class Identity implements MetaExpressionDeserializer{

        @Override
        public MetaExpression parseObject(Object object) {
            throw new IllegalArgumentException("The class type " + object.getClass().getName() + " has not been implemented by " + getClass().getName());
        }
    }
}
