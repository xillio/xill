package nl.xillio.xill.plugins.xml.constructs;

import com.google.inject.Inject;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.plugins.xml.services.XsdService;
import org.apache.logging.log4j.Logger;

/**
 * Returns true if XML file is valid according to XSD specification
 * In case of validation errors, it will show each problem as warning message
 * 
 * @author Zbynek Hochmann
 */
public class XsdCheckConstruct extends Construct {
	@Inject
	private XsdService xsdService;

	@Override
	public ConstructProcessor prepareProcess(ConstructContext context) {
		return new ConstructProcessor(
				(xmlfile, xsdfile) -> process(xmlfile, xsdfile, xsdService, context.getRootLogger()),
				new Argument("xmlfile", ATOMIC),
				new Argument("xsdfile", ATOMIC)
		);
	}

	static MetaExpression process(MetaExpression xmlFileVar, MetaExpression xsdFileVar, XsdService service, Logger logger) {
		return fromValue(service.xsdCheck(xmlFileVar.getStringValue(), xsdFileVar.getStringValue(), logger));
	}

}
