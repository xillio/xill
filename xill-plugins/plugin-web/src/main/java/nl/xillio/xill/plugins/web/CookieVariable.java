package nl.xillio.xill.plugins.web;

import java.util.Date;

public class CookieVariable {

	private String name;
	private String domain;
	private String path;
	private String value;
	private Date expireDate;

	public String getName() {
		return name;
	}

	public String getDomain() {
		return domain;
	}

	public String getPath() {
		return path;
	}

	public String getValue() {
		return value;
	}

	public Date getExpireDate() {
		return expireDate;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setDomain(final String name) {
		domain = name;
	}

	public void setPath(final String name) {
		path = name;
	}

	public void setValue(final String name) {
		value = name;
	}

	public void setExpireDate(final Date date) {
		expireDate = date;
	}
}
