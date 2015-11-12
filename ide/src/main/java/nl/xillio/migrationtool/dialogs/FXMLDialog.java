package nl.xillio.migrationtool.dialogs;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * A general dialog.
 */
public class FXMLDialog extends Stage {

	/**
	 * Default constructor.
	 *
	 * @param url
	 *        the path to the fxml resource to load
	 */
	public FXMLDialog(final String url) {
		loadFXML(getClass().getResource(url));
		initModality(Modality.APPLICATION_MODAL);

        try (InputStream image = this.getClass().getResourceAsStream("/icon.png")) {
            if (image != null) {
                this.getIcons().add(new Image(image));
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
	}

	private void loadFXML(final URL resource) {
		FXMLLoader loader = new FXMLLoader(resource);
		loader.setClassLoader(getClass().getClassLoader());
		loader.setController(this);

		try {
			setScene(new Scene(loader.load()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
