package nl.xillio.xill.plugins.web;

import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;

import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import nl.xillio.xill.api.components.AtomicExpression;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ExpressionBuilderHelper;

public abstract class PhantomJSConstruct extends Construct {
	
	 /**
   * Creates new {@link NodeVariable}
   *
   * @param driver  PhantomJS driver (page)
   * @param element web element on the page (represented by driver)
   *
   * @return created variable
   */
  protected static MetaExpression createNode(final WebDriver driver, final WebElement element) {
      MetaExpression var = new AtomicExpression(element.getAttribute("outerHTML"));
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
  protected static WebElement getNode(final MetaExpression var) {
      return var.getMeta(NodeVariable.class).getElement();
  }
  
	/**
   * Do the test if input {@link MetaExpression} if it's of NODE type
   *
   * @param var MetaExpression (any variable)
   *
   * @return true if it's of NODE type
   */
  protected static boolean checkNodeType(final MetaExpression var) {
      return var.getMeta(NodeVariable.class) != null;
  }
  
  /**
   * Extracts driver/page from {@link NodeVariable}
   *
   * @param var input variable (should be of a NODE type)
   *
   * @return driver (page)
   */
  protected static WebDriver getNodeDriver(final MetaExpression var) {
      return var.getMeta(NodeVariable.class).getDriver();
  }
  
  /**
   * Creates new {@link PageVariable}
   *
   * @param item from PhantomJS pool
   *
   * @return created PAGE variable
   */
  protected static MetaExpression createPage(final PhantomJSPool.Entity item) {
      MetaExpression var = new AtomicExpression(item.getDriver().getCurrentUrl());
      var.storeMeta(item);
      return var;
  }
  
  /**
   * Extracts PhantomJS pool item from {@link PageVariable}
   *
   * @param var input variable (should be of a PAGE type)
   *
   * @return PhantomJS pool item
   */
  private PhantomJSPool.Entity getPage(final MetaExpression var) {
      return var.getMeta(PhantomJSPool.Entity.class);
  }
  
  /**
   * Extracts driver/page from {@link PageVariable}
   *
   * @param var input variable (should be of a PAGE type)
   *
   * @return driver (page)
   */
  protected static WebDriver getPageDriver(final MetaExpression var) {
      return var.getMeta(PhantomJSPool.Entity.class).getDriver();
  }
  
  /**
   * Do the test if input {@link MetaExpression} if it's of PAGE type
   *
   * @param var MetaExpression (any variable)
   *
   * @return true if it's of PAGE type
   */
  protected static boolean checkPageType(final MetaExpression var) {
      return var.getMeta(PhantomJSPool.Entity.class) != null;
  }
  
  /**
   * Creates an associated list variable that contains all information about one cookie
   *
   * @param cookie Selenium's cookie class
   *
   * @return created cookie variable
   */
  protected static MetaExpression makeCookie(final Cookie cookie) {
      LinkedHashMap<String, MetaExpression> map = new LinkedHashMap<String, MetaExpression>();
      map.put("name", ExpressionBuilderHelper.fromValue(cookie.getName()));
      map.put("domain", ExpressionBuilderHelper.fromValue(cookie.getDomain()));
      map.put("path", ExpressionBuilderHelper.fromValue(cookie.getPath()));
      map.put("value", ExpressionBuilderHelper.fromValue(cookie.getValue()));

      if (cookie.getExpiry() != null) {
          map.put("expires", ExpressionBuilderHelper.fromValue(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S").format(cookie.getExpiry())));
      }

      return ExpressionBuilderHelper.fromValue(map);
  }
}
