package nl.xillio.migrationtool;


import me.biesaart.event.Event;
import me.biesaart.event.EventDispatcher;
import nl.xillio.license.License;
import nl.xillio.license.LicenseFactory;
import nl.xillio.license.LicenseValidator;
import nl.xillio.license.SoftwareModule;
import nl.xillio.license.util.PublicKeyReaderUtil;
import nl.xillio.migrationtool.dialogs.AddLicenseDialog;
import nl.xillio.util.XillioHomeFolder;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;

/**
 * This class is responsible for performing a license check.
 */
public class LicenseUtils {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final File LICENSE_FILE = new File(XillioHomeFolder.forXillIDE(), "license.json");
    public static final int DAYS_NEAR_EXPIRATION = 10;
    private static LicenseFactory licenseFactory;
    private final static EventDispatcher<License> licenseChangeEvent = new EventDispatcher<>();

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
        return isValid(LICENSE_FILE);
    }

    /**
     * Check a license file in a specific location for validity.
     *
     * @param licenseFile the file
     * @return true if and only if the license is valid
     */
    public static boolean isValid(File licenseFile) {
        if (licenseFile == null || !licenseFile.exists() || !licenseFile.isFile()) {
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

    /**
     * Show a license dialog if the current license if not valid.
     *
     * @param insist Ask for a license even though it is valid
     */
    public static boolean performValidation(boolean insist) {

        if (!insist && isValid()) {
            return true;
        }

        AddLicenseDialog dialog = new AddLicenseDialog();
        dialog.showAndWait();

        File chosenFile = dialog.getChosen();

        if (LicenseUtils.isValid(chosenFile)) {
            try {
                FileUtils.copyFile(chosenFile, LicenseUtils.LICENSE_FILE);
            } catch (IOException e) {
                LOGGER.error("Failed to copy license file", e);
            }
        }

        return isValid();
    }

    /**
     * Get the amount of days until the license expires.
     */
    public static long daysToExpiration() {
        // Check if the license file exists.
        if (!LICENSE_FILE.exists()) {
            return 0;
        }

        // Subtract the epoch days of now from the expiry date.
        long expiry = getLicense().getLicenseDetails().getExpiryDate().toEpochDay();
        long now = LocalDate.now().toEpochDay();
        return expiry - now;
    }

    private static License getLicense(InputStream stream) throws IOException {
        try {
            return getFactory().fetchLicense(stream);
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            /*
                We catch all exceptions here because we don't want a mistake in the license library
                to allow the application to start
            */
            LOGGER.error("Failed to validate license file", e);
            return License.INVALID_LICENSE;
        }
    }

    private static boolean isValid(License license) {
        boolean valid = license.isValid() && license.getLicenseDetails().isValidForSoftwareModule(SoftwareModule.IDE);
        if (valid) {
            licenseChangeEvent.fire(license);
        }
        return valid;
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
        return License.class.getResourceAsStream("/publickey");
    }

    public static Event<License> getOnLicenseChange() {
        return licenseChangeEvent.getEvent();
    }
}
