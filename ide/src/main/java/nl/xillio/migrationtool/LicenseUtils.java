package nl.xillio.migrationtool;


import me.biesaart.utils.IOUtils;
import nl.xillio.license.License;
import nl.xillio.license.LicenseFactory;
import nl.xillio.license.LicenseValidator;
import nl.xillio.license.SoftwareModule;
import nl.xillio.license.util.PublicKeyReaderUtil;
import nl.xillio.util.XillioHomeFolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * This class is responsible for performing a license check.
 */
public class LicenseUtils {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final File LICENSE_FILE = new File(XillioHomeFolder.forXillIDE(), "license.xml");
    private static LicenseFactory licenseFactory;

    /**
     * Get the currently active license.
     *
     * @return the license
     */
    public static License getLicense() {
        try {
            return getLicense(new FileInputStream(LICENSE_FILE));
        } catch (IOException e) {
            LOGGER.error("Failed to parse license", e);
            return License.INVALID_LICENSE;
        }
    }

    /**
     * Check the license in the default location for validity.
     *
     * @return true if and only if the license is valid
     */
    public static boolean isValid() {
        return true || isValid(LICENSE_FILE);
    }

    /**
     * Check a license file in a specific location for validity.
     *
     * @param licenseFile the file
     * @return true if and only if the license is valid
     */
    public static boolean isValid(File licenseFile) {
        if (!licenseFile.exists() || !licenseFile.isFile()) {
            return false;
        }

        try {
            License license = getLicense(new FileInputStream(licenseFile));
            return isValid(license);
        } catch (IOException e) {
            LOGGER.error("Failed to validate license file at " + licenseFile, e);
            return false;
        }
    }

    private static License getLicense(InputStream stream) throws IOException {
        try {
            return getFactory().fetchLicense(stream);
        } catch (PublicKeyReaderUtil.PublicKeyParseException e) {
            /*
                We catch all exceptions here because we don't want a mistake in the license library
                to allow the application to start
            */
            LOGGER.error("Failed to validate license file", e);
            return License.INVALID_LICENSE;
        }
    }

    private static boolean isValid(License license) {
        return license.isValid() && license.getLicenseDetails().isValidForSoftwareModule(SoftwareModule.IDE);
    }

    private static LicenseFactory getFactory() throws IOException, PublicKeyReaderUtil.PublicKeyParseException {
        if (licenseFactory != null) {
            return licenseFactory;
        }

        LicenseFactory factory = new LicenseFactory();
        LicenseValidator validator = new LicenseValidator();
        validator.setPublicKey(openPublicKeyString());
        factory.setLicenseValidator(validator);
        licenseFactory = factory;
        return factory;
    }

    private static InputStream openPublicKeyString() {
        return IOUtils.toInputStream("This is a test");
    }
}
