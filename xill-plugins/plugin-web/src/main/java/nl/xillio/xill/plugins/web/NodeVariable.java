package nl.xillio.xill.plugins.web;

import nl.xillio.xill.api.components.AtomicExpression;
import nl.xillio.xill.api.components.MetaExpression;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * This class represents NODE "pseudo" variable, it encapsulates basic operations.
 *
 * @author Zbynek Hochmann
 */
public class NodeVariable {
    private final WebDriver driver;
    private final WebElement element;

    private NodeVariable(final WebDriver driver, final WebElement element) {
        this.driver = driver;
        this.element = element;
    }

    private static String name = "Selenium:node";

    /**
     * Creates new {@link NodeVariable}
     *
     * @param driver  PhantomJS driver (page)
     * @param element web element on the page (represented by driver)
     *
     * @return created variable
     */
    public static MetaExpression create(final WebDriver driver, final WebElement element) {
        MetaExpression var = new AtomicExpression(element.getAttribute("outerHTML"));
        var.storeMeta(name);
        var.storeMeta(new NodeVariable(driver, element));
        return var;
    }

    /**
     * Extracts web element from {@link NodeVariable}
     *
     * @param var input variable (should be of a NODE type)
     *
     * @return web element
     */
    public static WebElement get(final MetaExpression var) {
        return var.getMeta(NodeVariable.class).getElement();
    }

   

		/**
     * Do the test if input {@link MetaExpression} if it's of NODE type
     *
     * @param var MetaExpression (any variable)
     *
     * @return true if it's of NODE type
     */
    public static boolean checkType(final MetaExpression var) {
        String metaName = var.getMeta(String.class);
        return metaName != null && metaName.equals(NodeVariable.name) && var.getMeta(NodeVariable.class) != null;
    }

    /**
     * Extracts driver/page from {@link NodeVariable}
     *
     * @param var input variable (should be of a NODE type)
     *
     * @return driver (page)
     */
    public static WebDriver getDriver(final MetaExpression var) {
        return var.getMeta(NodeVariable.class).getDriver();
    }
    
    public WebDriver getDriver() {
			return driver;
		}

		public WebElement getElement() {
			return element;
		}
}
