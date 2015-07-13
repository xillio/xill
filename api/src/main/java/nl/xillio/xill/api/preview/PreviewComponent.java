package nl.xillio.xill.api.preview;

import javafx.scene.Node;

/**
 * This interface represents an object that can provide a JavaFX preview
 */
public interface PreviewComponent  {
	/**
	 * @return the preview
	 */
	public Node getPreview();
}
