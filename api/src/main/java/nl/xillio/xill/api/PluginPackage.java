package nl.xillio.xill.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.text.WordUtils;

import nl.xillio.plugins.interfaces.Loadable;
import nl.xillio.xill.api.construct.Construct;

/**
 * This class represents the base for all Xill plugins
 */
public abstract class PluginPackage implements Loadable<PluginPackage>, AutoCloseable {
	private final List<Construct> constructs = new ArrayList<>();
	private final String defaultName;

	/**
	 * Create a new {@link PluginPackage} and set the default name
	 */
	public PluginPackage() {
		// Set the default name
		String name = getClass().getSimpleName();
		String superName = PluginPackage.class.getSimpleName();
		if (name.endsWith(superName)) {
			name = name.substring(0, name.length() - superName.length());
		}
		defaultName = WordUtils.capitalize(name);
	}

	/**
	 * Get a construct from this package
	 *
	 * @param name
	 *        the name of the construct
	 * @return the construct or null if none was found for the provided name
	 */
	public final Construct getConstruct(final String name) {
		Optional<Construct> result = getConstructs().stream().filter(c -> c.getName().equals(name)).findAny();

		if (result.isPresent()) {
			return result.get();
		}
		return null;
	}

	/**
	 * By default the name of a {@link PluginPackage} is the concrete implementation name acquired using {@link Class#getSimpleName()} without the {@link PluginPackage} suffix
	 * @return the name of the package
	 */
	public String getName() {
		return defaultName;
	}

	/**
	 * Add a construct to the package
	 *
	 * @param construct
	 *        the construct to be added
	 * @throws IllegalArgumentException
	 *         when a construct with the same name already exists
	 */
	protected final void add(final Construct construct) throws IllegalArgumentException {
		if (getConstructs().stream().anyMatch(c -> c.getName().equals(construct.getName()))) {
			System.out.println(construct.getName());
			throw new IllegalArgumentException("A construct with the same name exsits.");
		}

		getConstructs().add(construct);
	}

	/**
	 * Add constructs to the package.
	 * This is a shortcut to calling {@link #add(Construct)} multiple times.
	 *
	 * @param constructs
	 *        the constructs to add the the list
	 * @throws IllegalArgumentException
	 *         when a construct with the same name already exists
	 */
	protected final void add(final Construct... constructs) throws IllegalArgumentException {
		for (Construct c : constructs) {
			add(c);
		}
	}

	/**
	 * Add constructs to the package.
	 * This is a shortcut to calling {@link #add(Construct)} multiple times.
	 *
	 * @param constructs
	 *        the constructs to add to the list
	 * @throws IllegalArgumentException
	 *         when a construct with the same name already exists
	 */
	protected final void add(final Collection<Construct> constructs) throws IllegalArgumentException {
		for (Construct c : constructs) {
			add(c);
		}
	}

	/**
	 * Remove all constructs from this package and call the {@link AutoCloseable#close()} on all constructs that implement it.
	 */
	protected final void purge() {
		for (Construct construct : getConstructs()) {
			if (construct instanceof AutoCloseable) {
				try {
					((AutoCloseable) construct).close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		getConstructs().clear();
	}

	/**
	 * @return Returns the version of the package
	 */
	public String getVersion() {
		return getClass().getPackage().getImplementationVersion();
	}

	@Override
	public void close() throws Exception {
		purge();
	}

	/**
	 * @return the constructs
	 */
	public List<Construct> getConstructs() {
		return constructs;
	}
}
