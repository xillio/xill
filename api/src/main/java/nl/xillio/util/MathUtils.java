package nl.xillio.util;

import java.math.BigInteger;
import java.util.IntSummaryStatistics;

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
                Integer intR = addExactWithoutException(a.intValue(), b.intValue());
                if (intR != null) {
                    return intR;
                }
            case LONG:
                Long longR = addExactWithoutException(a.longValue(), b.longValue());
                if (longR != null) {
                    return longR;
                }
            case BIG:
                BigInteger bigA = getBig(a);
                BigInteger bigB = getBig(b);
                return bigA.add(bigB);
            case DOUBLE:
                return a.doubleValue() + b.doubleValue();
            default:
                throw new UnsupportedOperationException("Type " + type + " is not supported by the add operation.");
        }
    }

    private static BigInteger getBig(Number number) {
        if (number instanceof BigInteger) {
            return (BigInteger) number;
        }

        return BigInteger.valueOf(number.longValue());
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
                Integer intR = subtractExactWithoutException(a.intValue(), b.intValue());
                if (intR != null) {
                    return intR;
                }
            case LONG:
                Long longR = subtractExactWithoutException(a.longValue(), b.longValue());
                if (longR != null) {
                    return longR;
                }
            case BIG:
                BigInteger bigA = getBig(a);
                BigInteger bigB = getBig(b);
                return bigA.subtract(bigB);
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
                int intResult = a.intValue() / b.intValue();
                if (intResult * b.intValue() == a.intValue()) {
                    return intResult;
                }
            case LONG:
                long longResult = a.longValue() / b.longValue();
                if (longResult * b.longValue() == a.longValue()) {
                    return longResult;
                }
            case BIG:
                BigInteger bigA = getBig(a);
                BigInteger bigB = getBig(b);
                BigInteger result = bigA.divide(bigB);
                if (result.multiply(bigB).equals(bigA)) {
                    return result;
                }
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
                Integer intR = multiplyExactWithoutException(a.intValue(), b.intValue());
                if (intR != null) {
                    return intR;
                }
            case LONG:
                Long longR = multiplyExactWithoutException(a.longValue(), b.longValue());
                if (longR != null) {
                    return longR;
                }
            case BIG:
                BigInteger bigA = getBig(a);
                BigInteger bigB = getBig(b);
                return bigA.multiply(bigB);
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
            case BIG:
                BigInteger bigA = getBig(a);
                BigInteger bigB = getBig(b);
                return bigA.mod(bigB);
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
            case BIG:
                BigInteger bigA = getBig(a);
                return bigA.pow(b.intValue());
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
     * a value less than {@code 0} if {@code x < y}; and
     * a value greater than {@code 0} if {@code x > y}
     * @throws UnsupportedOperationException if no supported number type is found
     */
    public static int compare(Number a, Number b) {
        NumberType type = NumberType.forClass(a.getClass(), b.getClass());
        switch (type) {
            case INT:
                return Integer.compare(a.intValue(), b.intValue());
            case LONG:
                return Long.compare(a.longValue(), b.longValue());
            case BIG:
                BigInteger bigA = getBig(a);
                BigInteger bigB = getBig(b);
                return bigA.compareTo(bigB);
            case DOUBLE:
                return Double.compare(a.doubleValue(), b.doubleValue());
            default:
                throw new UnsupportedOperationException("Type " + type + " is not supported by the compare operation.");
        }
    }

    /**
     * Recreate the addExact method from Math.java to check for overflow while avoiding throwing and handling
     * of Exceptions (including empty catch blocks)
     *
     * @return null if an overflow would occur else the result of the addition
     */
    static Integer addExactWithoutException(int x, int y) {
        int r = x + y;
        if (((x ^ r) & (y ^ r)) < 0) {
            return null;
        }
        return r;
    }

    /**
     * Recreate the addExact method from Math.java to check for overflow while avoiding throwing and handling
     * of Exceptions (including empty catch blocks)
     *
     * @return null if an overflow would occur else the result of the addition
     */
    static Long addExactWithoutException(long x, long y) {
        long r = x + y;
        if (((x ^ r) & (y ^ r)) < 0) {
            return null;
        }
        return r;
    }

    /**
     * Recreate the subtractExact method from Math.java to check for overflow while avoiding throwing and handling
     * of Exceptions (including empty catch blocks)
     *
     * @return null if an overflow would occur else the result of the subtraction
     */
    static Integer subtractExactWithoutException(int x, int y) {
        int r = x - y;
        if (((x ^ y) & (x ^ r)) < 0) {
            return null;
        }
        return r;
    }

    /**
     * Recreate the subtractExact method from Math.java to check for overflow while avoiding throwing and handling
     * of Exceptions (including empty catch blocks)
     *
     * @return null if an overflow would occur else the result of the subtraction
     */
    static Long subtractExactWithoutException(long x, long y) {
        long r = x - y;
        if (((x ^ y) & (x ^ r)) < 0) {
            return null;
        }
        return r;
    }

    /**
     * Recreate the multiplyExact method from Math.java to check for overflow while avoiding throwing and handling
     * of Exceptions (including empty catch blocks)
     *
     * @return null if an overflow would occur else the result of the multiplication
     */
    static Integer multiplyExactWithoutException(int x, int y) {
        long r = (long) x * (long) y;
        if ((int) r != r) {
            return null;
        }
        return (int) r;
    }

    /**
     * Recreate the multiplyExact method from Math.java to check for overflow while avoiding throwing and handling
     * of Exceptions (including empty catch blocks)
     *
     * @return null if an overflow would occur else the result of the multiplication
     */
    static Long multiplyExactWithoutException(long x, long y) {
        long r = x * y;
        long ax = Math.abs(x);
        long ay = Math.abs(y);
        if (((ax | ay) >>> 31 != 0)) {
            // Some bits greater than 2^31 that might cause overflow
            // Check the result using the divide operator
            // and check for the special case of Long.MIN_VALUE * -1
            if (((y != 0) && (r / y != x)) ||
                    (x == Long.MIN_VALUE && y == -1)) {
                return null;
            }
        }
        return r;
    }

    /**
     * The supported number types.
     * Note that these types should be ordered from low to high precedence
     */
    private enum NumberType {
        DOUBLE(Double.class, Float.class),
        LONG(Long.class),
        BIG(BigInteger.class),
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
            for (Class<?> testClass : numberClasses) {
                if (testClass.isAssignableFrom(clazz)) {
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
