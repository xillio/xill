package nl.xillio.xill.api.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * <p>
 * This class represents a written list in a script e.g. [1,2,3,4].
 * </p>
 * Values:
 * <ul>
 * <li><b>{@link String}: </b> the JSON representation</li>
 * <li><b>{@link Boolean}: </b> false if the list is null else true (even if empty)</li>
 * <li><b>{@link Number}: </b> the length of the list</li>
 * </ul>
 */

public class ListExpression extends MetaExpression {

	private final List<? extends MetaExpression> value;

	/**
	 * @param value the value to set
	 */
	public ListExpression(final List<MetaExpression> value) {
		this.value = value;

		setValue(value);
		//Register references
		value.forEach(MetaExpression::registerReference);
	}

	@Override
	public Collection<Processable> getChildren() {
		return new ArrayList<>(value);
	}

	@Override
	public Number getNumberValue() {
		return value.size();
	}

	@Override
	public String getStringValue() {
		return toString();
	}

	@Override
	public boolean getBooleanValue() {
		return isNull();
	}

	@Override
	public boolean isNull() {
		return false;

	}
}
