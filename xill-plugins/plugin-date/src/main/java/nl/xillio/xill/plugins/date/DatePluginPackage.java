package nl.xillio.xill.plugins.date;

import nl.xillio.xill.api.PluginPackage;

/**
 * This package includes all date constructs
 */
public class DatePluginPackage extends PluginPackage {

	@Override
	public void load(final PluginPackage[] dependencies) {
		add(new TimestampConstruct(), new NewConstruct(), new FormatConstruct(), new InfoConstruct(),
				new DiffConstruct(), new ChangeConstruct());

	}

	@Override
	public String getName() {
		return "Date";
	}
}
