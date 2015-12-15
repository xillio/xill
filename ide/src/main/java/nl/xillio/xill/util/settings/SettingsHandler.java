package nl.xillio.xill.util.settings;
import nl.xillio.util.XillioHomeFolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;

/**
 * Class that is main point for dealing with settings in Xill IDE
 * It encapsulates all settings handlers.
 *
 * @author Zbynek Hochmann
 */
public class SettingsHandler {

    private final static File SETTINGS_FILE = new File(XillioHomeFolder.forXillIDE(), "settings.cfg");

    private ContentHandlerImpl content;
    private static SettingsHandler settings = new SettingsHandler();
    private SimpleVariableHandler simple;
    private ProjectSettingsHandler project;

    private static final Logger LOGGER = LogManager.getLogger(SettingsHandler.class);

    /**
     * @return The instance of settings handler
     */
    public static SettingsHandler getSettingsHandler() {
        return settings;
    }

    private SettingsHandler() {// singleton class

        this.content = new ContentHandlerImpl(SETTINGS_FILE);
        try {
            this.content.init();
        } catch (IOException e) {
            LOGGER.error("Cannot initialize settings handler.", e);
        }

        this.simple = new SimpleVariableHandler(this.content);
        this.project = new ProjectSettingsHandler(this.content);
    }

    /**
     * @return The implementation of simple variable settings
     */
    public SimpleVariableHandler simple() {
        return this.simple;
    }

    /**
     * @return The implementation of project settings
     */
    public ProjectSettingsHandler project() {
        return this.project;
    }

    /**
     * Set the save mechanism (see {@link nl.xillio.xill.util.settings.ContentHandler#setManualCommit(boolean)})
     *
     * @param manual true = manual commit, false = auto commit (default)
     */
    public void setManualCommit(boolean manual) {
        this.content.setManualCommit(manual);
    }

    /**
     * Save all changes from last commit() if manual commit is on (see {@link nl.xillio.xill.util.settings.ContentHandler#commit()})
     */
    public void commit() {
        this.content.commit();
    }
}
