package nl.xillio.xill.plugins.system;

import nl.xillio.plugins.XillPlugin;
import nl.xillio.xill.plugins.system.constructs.ExecConstruct;
import nl.xillio.xill.plugins.system.constructs.InfoConstruct;
import nl.xillio.xill.plugins.system.constructs.ParseJSONConstruct;
import nl.xillio.xill.plugins.system.constructs.PrintConstruct;
import nl.xillio.xill.plugins.system.constructs.PropertiesConstruct;
import nl.xillio.xill.plugins.system.constructs.ToJSONConstruct;
import nl.xillio.xill.plugins.system.constructs.TypeOfConstruct;
import nl.xillio.xill.plugins.system.constructs.VersionConstruct;
import nl.xillio.xill.plugins.system.constructs.WaitConstruct;

/**
 * This package includes all system constructs
 */
public class SystemXillPlugin extends XillPlugin {

	@Override
	public void loadConstructs() {
		add(new ExecConstruct(),
			new InfoConstruct(),
			new ParseJSONConstruct(),
			new PrintConstruct(),
			new PropertiesConstruct(),
			new ToJSONConstruct(),
			new TypeOfConstruct(),
			new VersionConstruct(),
			new WaitConstruct());
	}
}
