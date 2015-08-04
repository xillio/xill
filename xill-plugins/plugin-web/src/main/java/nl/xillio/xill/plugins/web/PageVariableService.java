package nl.xillio.xill.plugins.web;

import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;

import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;

import nl.xillio.xill.api.components.AtomicExpression;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.ExpressionBuilderHelper;

import com.google.inject.Singleton;

@Singleton
public class PageVariableService {
	String name = "Selenium:page";
	
  /**
   * Creates new {@link PageVariable}
   *
   * @param item from PhantomJS pool
   *
   * @return created PAGE variable
   */
  public MetaExpression create(final PhantomJSPool.Entity item) {
      MetaExpression var = new AtomicExpression(item.getDriver().getCurrentUrl());
      var.storeMeta(name);
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
  public PhantomJSPool.Entity get(final MetaExpression var) {
      return var.getMeta(PhantomJSPool.Entity.class);
  }
  
  /**
   * Extracts driver/page from {@link PageVariable}
   *
   * @param var input variable (should be of a PAGE type)
   *
   * @return driver (page)
   */
  public WebDriver getDriver(final MetaExpression var) {
      return var.getMeta(PhantomJSPool.Entity.class).getDriver();
  }
  
  /**
   * Do the test if input {@link MetaExpression} if it's of PAGE type
   *
   * @param var MetaExpression (any variable)
   *
   * @return true if it's of PAGE type
   */
  public boolean checkType(final MetaExpression var) {
      String metaName = var.getMeta(String.class);
      return metaName != null && metaName.equals(name) && var.getMeta(PhantomJSPool.Entity.class) != null;
  }
  
  /**
   * Creates an associated list variable that contains all information about one cookie
   *
   * @param cookie Selenium's cookie class
   *
   * @return created cookie variable
   */
  public MetaExpression makeCookie(final Cookie cookie) {
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
