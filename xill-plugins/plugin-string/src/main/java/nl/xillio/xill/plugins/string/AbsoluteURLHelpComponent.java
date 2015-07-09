package nl.xillio.xill.plugins.string;

import java.io.InputStream;

import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.construct.HelpComponent;

public class AbsoluteURLHelpComponent implements HelpComponent {

	@Override
	public String getName() {
		return "absoluteurlhelpfile";
	}


	@Override
	public ConstructProcessor prepareProcess(ConstructContext context) {
		return null;
	}

	@Override
	public InputStream openDocumentationStream() {
		// TODO Auto-generated method stub
		return getClass().getResourceAsStream("/helpfiles/absoluteurl.xml");
	}

}
