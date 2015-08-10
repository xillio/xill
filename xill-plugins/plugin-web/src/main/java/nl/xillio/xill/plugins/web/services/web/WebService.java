package nl.xillio.xill.plugins.web.services.web;

import java.io.File;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Set;

import nl.xillio.xill.plugins.web.NodeVariable;
import nl.xillio.xill.plugins.web.Options;
import nl.xillio.xill.plugins.web.PhantomJSPool;
import nl.xillio.xill.plugins.web.WebVariable;

import org.openqa.selenium.Cookie;
import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.StaleElementReferenceException;

import com.google.inject.ImplementedBy;

/**
 * Provides an interface for a webService.
 */
@ImplementedBy(WebServiceImpl.class)
public interface WebService {

	/**
	 * Tries to click a {@link WebVariable}.
	 *
	 * @param var
	 *        The variable we want to click.
	 * @throws StaleElementReferenceException
	 *         Throws an exception when the element is stale.
	 */
	public void click(WebVariable var) throws StaleElementReferenceException;

	/**
	 * Sets focus on an element on a given page.
	 *
	 * @param var
	 *        The element we want to move to.
	 */
	public void moveToElement(WebVariable var);

	/**
	 * Returns the tag name of a {@link WebVariable}
	 *
	 * @param var
	 *        The variable we want the tag of.
	 * @return
	 *         The name of the tag of the variable.
	 */
	public String getTagName(WebVariable var);

	/**
	 * Returns a given attribute of a {@link WebVariable}.
	 *
	 * @param var
	 *        The variable we want to get an attribute from
	 * @param name
	 *        The name of the attribute we want to retrieve.
	 * @return
	 *         The attribute of the element.
	 */
	public String getAttribute(WebVariable var, String name);

	/**
	 * Returns the text contained in a {@link WebVariable}.
	 *
	 * @param var
	 *        The variable we want to get text from.
	 * @return
	 *         Returns the text in the element.
	 */
	public String getText(WebVariable var);

	/**
	 * Returns the title of a {@link WebVariable}.
	 *
	 * @param var
	 *        The variable we want to a title from.
	 * @return
	 *         The title.
	 */
	public String getTitle(WebVariable var);

	/**
	 * Finds all the elements in a node given a cssPath.
	 *
	 * @param var
	 *        The {@link WebVariable} we're searching.
	 * @param cssPath
	 *        The cssPath we're using.
	 * @return
	 *         Returns a list of elements: {@link WebVariable}.
	 * @throws InvalidSelectorException
	 */
	public List<WebVariable> findElementsWithCssPath(WebVariable var, String cssPath) throws InvalidSelectorException;

	/**
	 * Finds all the elements in a node given a cssPath.
	 *
	 * @param var
	 *        The {@link WebVariable} we're searching.
	 * @param xpath
	 *        The xpath we're using.
	 * @return
	 *         Returns a list of elements: {@link WebVariable}.
	 * @throws InvalidSelectorException
	 */
	public List<WebVariable> findElementsWithXpath(WebVariable var, String xpath) throws InvalidSelectorException;

	/**
	 * Returns the current URL of a {@link WebVariable}.
	 *
	 * @param var
	 *        the variable we want the url from.
	 * @return
	 *         A string which is the current URL.
	 */
	public String getCurrentUrl(WebVariable var);

	/**
	 * Clears a {@link WebVariable}.
	 *
	 * @param var
	 *        the variable we want to clear.
	 */
	public void clear(WebVariable var);

	/**
	 * Simulates sending a key to an {@link WebVariable}.
	 * May alter its value.
	 *
	 * @param var
	 *        The variable we want to send keys to.
	 * @param key
	 *        the key we want to send.
	 */
	public void sendKeys(WebVariable var, String key);

	/**
	 * Gets the cookies from a {@link WebVariable}.
	 * @param var 
	 * 					The webpage we're retrieving cookies from.
	 * @return
	 *         A set of Cookies.
	 */
	public Set<Cookie> getCookies(WebVariable var);

	/**
	 * Gets the name of a {@link Cookie}
	 *
	 * @param cookie
	 *        The cookie.
	 * @return
	 *         The name of the cookie.
	 */
	public String getName(Cookie cookie);

	/**
	 * Gets the domain of a {@link Cookie}
	 *
	 * @param cookie
	 *        The cookie.
	 * @return
	 *         The domain of the cookie.
	 */
	public String getDomain(Cookie cookie);

	/**
	 * Gets the path of a {@link Cookie}
	 *
	 * @param cookie
	 *        The cookie.
	 * @return
	 *         The path of the cookie.
	 */
	public String getPath(Cookie cookie);

	/**
	 * Gets the value of a {@link Cookie}.
	 *
	 * @param cookie
	 *        The cookie.
	 * @return
	 *         The value of the cookie.
	 */
	public String getValue(Cookie cookie);

	/**
	 * Deletes the cookie in the {@link WebVariable} with the given name.
	 *
	 * @param var
	 *        The variable we want to delete cookies on.
	 * @param name
	 *        The name of the cookie we're deleting.
	 */
	public void deleteCookieNamed(WebVariable var, String name);

	/**
	 * Deletes all cookies on a {@link WebVariable}.
	 *
	 * @param var
	 *        The variable we're deleting cookies on.
	 */
	public void deleteCookies(WebVariable var);

	/**
	 * Makes a screenshot on a {@link WebVariable} and returns it as file.
	 *
	 * @param var
	 *        The variable we're screenshotting.
	 * @return
	 *         A file containing the screenshot.
	 */
	public File getScreenshotAsFile(WebVariable var);

	/**
	 * Checks whether a {@link WebVariable} is selected.
	 *
	 * @param var
	 *        The variable we want to query.
	 * @return
	 *         Returns whether the variable is selected.
	 */
	public boolean isSelected(WebVariable var);

	/**
	 * Switches to a given frame.
	 *
	 * @param page
	 *        The page we want to switch to.
	 * @param element
	 *        The element we want to switch to.
	 */
	public void switchToFrame(WebVariable page, WebVariable element);

	/**
	 * Switches to a given frame.
	 *
	 * @param var
	 *        the page we want to switch to.
	 * @param element
	 *        The element we want to switch to.
	 *
	 */
	public void switchToFrame(WebVariable var, String element);

	/**
	 * Switches to a given frame.
	 *
	 * @param driver
	 *        The driver we're using.
	 * @param element
	 *        The frame as an integer.
	 */
	public void switchToFrame(WebVariable driver, int element);

	/**
	 * Places a Cookie on a webvariable
	 *
	 * @param var
	 *        The webVariable we want to place the cookie on
	 * @param cookie
	 *        The cookie
	 */
	public void addCookie(WebVariable var, Cookie cookie);

	/**
	 * Creates a node variable with a given page and element.
	 *
	 * @param page
	 *        The page of the node.
	 * @param element
	 *        The element of the node.
	 * @return
	 *         A new node.
	 */
	public NodeVariable createNodeVariable(WebVariable page, WebVariable element);

	/**
	 * Executes HTTP GET on a {@link WebVariable} and an URL.
	 *
	 * @param var
	 *        The webpage.
	 * @param url
	 *        The url.
	 * @throws ClassCastException 
	 * 						The implementations casts to a class. When that goes wrong we need to catch it.
	 * @throws MalformedURLException 
	 * 						When the url is malformed an exception can occur.
	 */
	public void httpGet(WebVariable var, String url) throws ClassCastException, MalformedURLException;

	/**
	 * Creates a page as a {@link WebVariable} given a {@link Options} as setting.
	 *
	 * @param options
	 *        The options.
	 * @return
	 *         The page
	 */
	public WebVariable createPage(Options options);

	/**
	 * Drags a page as a {@link WebVariable} from the pool.
	 * Creates it when it doesn't exist yet.
	 * @param pool 
	 * 				The pool we're extracting the page from.
	 * @param options
	 *        The options the page has.
	 * @return
	 *         The page.
	 */
	public WebVariable getPageFromPool(PhantomJSPool pool, Options options);

	/**
	 * Sets the drivers default options of a {@link WebVariable}.
	 *
	 * @param var
	 *        The variable we're setting the options for.
	 * @param timeOut
	 *        A timeout in milliseconds.
	 */
	public void setDriverOptions(WebVariable var, int timeOut);

	/**
	 * Closes a {@link WebVariable} and all pages associated.
	 *
	 * @param var
	 *        The variable we want to quit.
	 */
	public void quit(WebVariable var);

}
