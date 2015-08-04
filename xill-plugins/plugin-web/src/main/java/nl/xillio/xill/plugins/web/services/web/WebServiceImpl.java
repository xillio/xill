package nl.xillio.xill.plugins.web.services.web;

import java.util.ArrayList;
import java.util.List;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.ExpressionBuilderHelper;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.web.NodeVariable;

import org.openqa.selenium.By;
import org.openqa.selenium.InvalidElementStateException;
import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

/**
 *The implementation of the {@link WebService} interface.
 *
 */
public class WebServiceImpl implements WebService {

	@Override
	public void click(WebElement element) throws StaleElementReferenceException {
		element.click();
	}

	@Override
	public void moveToElement(WebDriver page, WebElement element) {
		new Actions(page).moveToElement(element).perform();
	}

	@Override
	public String getTagName(WebElement element) {
		return element.getTagName();
	}

	@Override
	public String getAttribute(WebElement element, String name) {
		return element.getAttribute(name);
	}

	@Override
	public String getText(WebElement element) {
		return element.getText();
	}
}
