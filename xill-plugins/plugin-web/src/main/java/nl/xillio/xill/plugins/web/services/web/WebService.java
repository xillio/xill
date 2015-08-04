package nl.xillio.xill.plugins.web.services.web;

import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.google.inject.ImplementedBy;

@ImplementedBy(WebServiceImpl.class)
public interface WebService {

	/**
	 * Tries to click a {@link WebElement}.
	 * 
	 * @param element
	 *        The {@link WebElement} we're trying to click on.
	 * @throws StaleElementReferenceException
	 *         Throws an exception when the element is stale.
	 */
	public void click(WebElement element) throws StaleElementReferenceException;

	/**
	 * Sets focus on an element on a given page.
	 * 
	 * @param page
	 *        The page as a {@link WebDriver}.
	 * @param element
	 *        The element as a {@link WebElement}.
	 */
	public void moveToElement(WebDriver page, WebElement element);

	/**
	 * Returns the tag name of a {@link WebElement}.
	 * 
	 * @param element
	 *        The element we want to know the tag name of.
	 * @return
	 *         The name of the tag of the element.
	 */
	public String getTagName(WebElement element);

	/**
	 * Returns a given attribute of a {@link WebElement}.
	 * 
	 * @param element
	 *        The element we want to know the attribute of.
	 * @param name
	 *        The name of the attribute we want to retrieve.
	 * @return
	 *         The attribute of the element.
	 */
	public String getAttribute(WebElement element, String name);

	/**
	 * Returns the text contained in a {@link WebElement}.
	 * 
	 * @param element
	 *        The element from which we want to extract text.
	 * @return
	 *         Returns the text in the element.
	 */
	public String getText(WebElement element);

}
