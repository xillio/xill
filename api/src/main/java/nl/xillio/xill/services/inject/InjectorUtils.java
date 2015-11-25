package nl.xillio.xill.services.inject;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.List;

/**
 * This class contains some utility for the {@link Injector}
 */
public class InjectorUtils {
    private static final Logger log = LogManager.getLogger(InjectorUtils.class);
    private static Injector injector;
    private static List<Module> module;

    /**
     * Creates or returns the global injector. If no injector exists it will be initialized with defaults
     *
     * @return the injector
     */
    public static Injector getGlobalInjector() {
        if (injector == null) {
            if (module == null) {
                defaultConfig();
            }
            injector = Guice.createInjector(module);
        }

        return injector;
    }

    /**
     * Initialize the injector with custom settings
     *
     * @param globalModules the modules that should be used by the injector
     */
    public static void initialize(final List<Module> globalModules) {
        if (module != null || injector != null) {
            throw new IllegalStateException("The InjectorUtils have already been initialized.");
        }
        log.info("Initializing InjectorUtils with custom settings...");
        module = globalModules;
    }

    private static void defaultConfig() {
        log.info("Initializing InjectorUtils with defaults...");
        module = Collections.singletonList(new DefaultInjectorModule());
    }

    /**
     * Get or create an injector of the global module
     *
     * @return the module
     */
    public static List<Module> getModule() {
        if (module == null) {
            defaultConfig();
        }

        return module;
    }

    /**
     * Get an instance from the injector
     *
     * @param <T>   the requested type
     * @param clazz the type of instance that should be made
     * @return the requested object
     */
    public static <T> T get(Class<T> clazz) {
        return getGlobalInjector().getInstance(clazz);
    }

}
