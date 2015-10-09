package nl.xillio.xill.plugins.date.data;


import nl.xillio.xill.api.data.MetadataExpression;

import java.time.ZonedDateTime;

/**
 * <p>
 * This class represents a date MetadataExpression
 * </p>
 *
 * @author Thomas Biesaart
 * @since 7-8-2015
 */
public class Date implements nl.xillio.xill.api.data.Date {
	private final ZonedDateTime date;

	public Date(ZonedDateTime date) {
		if (date == null) {
			throw new NullPointerException();
		}
		this.date = date;
	}

	/**
	 * Returns a ZonedDateTime
	 *
	 * @return the date
	 */
	public ZonedDateTime getZoned() {
		return date;
	}

	@Override
	public String toString() {
		return getZoned().toString();
	}
}
