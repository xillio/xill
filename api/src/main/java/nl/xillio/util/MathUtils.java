package nl.xillio.util;

/**
 * This class represents some basic arithmetic operations on abstract Numbers.
 */
public class MathUtils {
    /**
     * Perform the arithmetic addition operation on two abstract numbers.
     *
     * @param a the left operand
     * @param b the right operand
     * @return the result of the operation
     * @throws UnsupportedOperationException if no supported number type is found
     */
    public static Number add(Number a, Number b) {
        NumberType type = NumberType.forClass(a.getClass(), b.getClass());
        switch (type) {
            case INT:
                return a.intValue() + b.intValue();
            case LONG:
                return a.longValue() + b.longValue();
            case FLOAT:
                return a.floatValue() + b.floatValue();
            case DOUBLE:
                return a.doubleValue() + b.doubleValue();
            default:
                throw new UnsupportedOperationException("Type " + type + " is not supported by the add operation.");
        }
    }

    /**
     * Perform the arithmetic subtraction operation on two abstract numbers.
     *
     * @param a the left operand
     * @param b the right operand
     * @return the result of the operation
     * @throws UnsupportedOperationException if no supported number type is found
     */
    public static Number subtract(Number a, Number b) {
        NumberType type = NumberType.forClass(a.getClass(), b.getClass());
        switch (type) {
            case INT:
                return a.intValue() - b.intValue();
            case LONG:
                return a.longValue() - b.longValue();
            case FLOAT:
                return a.floatValue() - b.floatValue();
            case DOUBLE:
                return a.doubleValue() - b.doubleValue();
            default:
                throw new UnsupportedOperationException("Type " + type + " is not supported by the subtract operation.");
        }
    }

    /**
     * Perform the arithmetic division operation on two abstract numbers.
     *
     * @param a the left operand
     * @param b the right operand
     * @return the result of the operation
     * @throws UnsupportedOperationException if no supported number type is found
     */
    public static Number divide(Number a, Number b) {
        NumberType type = NumberType.forClass(a.getClass(), b.getClass());
        switch (type) {
            case INT:
                return a.intValue() / b.intValue();
            case LONG:
                return a.longValue() / b.longValue();
            case FLOAT:
                return a.floatValue() / b.floatValue();
            case DOUBLE:
                return a.doubleValue() / b.doubleValue();
            default:
                throw new UnsupportedOperationException("Type " + type + " is not supported by the divide operation.");
        }
    }

    /**
     * Perform the arithmetic multiplication operation on two abstract numbers.
     *
     * @param a the left operand
     * @param b the right operand
     * @return the result of the operation
     * @throws UnsupportedOperationException if no supported number type is found
     */
    public static Number multiply(Number a, Number b) {
        NumberType type = NumberType.forClass(a.getClass(), b.getClass());
        switch (type) {
            case INT:
                return a.intValue() * b.intValue();
            case LONG:
                return a.longValue() * b.longValue();
            case FLOAT:
                return a.floatValue() * b.floatValue();
            case DOUBLE:
                return a.doubleValue() * b.doubleValue();
            default:
                throw new UnsupportedOperationException("Type " + type + " is not supported by the multiply operation.");
        }
    }

    /**
     * Perform the arithmetic modulo operation on two abstract numbers.
     *
     * @param a the left operand
     * @param b the right operand
     * @return the result of the operation
     * @throws UnsupportedOperationException if no supported number type is found
     */
    public static Number modulo(Number a, Number b) {
        NumberType type = NumberType.forClass(a.getClass(), b.getClass());
        switch (type) {
            case INT:
                return a.intValue() % b.intValue();
            case LONG:
                return a.longValue() % b.longValue();
            case FLOAT:
                return a.floatValue() % b.floatValue();
            case DOUBLE:
                return a.doubleValue() % b.doubleValue();
            default:
                throw new UnsupportedOperationException("Type " + type + " is not supported by the modulo operation.");
        }
    }

    /**
     * Perform the arithmetic exponential operation on two abstract numbers.
     *
     * @param a the left operand
     * @param b the right operand
     * @return the result of the operation
     * @throws UnsupportedOperationException if no supported number type is found
     */
    public static Number power(Number a, Number b) {
        NumberType type = NumberType.forClass(a.getClass(), b.getClass());
        switch (type) {
            case INT:
                return Math.pow(a.intValue(), b.intValue());
            case LONG:
                return Math.pow(a.longValue(), b.longValue());
            case FLOAT:
                return Math.pow(a.floatValue(), b.floatValue());
            case DOUBLE:
                return Math.pow(a.doubleValue(), b.doubleValue());
            default:
                throw new UnsupportedOperationException("Type " + type + " is not supported by the power operation.");
        }
    }

    /**
     * Perform the comparison operation on two abstract numbers.
     *
     * @param a the left operand
     * @param b the right operand
     * @return the value {@code 0} if {@code x == y};
     *         a value less than {@code 0} if {@code x < y}; and
     *         a value greater than {@code 0} if {@code x > y}
     * @throws UnsupportedOperationException if no supported number type is found
     */
    public static int compare(Number a, Number b) {
        NumberType type = NumberType.forClass(a.getClass(), b.getClass());
        switch (type) {
            case INT:
                return Integer.compare(a.intValue(), b.intValue());
            case LONG:
                return Long.compare(a.longValue(), b.longValue());
            case FLOAT:
                return Float.compare(a.floatValue(), b.floatValue());
            case DOUBLE:
                return Double.compare(a.doubleValue(), b.doubleValue());
            default:
                throw new UnsupportedOperationException("Type " + type + " is not supported by the compare operation.");
        }
    }


    /**
     * The supported number types.
     * Note that these types should be ordered from low to high precedence
     */
    private enum NumberType {
        DOUBLE(Double.class),
        FLOAT(Float.class),
        LONG(Long.class),
        INT(Integer.class, Byte.class, Short.class);

        private static final NumberType[] values = NumberType.values();
        private final Class<? extends Number>[] numberClasses;

        @SafeVarargs
        NumberType(Class<? extends Number>... numberClasses) {
            this.numberClasses = numberClasses;
        }

        /**
         * Test if this type supports a class
         *
         * @param clazz the class
         * @return true if and only if this type supports the number
         */
        public boolean supports(Class<? extends Number> clazz) {
            for (Class<?> testClazz : numberClasses) {
                if (testClazz.isAssignableFrom(clazz)) {
                    return true;
                }
            }

            return false;
        }

        public static NumberType forClass(Class<? extends Number> classA, Class<? extends Number> classB) {
            for (NumberType type : values) {
                if (type.supports(classA) || type.supports(classB)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("No compatible number type for " + classA.getSimpleName() + " or " + classB);
        }
    }
}
