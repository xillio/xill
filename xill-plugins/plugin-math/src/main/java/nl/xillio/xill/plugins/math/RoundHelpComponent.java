package nl.xillio.xill.plugins.math;

import java.io.InputStream;

import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.construct.HelpComponent;

/**
 * The help component which contains the help XML files for the round function
 * @author Ivor
 *
 */
public class RoundHelpComponent implements HelpComponent {

	@Override
	public String getName() {
		return "roundhelpfile";
	}


	@Override
	public ConstructProcessor prepareProcess(ConstructContext context) {
		return null;
	}

	@Override
	public InputStream openDocumentationStream() {
		return getClass().getResourceAsStream("/helpfiles/round.xml");
	}

}
