package nl.xillio.xill.plugins.web;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.web.services.web.WebService;

/**
 * This is a factory which creates a {@link CookieVariable} and stores it using a {@link WebService}.
 * @author Ivor
 *
 */
public class CookieFactory {

	/**
	 * Sets a cookie and stores in the {@link WebService}.
	 * @param driver
	 * 						The page we want to set the cookie on.
	 * @param cookie
	 * 						The settings for the cookie we want to add.
	 * @param webService
	 * 						The webService we're storing the cookie in.
	 */
	public void setCookie(final WebVariable driver, final Map<String, MetaExpression> cookie, final WebService webService) {
		String cookieName = cookie.get("name").getStringValue();
		String cookieDomain = cookie.get("domain").getStringValue();
		String cookiePath = cookie.get("path").getStringValue();
		String cookieValue = cookie.get("value").getStringValue();
		String cookieExpires = cookie.get("expires").getStringValue();
		CookieVariable cookieVariable = new CookieVariable();

		if (cookieName.equals("null")) {
			throw new RobotRuntimeException("Invalid cookie. Attribute 'name' is empty.");
		}
		else {
			cookieVariable.setName(cookieName);
		}

		if (cookieValue.equals("null")) {
			cookieVariable.setValue("");
		}
		else {
			cookieVariable.setValue(cookieValue);
		}
		if (cookieDomain.equals("null")) {
			cookieVariable.setDomain(null);
		}
		else {
			cookieVariable.setDomain(cookieDomain);
		}
		if (cookiePath.equals("null")) {
			cookieVariable.setPath(null);
		}
		else {
			cookieVariable.setPath(cookiePath);
		}

		if (cookieExpires.equals("null")) {
			cookieVariable.setExpireDate(null);
		} else {
			try {
				Date cookieExpiresDate = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss").parse(cookieExpires);
				cookieVariable.setExpireDate(cookieExpiresDate);
			} catch (Exception e) {
				throw new RobotRuntimeException("Invalid cookie. Atribute 'expires' does not have the format yyyy-MM-ddThh:mm:ss");
			}
		}

		try {
			webService.addCookie(driver, cookieVariable);
		} catch (Exception e) {
			throw new RobotRuntimeException("Invalid cookie", e);
		}
	}

}
