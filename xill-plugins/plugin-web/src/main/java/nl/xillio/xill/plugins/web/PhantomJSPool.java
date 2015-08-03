package nl.xillio.xill.plugins.web;

import nl.xillio.xill.plugins.web.constructs.LoadPageConstruct.Options;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;

import java.util.Vector;

/**
 * This class encapsulates the pooling mechanism for Selenium's PhantomJS processes used in Web.loadpage() Xill
 * function.
 *
 * @author Zbynek Hochmann
 */
public class PhantomJSPool {

    private final Logger log = LogManager.getLogger();

    Vector<Entity> poolEntities = new Vector<Entity>();
    int maxPoolSize = 10; // maximum of entities in pool (entity means running PhantomJS process)

    /**
     * It represents the options that have been used as CLI arguments of
     * PhantomJS process. It helps to identify the proper PhantomJS
     * process - i.e. existing process that has been started with the same CLI
     * parameters as we are looking for.
     * <p>
     * This class could be omitted and used Options class instead but there can be/could have been more
     * various characters identifying particular PJS process.
     */
    public class Identifier {

        private final Options options;

        /**
         * Constructor of Identifier class
         *
         * @param options The CLI options that has been used when given PJS process has been started
         */
        public Identifier(final Options options) {
            this.options = options;
        }

        /**
         * @return The CLI options that has been used when given PJS process has been started
         */
        public Options getOptions() {
            return options;
        }
    }

    /**
     * It represents one item in the pool - i.e. one existing PhantomJS process with other related information
     * Entity consists basically from one pair: PJS process (driver) and
     * the options (id) that have been used when PJS has been started.
     * The PJS CLI options once used for PJS process, they cannot be changed for particular existing PJS anymore.
     * So the CLI options are like identificators when we are looking in the pool for PJS process with given CLI
     * options.
     */
    public class Entity implements AutoCloseable {

        private WebDriver driver;
        private Identifier id;
        private boolean used = false; // is this entity in use (true) or free to use (false)?

        /**
         * @return true if this PJS process is currently in use; otherwise false which means that this PJS process is
         * free to re/use
         */
        public boolean isUsed() {
            return used;
        }

        /**
         * Creates new PhantomJS pool entity
         * It starts new PJS process with CLI options given by Identifier
         * It sets this PJS process as used automatically
         *
         * @param id in fact it is PJS process CLI options
         *
         * @return Newly created WebDriver instance (i.e. PhantomJS)
         */
        private WebDriver create(final Identifier id) {
            this.id = id;
            driver = id.getOptions().createDriver();
            used = true;
            return driver;
        }

        /**
         * Determines if the identifier 'id' matches this entity
         * It compares all CLI options if this particular existing PJS process
         * in the pool has been started with same CLI options as we are looking for
         *
         * @param id in fact it is PJS process CLI options
         *
         * @return true if CLI options matches
         */
        public boolean compare(final Identifier id) {
            return this.id.getOptions().compareDCap(id.getOptions());
        }

        /**
         * Reuse PJS process
         * It takes this PJS process (free to use check must be done before this call)
         * and set non-CLI options for this PJS process (CLI options must match) and set as currently in use
         *
         * @param id It is used for passing the options that are to be set
         *           after driver is created (not CLI options)
         */
        private void reuse(final Identifier id) {
            used = true; // set to be used
            id.getOptions().setDriverOptions(driver); // re-set driver (non-CLI) options
        }

        /**
         * Terminates PhantomJS process of this entity
         * It will end up this PJS process.
         */
        private void dispose() {
            driver.quit();
        }

        /**
         * Stops using this entity (PhantomJS process)
         * It sets that this PJS process is free to use now
         */
        public void release() {
            used = false;
        }

        /**
         * @return WebDriver of this entity (PhantomJS process class)
         */
        public WebDriver getDriver() {
            return driver;
        }

        @Override
        public void close() throws Exception {
            release();
        }
    }

    /**
     * Creates PJS pool
     *
     * @param maxPoolSize Maximum amount of entities (PhantomJS processes) that can
     *                    be in the pool at one time
     */
    public PhantomJSPool(final int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
        Runtime.getRuntime().addShutdownHook(new Thread(this::dispose));
    }

    /**
     * It tries to find any existing PhantomJS process that has the same
     * CLI options given by 'id' parameter and it's currently not in use.
     * It sets 'used' flag to true immediately on the found item.
     *
     * @param id in fact it is PJS process CLI options
     *
     * @return Found entity or null if not found convenient process
     */
    private Entity findFirstFreeEntityAndReuse(final Identifier id) {
        for (Entity item : poolEntities) {
            if (item.compare(id) && !item.isUsed()) {
                item.reuse(id);
                return item;
            }
        }
        return null;
    }

    /**
     * This covers the core of entire pool mechanism
     * It looks if the PhantomJS process with required CLI options exists in a pool and it's free
     * to use - then it will return it and reuse, otherwise it creates new one
     *
     * @param id in fact it is PJS process CLI options
     *
     * @return WebDriver (i.e. PhantomJS process) with provided options that can be used for web operations
     */
    public Entity get(final Identifier id) {
        // System.out.println(String.format("PJS: PhantomJSPool get(), thread %1$d", Thread.currentThread().getId()));
        log.debug("PJSPool: New request for WebDriver...");
        Entity item = findFirstFreeEntityAndReuse(id);
        if (item == null) { // no proper found
            // we create new one
            if (poolEntities.size() >= maxPoolSize) {
                // pool reached full size - will try to release not used one
                // entity (with different id (CLI options) - obviously because
                // there is no free PJS process with required id to use.
                if (!freeUnusedEntity()) {
                    // there is no unused entity available
                    log.error("PJSPool: No free slot for next PhantomJS process!");
                    return null; // cannot create new PJS process!
                }
            }
            // else
            // create brand new entity
            log.debug("PJSPool: Creating new driver...");
            item = new Entity();
            item.create(id);
            poolEntities.add(item);
        } else {
            // found free entity with the same id
            log.debug("PJSPool: Reusing new driver...");
        }
        return item;
    }

    /**
     * Tries to find existing entity in the pool having provided WebDriver
     *
     * @param driver WebDriver class representing PhantomJS
     *
     * @return Found entity or null if not found
     */
    public Entity find(final WebDriver driver) {
        for (Entity item : poolEntities) {
            if (item.driver == driver) {
                return item;
            }
        }
        return null;
    }

    /**
     * It ends up one of not currently used PhantomJS process in
     * the pool and removes the entity from the pool
     *
     * @return true if entity has been freed or false if there is no any not
     * used entity in the pool
     */
    private boolean freeUnusedEntity() {
        for (Entity item : poolEntities) {
            if (!item.isUsed()) {
                item.dispose(); // it will end up PJS process
                return poolEntities.remove(item);
            }
        }
        return false;
    }

    /**
     * Creates new Identifier object from Options object
     *
     * @param options PJS CLI options
     *
     * @return created Identifier
     */
    public Identifier createIdentifier(final Options options) {
        return new Identifier(options);
    }

    /**
     * Disposes entire PJS pool - i.e. all pool entities (~all PhantomJS
     * processes in the pool will be terminated)
     */
    public void dispose() {
        try {
            for (Entity item : poolEntities) {
                item.dispose();
            }
            poolEntities.clear();
        } catch (Exception e) {
            log.error("Error when closing PhantomJS instances! " + e.getMessage());
        }
    }

}
