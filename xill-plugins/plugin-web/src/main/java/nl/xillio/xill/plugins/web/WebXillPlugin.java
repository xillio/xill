package nl.xillio.xill.plugins.web;

import nl.xillio.plugins.XillPlugin;
import nl.xillio.xill.plugins.web.constructs.CSSPathConstruct;
import nl.xillio.xill.plugins.web.constructs.ClickConstruct;
import nl.xillio.xill.plugins.web.constructs.FocusConstruct;
import nl.xillio.xill.plugins.web.constructs.GetTextConstruct;
import nl.xillio.xill.plugins.web.constructs.InputConstruct;
import nl.xillio.xill.plugins.web.constructs.LoadPageConstruct;
import nl.xillio.xill.plugins.web.constructs.PageInfoConstruct;
import nl.xillio.xill.plugins.web.constructs.RemoveCookieConstruct;
import nl.xillio.xill.plugins.web.constructs.ScreenshotConstruct;
import nl.xillio.xill.plugins.web.constructs.SelectConstruct;
import nl.xillio.xill.plugins.web.constructs.SelectedConstruct;
import nl.xillio.xill.plugins.web.constructs.SetCookieConstruct;
import nl.xillio.xill.plugins.web.constructs.StringToPageConstruct;
import nl.xillio.xill.plugins.web.constructs.SwitchFrameConstruct;
import nl.xillio.xill.plugins.web.constructs.XPathConstruct;

/**
 * This package includes all Selenium constructs
 */
public class WebXillPlugin extends XillPlugin {

	@Override
	public void load(final XillPlugin[] dependencies) {
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
}
