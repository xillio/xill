package nl.xillio.plugins;

import com.google.common.reflect.ClassPath;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Injector;
import me.biesaart.utils.Log;
import nl.xillio.xill.api.construct.Construct;
import org.apache.commons.lang3.text.WordUtils;
import org.slf4j.Logger;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * This class represents the base for all Xill plugins.
 */
public abstract class XillPlugin extends AbstractModule implements AutoCloseable {
    private static final Logger LOGGER = Log.get();
    private final List<Construct> constructs = new ArrayList<>();
    private final String defaultName;
    private boolean loadingConstructs = false;
    @Inject
    private Injector injector;

    /**
     * Creates a new {@link XillPlugin} and sets the default name.
     */
    public XillPlugin() {
        // Set the default name
        String name = getClass().getSimpleName();
        String superName = XillPlugin.class.getSimpleName();
        if (name.endsWith(superName)) {
            name = name.substring(0, name.length() - superName.length());
        }
        defaultName = WordUtils.capitalize(name);
    }

    /**
     * Configures bindings for Injection.
     */
    public void configure() {
    }

    /**
     * Gets a construct from this package.
     *
     * @param name the name of the construct
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
     * By default, the name of a {@link XillPlugin} is the concrete implementation name acquired using {@link Class#getSimpleName()}
     * without the {@link XillPlugin} suffix.
     *
     * @return the name of the package
     */
    public String getName() {
        return defaultName;
    }

    /**
     * Adds a construct to the package.
     *
     * @param construct the construct to add
     * @throws IllegalArgumentException when a construct with the same name already exists
     */
    protected final void add(final Construct construct) throws IllegalArgumentException {
        if (!loadingConstructs) {
            throw new IllegalStateException("Can only load constructs in the loadConstructs() method.");
        }
        if (getConstructs().stream().anyMatch(c -> c.getName().equals(construct.getName()))) {
            throw new IllegalArgumentException("A construct with the same name exsits.");
        }

        getConstructs().add(construct);
    }

    /**
     * Adds constructs to the package.
     *
     * @param constructs the constructs to add the the list
     * @throws IllegalArgumentException when a construct with the same name already exists
     */
    protected final void add(final Construct... constructs) throws IllegalArgumentException {
        if (!loadingConstructs) {
            throw new IllegalStateException("Can only load constructs in the loadConstructs() method.");
        }
        for (Construct c : constructs) {
            add(c);
        }
    }

    /**
     * Adds a list of constructs to the package.
     *
     * @param constructs the constructs to add to the list
     * @throws IllegalArgumentException when a construct with the same name already exists
     */
    protected final void add(final Collection<Construct> constructs) throws IllegalArgumentException {
        if (!loadingConstructs) {
            throw new IllegalStateException("Can only load constructs in the loadConstructs() method.");
        }
        constructs.forEach(this::add);
    }

    /**
     * Removes all constructs from this package and call {@link AutoCloseable#close()} on all constructs that implements it.
     */
    protected final void purge() {
        getConstructs().stream()
                .filter(construct -> construct instanceof AutoCloseable)
                .map(construct -> (AutoCloseable) construct)
                .forEach(construct -> {
                    try {
                        construct.close();
                    } catch (Exception e) {
                        LOGGER.error("Failed to close " + construct, e);
                    }
                });
        getConstructs().clear();
    }

    /**
     * Returns the version of the package.
     *
     * @return the version of the package
     */
    public String getVersion() {
        return getClass().getPackage().getImplementationVersion();
    }

    @Override
    public void close() throws Exception {
        purge();
    }

    /**
     * Loads all the constructs in the package.
     */
    public final void initialize() {
        loadingConstructs = true;
        loadConstructs();
        loadingConstructs = false;
    }

    /**
     * This is where the package can add all the constructs. If this method is not overridden it will load all constructs in the subpackage 'construct'.
     */
    public void loadConstructs() {
        //Load all constructs
        try {
            ClassPath classPath = ClassPath.from(getClass().getClassLoader());
            for (ClassPath.ClassInfo classInfo : classPath.getTopLevelClasses(getClass().getPackage().getName() + ".constructs")) {
                Class<?> constructClass = classInfo.load();
                if (Construct.class.isAssignableFrom(constructClass) && !Modifier.isAbstract(constructClass.getModifiers())) {
                    //This is a construct
                    add((Construct) injector.getInstance(constructClass));
                }
            }
        } catch (IOException e) {
            LOGGER.error("Error while auto loading constructs", e);
        }
    }

    /**
     * @return the constructs
     */
    public List<Construct> getConstructs() {
        return constructs;
    }
}
