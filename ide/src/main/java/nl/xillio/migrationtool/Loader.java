package nl.xillio.migrationtool;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import javafx.scene.control.Alert;
import javafx.stage.Modality;
import nl.xillio.xill.docgen.impl.XillDocGen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;
import nl.xillio.xill.api.Xill;

/**
 * Launcher class, is used to launch processors in their own threads, facilitates a simple Log, and provides commandline running.
 */
public class Loader implements nl.xillio.plugins.ContenttoolsPlugin {
	private static final Manifest MANIFEST;

	static {
		Logger logger = LogManager.getLogger();
		try {
			String path = Loader.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
			MANIFEST = new JarFile(path).getManifest();
		} catch (URISyntaxException | IOException e) {
			throw new RuntimeException("Failed to find running jar file", e);
		}

		String shortVersion = Loader.class.getPackage().getImplementationVersion() == null ? "dev" : Loader.class.getPackage().getImplementationVersion();
		String date = MANIFEST.getMainAttributes().getValue("Created-On");
		try {
			Date parsedDate = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH).parse(date);
			date = DateFormat.getDateInstance().format(parsedDate);
		} catch (ParseException e) {
			logger.error("Failed to parse date from manifest", e);
		}
		LONG_VERSION = shortVersion + ", " + date;
		SHORT_VERSION = shortVersion;
		VERSION_DATE = date;
		LOGGER = logger;
	}

	/**
	 * The GUI's current official version.
	 */
	public static final String SHORT_VERSION;

	/**
	 * The release date of the official version.
	 */
	public static final String VERSION_DATE;

	/**
	 * Just the version number + date.
	 */
	public static final String LONG_VERSION;
	public static final String APP_TITLE = Loader.class.getPackage().getImplementationTitle();
	private static final Logger LOGGER;

	private static Xill xill;
	private static XillInitializer initializer;

	@Override
	public void start(final Stage primaryStage, final Xill xill) {
		try {
			checkJRE();
		}catch(IOException e) {
			LOGGER.error("JRE Check Failed", e);
			alert("Something went wrong while starting the application: " + e.getLocalizedMessage());
		}
		Loader.xill = xill;
		Thread.currentThread().setContextClassLoader(getClass().getClassLoader());

		// Start loading plugins
		initializer = new XillInitializer(new XillDocGen());
		initializer.start();

		try (InputStream image = this.getClass().getResourceAsStream("/icon.png")) {
			if (image != null) {
				primaryStage.getIcons().add(new Image(image));
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
		
		Parent root;
		try {
			Font.loadFont(this.getClass().getResourceAsStream("/fonts/Glober SemiBold.ttf"), 10);
			Font.loadFont(this.getClass().getResourceAsStream("/fonts/Glober xBold.ttf"), 10);
			Font.loadFont(this.getClass().getResourceAsStream("/fonts/UbuntuMono Regular.ttf"), 10);
			
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/contenttools.fxml"));
			root = loader.load();

			Scene scene = new Scene(root, 1024, 768);

			primaryStage.setScene(scene);

			// Set window to max size (native maximize is not available)
			Screen screen = Screen.getPrimary();
			Rectangle2D bounds = screen.getVisualBounds();

			primaryStage.setX(bounds.getMinX());
			primaryStage.setY(bounds.getMinY());
			primaryStage.setWidth(bounds.getWidth());
			primaryStage.setHeight(bounds.getHeight());

		} catch (IOException e) {
			System.err.println("Loader.initGUI(): Fatal error occurred during launch: " + e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}
		
		primaryStage.setTitle(APP_TITLE + " - " + LONG_VERSION);
		primaryStage.getScene().lookup("#apnRoot").setVisible(false);
		primaryStage.show();

		Platform.runLater(() -> primaryStage.getScene().lookup("#apnRoot").setVisible(true));
	}

	private void checkJRE() throws IOException {
		String expectedVersion = MANIFEST.getMainAttributes().getValue("Created-By");
		if(expectedVersion == null) {
			throw new IOException("No java version was found. This is not a build.");
		}
		String actualVersion = System.getProperty("java.version");

		if(!actualVersion.equals(expectedVersion)) {
			throw new IOException("The application was built with java version " + expectedVersion + " but is running with " + actualVersion + ".");
		}
	}

	private void alert(String message) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.initModality(Modality.APPLICATION_MODAL);
		alert.setTitle("Warning");
		alert.setContentText(message);
		alert.show();
	}

	/**
	 * @return the currently used initializer
	 */
	public static XillInitializer getInitializer() {
		return initializer;
	}

	@Override
	public Object serve() {
		// Nothing to serve
		return null;
	}

	/**
	 * Shortcut to load FXML
	 *
	 * @param resource
	 * @param controller
	 * @return The node represented in the FXML resource
	 * @throws IOException
	 */
	public static Node load(final URL resource, final Object controller) throws IOException {
		FXMLLoader loader = new FXMLLoader(resource);
		loader.setClassLoader(controller.getClass().getClassLoader());
		loader.setController(controller);
		return loader.load();
	}

	@Override
	public void load(final nl.xillio.plugins.ContenttoolsPlugin[] dependencies) {}

	/**
	 * @return The xill implementation this was initialized with
	 */
	public static Xill getXill() {
		return xill;
	}
}
