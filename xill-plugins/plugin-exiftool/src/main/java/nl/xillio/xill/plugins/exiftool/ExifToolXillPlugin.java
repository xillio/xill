package nl.xillio.xill.plugins.exiftool;

import com.google.inject.Provides;
import com.google.inject.Singleton;
import nl.xillio.exiftool.ExifTool;
import nl.xillio.exiftool.ProcessPool;
import nl.xillio.plugins.XillPlugin;

/**
 * This plugin contains some constructs to interact with the exiftool.
 *
 * @author Thomas Biesaart
 */
public class ExifToolXillPlugin extends XillPlugin {

    @Singleton
    @Provides
    ProcessPool processPool() {
        return ExifTool.buildPool();
    }
}
