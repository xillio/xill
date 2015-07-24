package nl.xillio.xill.plugins.date;

import nl.xillio.plugins.XillPlugin;
import nl.xillio.xill.plugins.date.constructs.ChangeConstruct;
import nl.xillio.xill.plugins.date.constructs.DiffConstruct;
import nl.xillio.xill.plugins.date.constructs.FormatConstruct;
import nl.xillio.xill.plugins.date.constructs.InfoConstruct;
import nl.xillio.xill.plugins.date.constructs.LocalizedFormatConstruct;
import nl.xillio.xill.plugins.date.constructs.NowConstruct;
import nl.xillio.xill.plugins.date.constructs.OfConstruct;
import nl.xillio.xill.plugins.date.constructs.ParseConstruct;

/**
 * This package includes all date constructs
 */
public class DateXillPlugin extends XillPlugin {

	@Override
	public void load(final XillPlugin[] dependencies) {
		add(new NowConstruct(), new ParseConstruct(), new FormatConstruct(), new InfoConstruct(),
			new DiffConstruct(), new ChangeConstruct(), new OfConstruct(), new LocalizedFormatConstruct());
	}
}
