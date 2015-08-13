package nl.xillio.xill.plugins.web.data;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.web.services.web.WebService;

/**
 * This is a factory which creates a {@link CookieVariable} and stores it using a {@link WebService}.
 *
 * @author Ivor
 *
 */
public class CookieFactory {

	/**
	 * Sets a cookie and stores in the {@link WebService}.
	 *
	 * @param driver
	 *        The page we want to set the cookie on.
	 * @param cookie
	 *        The settings for the cookie we want to add.
	 * @param webService
	 *        The webService we're storing the cookie in.
	 */
	public void setCookie(final WebVariable driver, final Map<String, MetaExpression> cookieValues, final WebService webService) {

		CookieVariable cookie = new CookieVariable();
		setName(cookieValues, cookie);
		setDomain(cookieValues, cookie);
		setPath(cookieValues, cookie);
		setValue(cookieValues, cookie);
		setExpirationDate(cookieValues, cookie);

		addCookie(driver, cookie, webService);
	}

	/**
	 * Adds a cookie to a webPage.
	 * @param page
	 * 					The page we want to add the cookie to.
	 * @param cookie
	 * 					The cookie we want to add.
	 * @param webService
	 * 					The service we're using to add the cookie.
	 */
	private void addCookie(final WebVariable page, final CookieVariable cookie, final WebService webService) {
		try {
			webService.addCookie(page, cookie);
		} catch (Exception e) {
			throw new RobotRuntimeException("Failed to add cookie.", e);
		}
	}

	private void setName(final Map<String, MetaExpression> cookieValues, final CookieVariable cookie) {
		MetaExpression name = cookieValues.get("name");
		if (!isNoNullString(name)) {
			throw new RobotRuntimeException("Invalid cookie. Attribute 'name' is empty.");
		}
		else {
			cookie.setName(name.getStringValue());
		}
	}

	private void setDomain(final Map<String, MetaExpression> cookieValues, final CookieVariable cookie) {
		MetaExpression domain = cookieValues.get("domain");
		if (isNoNullString(domain)) {
			cookie.setDomain(domain.getStringValue());
		}
	}

	private void setPath(final Map<String, MetaExpression> cookieValues, final CookieVariable cookie) {
		MetaExpression path = cookieValues.get("path");
		if (isNoNullString(path)) {
			cookie.setPath(path.getStringValue());
		}
	}

	private void setValue(final Map<String, MetaExpression> cookieValues, final CookieVariable cookie) {
		MetaExpression value = cookieValues.get("value");
		if (isNoNullString(value)) {
			cookie.setValue(value.getStringValue());
		}
	}

	private void setExpirationDate(final Map<String, MetaExpression> cookieValues, final CookieVariable cookie) {
		MetaExpression expirationDate = cookieValues.get("expires");
		if (isNoNullString(expirationDate)) {
			try {
				Date cookieExpiresDate = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss").parse(expirationDate.getStringValue());
				cookie.setExpireDate(cookieExpiresDate);
			} catch (Exception e) {
				throw new RobotRuntimeException("Invalid cookie. Atribute 'expires' does not have the format yyyy-MM-ddThh:mm:ss", e);
			}
		}
	}

	private boolean isNoNullString(final MetaExpression meta) {
		return meta != null && !"null".equals(meta.getStringValue());
	}
}
