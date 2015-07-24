package nl.xillio.xill.api.inject;

import java.util.function.Supplier;

import com.google.inject.Provider;

/**
 * This class represents a factory that will create a new instance of
 *
 * @param <T>
 *        the class that will be made
 */
public class Factory<T> implements Provider<T> {
	private final Supplier<T> supplier;

	/**
	 * Create a new {@link Factory} that will run a {@link Supplier} to get an instance
	 *
	 * @param supplier
	 *        the supplier
	 */
	public Factory(final Supplier<T> supplier) {
		this.supplier = supplier;
	}

	/**
	 * Create a new {@link Factory} that will instantiate a class using an empty constructor
	 *
	 * @param clazz
	 *        the type of class
	 */
	public Factory(final Class<T> clazz) {
		supplier = () -> {
			try {
				clazz.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				throw new FactoryBuilderException("Failed to instantiate a " + clazz.getName(), e);
			}
			return null;
		};
	}

	@Override
	public T get() {
		return supplier.get();
	}

}
