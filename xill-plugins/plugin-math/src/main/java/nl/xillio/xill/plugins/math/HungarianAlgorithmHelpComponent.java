package nl.xillio.xill.plugins.math;

import java.io.InputStream;

import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.construct.HelpComponent;

/**
 * The helpcomponent which contains the xml helpfile for the HungarianAlgorithm
 * @author Ivor
 *
 */
public class HungarianAlgorithmHelpComponent implements HelpComponent {

	@Override
	public String getName() {
		return "hungarianalgorithmhelpfile";
	}


	@Override
	public ConstructProcessor prepareProcess(ConstructContext context) {
		return null;
	}

	@Override
	public InputStream openDocumentationStream() {
		return getClass().getResourceAsStream("/helpfiles/hungarianalgorithm.xml");
	}

}
