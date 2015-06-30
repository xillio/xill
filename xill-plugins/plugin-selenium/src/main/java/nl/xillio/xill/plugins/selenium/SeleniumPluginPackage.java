package nl.xillio.xill.plugins.selenium;

import nl.xillio.xill.api.PluginPackage;

/**
 * This package includes all Selenium constructs
 */
public class SeleniumPluginPackage extends PluginPackage {

	@Override
	public void load(PluginPackage[] dependencies) {
		add(new ScreenshotConstruct());
		add(new LoadPageConstruct());
		add(new XPathConstruct());
		add(new PageInfoConstruct());
		add(new InputConstruct());
		add(new FocusConstruct());
		add(new StringToPageConstruct());
		add(new SelectedConstruct());
		add(new SelectConstruct());
		add(new CSSPathConstruct());
		add(new SwitchFrameConstruct());
		add(new SetCookieConstruct());
		add(new GetTextConstruct());
		add(new ClickConstruct());
		add(new RemoveCookieConstruct());
		
		//add(new ());
	}

	@Override
	public String getName() {
		return "Selenium";
	}

}
