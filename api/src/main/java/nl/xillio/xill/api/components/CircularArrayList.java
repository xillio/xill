package nl.xillio.xill.api.components;

import java.util.ArrayList;

/**
 * This class represents an extention on ArrayList that allows cirular references in maps and lists.
 *
 * @param <T>
 *        the type of element in the list
 */
public class CircularArrayList<T> extends ArrayList<T> {
	private static final long serialVersionUID = 2397377372119909815L;
	private boolean isParsingString;

	@Override
	public String toString() {
		if (isParsingString) {
			return "(this.Collection)";
		}

		isParsingString = true;
		String result = super.toString();
		isParsingString = false;
		return result;
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}
}
