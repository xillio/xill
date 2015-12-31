package nl.xillio.xill.docgen;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * This class represents the configuration of the DocGen system
 *
 * @author Thomas Biesaart
 * @author Ivor van der Hoog
 * @since 12-8-2015
 */
public class DocGenConfiguration {
    private static final String PROPERTY_ROOT = "rootFolder";
    private static final String TEMPLATE_ROOT = "templatesUrl";
    private static final String RESOURCE_ROOT = "resourcesUrl";
    private static final String CONFIG_URL = "/docgen.properties";
    private static final Logger LOGGER = LogManager.getLogger();
    private final Properties properties;

    public DocGenConfiguration() {
        properties = new Properties(getDefaults());
        try {
            InputStream stream = getClass().getResourceAsStream(CONFIG_URL);
            if (stream == null) {
                throw new FileNotFoundException("Properties file " + CONFIG_URL + " does not exist");
            }
            properties.load(stream);
        } catch (IOException e) {
            LOGGER.warn("Could not load " + CONFIG_URL + " using defaults...", e);
        }
    }

    /**
     * Load all the defaults for this configuration
     *
     * @return the defaults
     */
    private Properties getDefaults() {
        Properties defaults = new Properties();
        defaults.setProperty(PROPERTY_ROOT, "docgen_output");
        defaults.setProperty(TEMPLATE_ROOT, "templates");
        defaults.setProperty(RESOURCE_ROOT, "static");
        return defaults;
    }

    public File getDocumentationFolder() {
        String path = properties.getProperty(PROPERTY_ROOT);
        return new File(path);
    }

    public String getResourceUrl() {
        return properties.getProperty(RESOURCE_ROOT);
    }

    /**
     * Get the url to the template package
     *
     * @return the url
     */
    public String getTemplateUrl() {
        return properties.getProperty(TEMPLATE_ROOT);
    }
}
