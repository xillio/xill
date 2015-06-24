package nl.xillio.xill.api;

/**
 * A function with three arguments.
 *
 * @param <S>
 *        the first argument type
 * @param <T>
 *        the second argument type
 * @param <U>
 *        the third argument type
 * @param <R>
 *        the result type
 */
@FunctionalInterface
public interface TriFunction<S, T, U, R> {

	/**
	 * Applies this function to the given arguments.
	 *
	 * @param s
	 *        the first function argument
	 * @param t
	 *        the second function argument
	 *
	 * @param u
	 *        the third function argument
	 * @return the function result
	 */
	R apply(final S s, final T t, final U u);
}
