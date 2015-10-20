package nl.xillio.xill.plugins.xml;

import com.google.inject.Binder;
import nl.xillio.plugins.XillPlugin;
import nl.xillio.xill.api.data.XmlNodeFactory;
import nl.xillio.xill.plugins.xml.services.NodeServiceImpl;

/**
 * This package includes all Xml constructs
 */
public class XMLXillPlugin extends XillPlugin {
	@Override
	public void configure(Binder binder) {
		super.configure(binder);
		binder.bind(XmlNodeFactory.class).to(NodeServiceImpl.class);
	}
}
