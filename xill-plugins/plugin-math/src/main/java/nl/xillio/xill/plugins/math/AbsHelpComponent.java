package nl.xillio.xill.plugins.math;

import java.io.InputStream;

import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.construct.HelpComponent;

/**
 * The help component which contains the XML helpfile for abs
 * @author Ivor
 *
 */
public class AbsHelpComponent implements HelpComponent {

	@Override
	public String getName() {
		return "abshelpfile";
	}


	@Override
	public ConstructProcessor prepareProcess(ConstructContext context) {
		return null;
	}

	@Override
	public InputStream openDocumentationStream() {
		return getClass().getResourceAsStream("/helpfiles/abs.xml");
	}

}
