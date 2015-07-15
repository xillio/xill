package nl.xillio.xill.plugins.date;

import nl.xillio.xill.api.PluginPackage;
import nl.xillio.xill.plugins.date.constructs.ChangeConstruct;
import nl.xillio.xill.plugins.date.constructs.DiffConstruct;
import nl.xillio.xill.plugins.date.constructs.FormatConstruct;
import nl.xillio.xill.plugins.date.constructs.FormatLocaleConstruct;
import nl.xillio.xill.plugins.date.constructs.InfoConstruct;
import nl.xillio.xill.plugins.date.constructs.ParseConstruct;
import nl.xillio.xill.plugins.date.constructs.NowConstruct;
import nl.xillio.xill.plugins.date.constructs.OfConstruct;

/**
 * This package includes all date constructs
 */
public class DatePluginPackage extends PluginPackage {

	@Override
	public void load(final PluginPackage[] dependencies) {
		add(new NowConstruct(), new ParseConstruct(), new FormatConstruct(), new InfoConstruct(),
				new DiffConstruct(), new ChangeConstruct(),new OfConstruct(),new FormatLocaleConstruct());

	}

	@Override
	public String getName() {
		return "Date";
	}
}
