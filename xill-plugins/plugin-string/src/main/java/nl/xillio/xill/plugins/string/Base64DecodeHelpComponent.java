package nl.xillio.xill.plugins.string;

import java.io.InputStream;

import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.construct.HelpComponent;

public class Base64DecodeHelpComponent implements HelpComponent {

	@Override
	public String getName() {
		return "base64decodehelpfile";
	}


	@Override
	public ConstructProcessor prepareProcess(ConstructContext context) {
		return null;
	}

	@Override
	public InputStream openDocumentationStream() {
		// TODO Auto-generated method stub
		return getClass().getResourceAsStream("/helpfiles/base64decode.xml");
	}

}
