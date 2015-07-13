package nl.xillio.xill.plugins.date;

import java.text.DateFormat;
import java.util.Date;

/**
 *
 * DateInfo contains a Date and DateFormat.
 *
 * @author Sander
 *
 */
public class DateInfo {

	private Date dateVar;
	private DateFormat formatVar;

	/**
	 * @param date
	 * @param format
	 */
	public DateInfo(final Date date, final DateFormat format) {
		dateVar = date;
		formatVar = format;
	}

	/**
	 *
	 */
	public DateInfo() {
	}

	/**
	 * @return the date.
	 */
	public Date GetDate() {
		return dateVar;
	}

	/**
	 * @param date
	 *            Sets the date.
	 *
	 */
	public void SetDate(final Date date) {
		dateVar = date;
	}

	/**
	 * @return the format
	 */
	public DateFormat GetFormat() {
		return formatVar;
	}

	/**
	 * @param format
	 *            sets the format.
	 */
	public void SetFormat(final DateFormat format) {
		formatVar = format;
	}

}
