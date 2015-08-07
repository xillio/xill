package nl.xillio.xill.plugins.date.data;

import nl.xillio.xill.api.components.MetadataExpression;

import java.time.ZonedDateTime;

/**
 * <p>
 * This class represents a date MetadataExpression
 * </p>
 *
 * @author Thomas Biesaart
 * @since 7-8-2015
 */
public class Date implements MetadataExpression {
	private final ZonedDateTime date;

	public Date(ZonedDateTime date) {

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
}
