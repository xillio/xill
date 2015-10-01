package nl.xillio.xill.plugins.document.constructs;

import com.google.inject.Inject;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
public class RemoveConstruct extends Construct {


	@Override
	public ConstructProcessor prepareProcess(ConstructContext context) {
		return new ConstructProcessor(
				(documentid, versionid, section) -> process(documentid, versionid, section),
				new Argument("documentid", ATOMIC),
				new Argument("versionid",fromValue("current"), ATOMIC),
				new Argument("section", fromValue("target"), ATOMIC)
		);
	}

	static MetaExpression process(MetaExpression documentid, MetaExpression versionid, MetaExpression section) {
		//get string values of arguments
		String docid = documentid.getStringValue();
		String versid = versionid.getStringValue();
		String sect = section.getStringValue();
		
		//connect to UDM service
		
		

		
		return NULL;
	}
}