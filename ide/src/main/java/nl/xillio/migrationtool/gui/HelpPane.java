package nl.xillio.migrationtool.gui;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import nl.xillio.migrationtool.Loader;
import nl.xillio.migrationtool.gui.editor.XillJSObject;
import nl.xillio.xill.docgen.DocGenConfiguration;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * This pane contains the documentation information.
 *
 * @author Thomas Biesaart
 */
public class HelpPane extends AnchorPane {
    @FXML
    private WebView webFunctionDoc;

    @FXML
    private HelpSearchBar helpSearchBar;

    // the logger
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * Instantiate the HelpPane and load the home page.
     */
    public HelpPane() {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/HelpPane.fxml"));
        loader.setClassLoader(getClass().getClassLoader());
        loader.setController(this);

        try {
            Node ui = loader.load();
            getChildren().add(ui);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
        helpSearchBar.setHelpPane(this);

        Loader.getInitializer().getOnLoadComplete().addListener(init -> {
            displayHome();
            webFunctionDoc.getEngine().getHistory().setMaxSize(0);
            webFunctionDoc.getEngine().getHistory().setMaxSize(100);
            helpSearchBar.setSearcher(init.getSearcher());
        });

        // Fill the highlight settings with keywords and builtins


        webFunctionDoc.getEngine().documentProperty().addListener((observable, o, n) -> {
            // Set the highlight settings
            JSObject window = (JSObject) webFunctionDoc.getEngine().executeScript("window");
            window.setMember(
                    "xillCoreOverride",
                    new XillJSObject(
                            Loader.getXill().createProcessor(
                                    new File("."),
                                    new File("."),
                                    Loader.getInitializer().getLoader()
                            )
                    )
            );
            // Load the Ace editors if present
            webFunctionDoc.getEngine().executeScript("if (typeof loadEditors !== 'undefined') {loadEditors()}");

        });

        heightProperty().addListener((observable, oldValue, newValue) -> helpSearchBar.handleHeightChange());

        // load the splash
        File splashFile = new File("helpfiles", "splash.html");
        SplashGenerator generator = new SplashGenerator(splashFile);
        generator.generateSplash();
        try {
            this.display(splashFile.toURI().toURL());
        } catch (MalformedURLException e) {
            LOGGER.error("Malformed url when displaying splash", e);
        }

        // Disable drag-and-drop, set the cursor graphic when dragging.
        webFunctionDoc.setOnDragDropped(null);
        webFunctionDoc.setOnDragOver(e -> getScene().setCursor(Cursor.DISAPPEAR));
    }

    /**
     * Displays the home page
     */
    public void displayHome() {
        try {
            this.display(new File("helpfiles", "index.html").toURI().toURL());
        } catch (MalformedURLException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * Display the page corresponding to a keyword.
     *
     * @param pluginPackage The package the function we want to display comes from
     * @param keyword       The name of the function in the package
     */
    public void display(final String pluginPackage, final String keyword) {
        File file = new File("helpfiles/" + pluginPackage + "/" + keyword + ".html");

        Platform.runLater(() -> webFunctionDoc.getEngine().load(file.toURI().toString()));
    }

    /**
     * Load the passed resource.
     *
     * @param resource the resource to display
     */
    public void display(final URL resource) {
        Platform.runLater(() -> webFunctionDoc.getEngine().load(resource.toExternalForm()));
    }

    private void back() {
        webFunctionDoc.getEngine().executeScript("history.back()");
    }

    private void forward() {
        webFunctionDoc.getEngine().executeScript("history.forward()");
    }

    @FXML
    private void buttonHelpHome() {
        displayHome();
    }

    @FXML
    private void buttonHelpBack() {
        back();
    }

    @FXML
    private void buttonHelpForward() {
        forward();
    }

    @FXML
    private void buttonHelpInfo() {
    }

    @Override
    public void requestFocus() {
        webFunctionDoc.requestFocus();
    }

    /**
     * The generator of the splash file.
     */
    private class SplashGenerator {
        private final File file;

        /**
         * Creates a SplashGenerator which will generate a splash html at a given file
         *
         * @param file
         */
        public SplashGenerator(final File file) {
            this.file = file;
        }

        /**
         * Generates the splash and places it in the Helpfiles folder
         */
        public void generateSplash() {
            Map<String, String> substitutions = new HashMap<String, String>();
            substitutions.put("style", getClass().getResource("/docgen/resources/_assets/css/style.css").toExternalForm());
            substitutions.put("splash", getClass().getResource("/docgen/resources/_assets/img/splash.png").toExternalForm());

            try {
                touch(file);
                FileWriter writer = new FileWriter(file);
                Template template = configureHTMLGenerator().getTemplate("splash.html");
                template.process(substitutions, writer);
                writer.close();
            } catch (IOException e) {
                LOGGER.error("IO exception when generating splash", e);
            } catch (TemplateException e) {
                LOGGER.error("Template exception when generating splash", e);
            }
        }

        private Configuration configureHTMLGenerator() {
            DocGenConfiguration docConfig = new DocGenConfiguration();
            Configuration config = new Configuration(Configuration.VERSION_2_3_23);
            config.setClassForTemplateLoading(getClass(), docConfig.getTemplateUrl());
            return config;
        }

        /**
         * Checks if a file exists, else creates that file.
         *
         * @param file The file we want to touch.
         * @throws IOException
         */
        private void touch(final File file) throws IOException {
            FileUtils.touch(file);
        }
    }
}
