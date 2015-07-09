package nl.xillio.xill.plugins.math;

import java.io.InputStream;

import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.construct.HelpComponent;

/**
 * The helpcomponent which contains the help XML files for the random function
 * @author Ivor
 *
 */
public class RandomHelpComponent implements HelpComponent {

	@Override
	public String getName() {
		return "randomhelpfile";
	}


	@Override
	public ConstructProcessor prepareProcess(ConstructContext context) {
		return null;
	}

	@Override
	public InputStream openDocumentationStream() {
		return getClass().getResourceAsStream("/helpfiles/random.xml");
	}

}
