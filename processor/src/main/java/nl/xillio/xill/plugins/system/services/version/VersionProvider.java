package nl.xillio.xill.plugins.system.services.version;

import nl.xillio.xill.services.XillService;

/**
 * This class get's the running version
 */
public class VersionProvider implements XillService {

    /**
     * The string used when no implementation version is found.
     */
    public static final String DEVELOP = "Development";

    /**
     * Get the current version
     *
     * @return the current version
     */
    public String getVersion() {
        String version = getClass().getPackage().getSpecificationVersion();

        if (version == null) {
            version = DEVELOP;
        }

        return version;
    }

}
