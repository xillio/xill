package nl.xillio.xill.plugins.web.services.web;

import java.util.List;

import org.openqa.selenium.InvalidElementStateException;
import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.google.inject.ImplementedBy;

@ImplementedBy(WebServiceImpl.class)
public interface WebService {
	
	/**
	 * Tries to click a {@link WebElement}.
	 * @param element
	 * 					The {@link WebElement} we're trying to click on.
	 * @throws StaleElementReferenceException
	 * 						Throws an exception when the element is stale.
	 */
	public void click(WebElement element) throws StaleElementReferenceException;
	
	/**
	 * Sets focus on an element on a given page.
	 * @param page
	 * 					The page as a {@link WebDriver}.
	 * @param element
	 * 				  The element as a {@link WebElement}.
	 */
	public void moveToElement(WebDriver page, WebElement element);
			


}
