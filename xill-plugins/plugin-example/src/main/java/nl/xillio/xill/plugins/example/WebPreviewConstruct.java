package nl.xillio.xill.plugins.example;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import nl.xillio.xill.api.components.AtomicExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.preview.PreviewComponent;

/**
 * This construct shows how to make a custom preview
 */
public class WebPreviewConstruct implements Construct {

    @Override
    public String getName() {
	return "webPreview";
    }

    @Override
    public ConstructProcessor prepareProcess(final ConstructContext context) {
	//To show a custom preview of the variable you need to implement the PreviewComponent interface
	
	return new ConstructProcessor((url) -> new WebExpression(url.getStringValue()), new Argument("url"));
    }

    
    /**
     * This class is an implementation of the PreviewComponent
     */
    private class WebExpression extends AtomicExpression implements PreviewComponent {

	public WebExpression(final String url) {
	    super(url);
	}
	
	@Override
	public Node getPreview() {
	    //Because we can't display a whole webpage in the tree structure we can make a button that shows it when  it is clicked
	    
	    Button button = new Button("View Page");
	    button.setOnAction(action -> {
		WebView webview = new WebView();
		webview.getEngine().load(getStringValue());
		Stage stage = new Stage();
		stage.setScene(new Scene(webview));
		stage.showAndWait();
		stage.close();
	    });

	    return button;
	}
    }
}
