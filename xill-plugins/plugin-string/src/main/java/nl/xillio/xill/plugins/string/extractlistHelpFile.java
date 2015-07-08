package nl.xillio.xill.plugins.string;

import java.io.InputStream;

import nl.xillio.xill.api.components.ExpressionBuilder;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.construct.HelpComponent;

public class extractlistHelpFile implements HelpComponent {

	@Override
	public String getName() {
		return "extractlist";
	}


	@Override
	public ConstructProcessor prepareProcess(ConstructContext context) {
		return null;
	}

	@Override
	public InputStream openDocumentationStream() {
		// TODO Auto-generated method stub
		return getClass().getResourceAsStream("/helpfiles/extractlist.xml");
	}

}
