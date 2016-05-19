package nl.xillio.xill.api.data;


import java.time.ZonedDateTime;

/**
 * This interface represents a date object in the Xill language.
 *
 * @author Thomas Biesaart
 * @author Sander Visser
 * @author Geert Konijnendijk
 * @since 3.0.0
 */
public interface Date extends MetadataExpression {

	/**
	 * Returns a ZonedDateTime that represents the date stored in this object.
	 *
	 * @return the date
	 */
	ZonedDateTime getZoned();
}
