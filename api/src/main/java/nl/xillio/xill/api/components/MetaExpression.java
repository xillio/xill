package nl.xillio.xill.api.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.exception.ExceptionUtils;

import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.NullDebugger;
import nl.xillio.xill.api.errors.NotImplementedException;
import nl.xillio.xill.api.errors.RobotRuntimeException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 *
 */
public abstract class MetaExpression implements Expression, Processable {
	private static final Gson gson = new GsonBuilder()
	.enableComplexMapKeySerialization()
	.serializeNulls()
	// .setPrettyPrinting()
	.disableHtmlEscaping()
	.disableInnerClassSerialization()
	.create();
	private final MetadataExpressionPool<Object> metadataPool = new MetadataExpressionPool<>();
	private Object value;
	private ExpressionDataType type = ExpressionDataType.ATOMIC;

	/**
	 * Get a value from the {@link MetadataExpressionPool}
	 *
	 * @param clazz
	 * @return The stored value or null if none was found
	 */
	public <T> T getMeta(final Class<T> clazz) {
		return metadataPool.get(clazz);
	}

	/**
	 * Store a value in the {@link MetadataExpressionPool}
	 *
	 * @param object
	 * @return The previous stored value in the pool or null if no value was stored.
	 */
	public <T> T storeMeta(final T object) {
		return metadataPool.put(object);
	}

	/**
	 * Set the value of this variable to an {@link ExpressionDataType#ATOMIC} value
	 *
	 * @param value
	 * @return self
	 */
	protected MetaExpression setValue(final Expression value) {
		this.value = value;
		type = ExpressionDataType.ATOMIC;
		return this;
	}

	/**
	 * Set the value of this expression to that of another one
	 *
	 * @param value
	 * @return
	 */
	protected MetaExpression setValue(final MetaExpression value) {
		this.value = value.getValue();
		type = value.getType();
		return this;
	}

	/**
	 * Set the value of this variable to a {@link ExpressionDataType#LIST} value
	 *
	 * @param value
	 * @return self
	 */
	protected MetaExpression setValue(final List<MetaExpression> value) {
		this.value = value;
		type = ExpressionDataType.LIST;
		return this;
	}

	/**
	 * Set the value of this variable to an {@link ExpressionDataType#OBJECT} value
	 *
	 * @param value
	 * @return self
	 */
	protected MetaExpression setValue(final Map<String, MetaExpression> value) {
		this.value = value;
		type = ExpressionDataType.OBJECT;
		return this;
	}

	/**
	 * Get the contained value of this {@link MetaExpression}<br/>
	 * The return type of this method will be different for each type of {@link ExpressionDataType}
	 * which can be found by calling {@link #getType()}.<br/>
	 * <ul>
	 * <li>{@link ExpressionDataType#ATOMIC}: Returns an {@link Expression} </li>
	 * <li>{@link ExpressionDataType#LIST}: Returns a {@link List List&lt;MetaExpression&gt;} </li>
	 * <li>{@link ExpressionDataType#OBJECT}: Returns a {@link Map Map&lt;String, MetaExpression&gt;} </li>
	 * </ul>
	 * @return the value according to the {@link ExpressionDataType} specification
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * @return the type
	 */
	public ExpressionDataType getType() {
		return type;
	}

	@Override
	public String toString() {
		List<MetaExpression> initialVisited = new ArrayList<>(1);
		initialVisited.add(this);
		MetaExpression cleaned = removeCircularReference(this, initialVisited, ExpressionBuilder.fromValue("<<CIRCULAR REFERENCE>>"));
		return gson.toJson(extractValue(cleaned));
	}

	/**
	 * Remove all circular references to prepare for serialisation
	 *
	 * @param metaExpression
	 * @param visited
	 */
	@SuppressWarnings("unchecked")
	private static MetaExpression removeCircularReference(final MetaExpression metaExpression,
			final List<MetaExpression> visited, final MetaExpression replacement) {
		Processable result = ExpressionBuilder.NULL;

		switch (metaExpression.getType()) {
			case LIST:
				List<Processable> resultListValue = new ArrayList<>();

				for (MetaExpression child : (List<MetaExpression>) metaExpression.getValue()) {
					if (visited.stream().anyMatch(metaExp -> metaExp == child)) {
						// Circular reference
						resultListValue.add(replacement);
					} else {
						// No Circular reference
						resultListValue.add(removeCircularReference(child, visited, replacement));
					}
				}

				result = new ListExpression(resultListValue);

				break;
			case OBJECT:
				Map<Processable, Processable> resultMapValue = new HashMap<>();

				for (Map.Entry<String, MetaExpression> pair : ((Map<String, MetaExpression>) metaExpression.getValue()).entrySet()) {
					Processable key = ExpressionBuilder.fromValue(pair.getKey());

					if (visited.stream().anyMatch(metaExp -> metaExp == pair.getValue())) {
						// Circular reference
						resultMapValue.put(key, replacement);
					} else {
						// No circular reference
						visited.add(pair.getValue());
						resultMapValue.put(key, removeCircularReference(pair.getValue(), visited, replacement));
					}
				}
				result = new ObjectExpression(resultMapValue);
				break;
			case ATOMIC:
				result = metaExpression;
				break;
		}

		try {
			return result.process(new NullDebugger()).get();
		} catch (RobotRuntimeException e) {
			return new AtomicExpression(ExceptionUtils.getRootCause(e).getMessage());
		}
	}

	@Override
	public boolean equals(final Object obj) {
		// Only compare to MetaExpression
		if (!(obj instanceof MetaExpression)) {
			return false;
		}

		MetaExpression other = (MetaExpression) obj;

		// Compare the type
		if (getType() != other.getType()) {
			return false;
		}

		switch (getType()) {
			case ATOMIC:
				return getBooleanValue() == other.getBooleanValue() && // Boolean equality
				getStringValue().equals(other.getStringValue()) && // String equality
				( // Double equality (or both NaN)
						getNumberValue().doubleValue() == other.getNumberValue().doubleValue() ||
						(Double.isNaN(getNumberValue().doubleValue()) && Double.isNaN(getNumberValue().doubleValue()))
						);
			case LIST:
				return getValue().equals(other.getValue());
			case OBJECT:
				return getValue().equals(other.getValue());
		}

		return false;
	}

	@Override
	public InstructionFlow<MetaExpression> process(Debugger debugger) {
		return InstructionFlow.doResume(this);
	}

	/**
	 * Extracts the actual Java Object value for this expression.
	 * @param expression The {@link MetaExpression} to extract a value from.
	 * @return <ul> 
	 * <li>{@link ExpressionDataType#ATOMIC}: returns an {@link Object}<br/>
	 * 					This represents a singular value that is parsed in the folowing way: 
	 * 					<ol>
	 * 						<li>If the value is null: return null</li>
	 * 						<li>If the value can be a number (so also string constants like "5.7") it will return a {@link Double}</li>
	 * 						<li>In all other cases return a {@link String} representation.
	 * 					</ol> </li>
	 * <li>{@link ExpressionDataType#LIST}: returns a {@link List List&lt;Object&gt;} where {@link Object} is the result of a recursive call of this method.</li>
	 * <li>{@link ExpressionDataType#OBJECT}: returns a {@link Map Map&lt;String, Object&gt;}  where {@link Object} is the result of a recursive call of this method.</li>
	 * </ul>
	 */
	@SuppressWarnings("unchecked")
	public static Object extractValue(final MetaExpression expression) {
		switch (expression.getType()) {
			case ATOMIC:
				// null
				if (expression.isNull()) {
					return null;
				}

				// String
				if (Double.isNaN(expression.getNumberValue().doubleValue())) {
					return expression.getStringValue();
				}

				// Int
				if (expression.getNumberValue().intValue() == expression.getNumberValue().doubleValue()) {
					return expression.getNumberValue().intValue();
				}

				// Double
				return expression.getNumberValue();

			case LIST:
				return ((List<MetaExpression>) expression.getValue()).stream().map(MetaExpression::extractValue).collect(Collectors.toList());
			case OBJECT:
				return ((Map<String, MetaExpression>) expression.getValue()).entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> extractValue(entry.getValue())));
		}

		throw new NotImplementedException("This type has not been implemented.");

	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void close() throws Exception {
		metadataPool.close();
		
		//Close children
		switch (getType()) {
			case LIST:
				for(MetaExpression expr : ((List<MetaExpression>)getValue())) expr.close();
				break;
			case OBJECT:
				for(MetaExpression expr : ((Map<String, MetaExpression>)getValue()).values()) expr.close();
				break;
			default:
				break;
		}
	}
}
