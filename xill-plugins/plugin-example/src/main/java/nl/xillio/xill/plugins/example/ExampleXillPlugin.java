package nl.xillio.xill.plugins.example;

import nl.xillio.plugins.XillPlugin;
import nl.xillio.xill.plugins.example.constructs.CopyConstruct;
import nl.xillio.xill.plugins.example.constructs.LifeConstuct;
import nl.xillio.xill.plugins.example.constructs.WebPreviewConstruct;

/**
 * This package includes all example constructs
 */
public class ExampleXillPlugin extends XillPlugin {

	@Override
	public void load(final XillPlugin[] dependencies) {
		add(new LifeConstuct(), new CopyConstruct(), new WebPreviewConstruct());
	}
}
