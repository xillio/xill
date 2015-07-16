package nl.xillio.xill.plugins.selenium;

import nl.xillio.xill.api.PluginPackage;
import nl.xillio.xill.plugins.selenium.constructs.CSSPathConstruct;
import nl.xillio.xill.plugins.selenium.constructs.ClickConstruct;
import nl.xillio.xill.plugins.selenium.constructs.FocusConstruct;
import nl.xillio.xill.plugins.selenium.constructs.GetTextConstruct;
import nl.xillio.xill.plugins.selenium.constructs.InputConstruct;
import nl.xillio.xill.plugins.selenium.constructs.LoadPageConstruct;
import nl.xillio.xill.plugins.selenium.constructs.PageInfoConstruct;
import nl.xillio.xill.plugins.selenium.constructs.RemoveCookieConstruct;
import nl.xillio.xill.plugins.selenium.constructs.ScreenshotConstruct;
import nl.xillio.xill.plugins.selenium.constructs.SelectConstruct;
import nl.xillio.xill.plugins.selenium.constructs.SelectedConstruct;
import nl.xillio.xill.plugins.selenium.constructs.SetCookieConstruct;
import nl.xillio.xill.plugins.selenium.constructs.StringToPageConstruct;
import nl.xillio.xill.plugins.selenium.constructs.SwitchFrameConstruct;
import nl.xillio.xill.plugins.selenium.constructs.XPathConstruct;

/**
 * This package includes all Selenium constructs
 */
public class SeleniumPluginPackage extends PluginPackage {

	@Override
	public void load(final PluginPackage[] dependencies) {
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
	}

	@Override
	public String getName() {
		return "Selenium";
	}

}
