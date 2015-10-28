package nl.xillio.xill.api.components;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import nl.xillio.util.IdentityArrayList;
import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.behavior.BooleanBehavior;
import nl.xillio.xill.api.construct.ExpressionBuilderHelper;
import nl.xillio.xill.api.data.MetadataExpression;
import nl.xillio.xill.api.errors.NotImplementedException;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * This class represents a general expression in the Xill language.
 */
public abstract class MetaExpression implements Expression, Processable {
	private static final Gson gson = new GsonBuilder().enableComplexMapKeySerialization().serializeNulls()
		// .setPrettyPrinting()
		.disableHtmlEscaping().disableInnerClassSerialization().serializeSpecialFloatingPointValues()
		.serializeNulls().create();
	private final MetadataExpressionPool<MetadataExpression> metadataPool = new MetadataExpressionPool<>();
	private Object value;
	private ExpressionDataType type = ExpressionDataType.ATOMIC;
	private boolean isClosed;
	private int referenceCount;
	private boolean preventDispose;

	/**
	 * Get a value from the {@link MetadataExpressionPool}
	 *
	 * @param <T>   the type of object to fetch
	 * @param clazz The type of class to fetch. <b>Note</b> that this doesn't take inheritance into account.
	 * @return The stored value or null if none was found
	 * @throws IllegalStateException if this expression has been closed
	 */
	public <T extends MetadataExpression> T getMeta(final Class<T> clazz) {
		if (isClosed()) {
			throw new IllegalStateException("This expression has already been closed.");
		}
		return metadataPool.get(clazz);
	}

	/**
	 * Store a value in the {@link MetadataExpressionPool} and default the key to the class of the object as returned by {@link Object#getClass()}
	 *
	 * @param <T>    the type key to store the object as
	 * @param object The object to store
	 * @return The previous stored value in the pool or null if no value was
	 * stored.
	 * @throws IllegalStateException if this expression has been closed
	 */
	@SuppressWarnings("unchecked")
	public <T extends MetadataExpression> T storeMeta(final T object) {
		return storeMeta((Class<T>) object.getClass(), object);
	}

	/**
	 * Store a value in the {@link MetadataExpressionPool}
	 *
	 * @param <T>    the type key to store the object as
	 * @param clazz  The object type to store
	 * @param object the object to store
	 * @return The previous stored value in the pool or null if no value was
	 * stored.
	 * @throws IllegalStateException if this expression has been closed
	 */
	public <T extends MetadataExpression> T storeMeta(final Class<T> clazz, final T object) {
		if (isClosed()) {
			throw new IllegalStateException("This expression has already been closed.");
		}
		return metadataPool.put(clazz, object);
	}

	/**
	 * Set the value of this variable to an {@link ExpressionDataType#ATOMIC} value
	 *
	 * @param value The {@link ExpressionDataType#ATOMIC} value to store
	 * @return self
	 * @throws IllegalStateException if this expression has been closed
	 */
	protected MetaExpression setValue(final Expression value) {
		if (isClosed()) {
			throw new IllegalStateException("This expression has already been closed.");
		}
		this.value = value;
		type = ExpressionDataType.ATOMIC;
		return this;
	}

	/**
	 * Set the value of this expression to that of another one
	 *
	 * @param value The other {@link MetaExpression} to copy the value from
	 * @return this
	 * @throws IllegalStateException if this expression has been closed
	 */
	protected MetaExpression setValue(final MetaExpression value) {
		if (isClosed()) {
			throw new IllegalStateException("This expression has already been closed.");
		}
		this.value = value.getValue();
		type = value.getType();
		return this;
	}

	/**
	 * Set the value of this variable to a {@link ExpressionDataType#LIST} value
	 *
	 * @param value the {@link ExpressionDataType#LIST} to set the value to
	 * @return self
	 * @throws IllegalStateException if this expression has been closed
	 */
	protected MetaExpression setValue(final List<MetaExpression> value) {
		if (isClosed()) {
			throw new IllegalStateException("This expression has already been closed.");
		}
		this.value = value;
		type = ExpressionDataType.LIST;
		return this;
	}

	/**
	 * Set the value of this variable to an {@link ExpressionDataType#OBJECT} value
	 *
	 * @param value in a linked hash map to enforce order
	 * @return self
	 * @throws IllegalStateException if this expression has been closed
	 */
	protected MetaExpression setValue(final LinkedHashMap<String, MetaExpression> value) {
		if (isClosed()) {
			throw new IllegalStateException("This expression has already been closed.");
		}
		this.value = value;
		type = ExpressionDataType.OBJECT;
		return this;
	}

	/**
	 * <p>
	 * Get the contained value of this {@link MetaExpression}. The return type of this method will be different for each type of {@link ExpressionDataType} which can be found by calling
	 * {@link #getType()}.
	 * </p>
	 * <ul>
	 * <li>{@link ExpressionDataType#ATOMIC}: Returns an {@link Expression}</li>
	 * <li>{@link ExpressionDataType#LIST}: Returns a {@link List
	 * List&lt;MetaExpression&gt;}</li>
	 * <li>{@link ExpressionDataType#OBJECT}: Returns a {@link Map
	 * Map&lt;String, MetaExpression&gt;}</li>
	 * </ul>
	 *
	 * @return the value according to the {@link ExpressionDataType} specification
	 * @throws IllegalStateException if this expression has been closed
	 */
	public Object getValue() {
		if (isClosed()) {
			throw new IllegalStateException("This expression has already been closed.");
		}
		return value;
	}

	/**
	 * <p>
	 * Returns the type of data stored in this {@link MetaExpression}
	 * </p>
	 * <ul>
	 * <li>{@link ExpressionDataType#ATOMIC}: A single value</li>
	 * <li>{@link ExpressionDataType#LIST}: A list of {@link MetaExpression}</li>
	 * <li>{@link ExpressionDataType#OBJECT}: An object with {@link String} indices</li>
	 * </ul>
	 *
	 * @return the type
	 * @throws IllegalStateException if this expression has been closed
	 */
	public ExpressionDataType getType() {
		if (isClosed()) {
			throw new IllegalStateException("This expression has already been closed.");
		}
		return type;
	}

	/**
	 * <p>
	 * Generate the JSON representation of this expression using a {@link Gson} parser
	 * </p>
	 * <b>NOTE: </b> This is not the string value of this expression. It is
	 * JSON. For the string value use {@link MetaExpression#getStringValue()}
	 *
	 * @return JSON representation
	 */
	@Override
	public String toString() {

		return toString(gson);
	}

	/**
	 * <p>
	 * Generate the JSON representation of this expression using a {@link Gson} parser
	 * </p>
	 * <p>
	 * <b>NOTE: </b> This is not the string value of this expression. It is JSON. For the string value use {@link MetaExpression#getStringValue()}
	 * </p>
	 *
	 * @param gsonParser The gson parser that should be used
	 * @return JSON representation
	 */
	public String toString(final Gson gsonParser) {
		MetaExpression cleaned = removeCircularReference(this, new IdentityArrayList<>(),
			ExpressionBuilderHelper.fromValue("<<CIRCULAR REFERENCE>>"));
		return gsonParser.toJson(extractValue(cleaned));
	}

	/**
	 * Remove all circular references to prepare for serialisation
	 *
	 * @param metaExpression      the expression to remove references from
	 * @param currentlyProcessing the currently processing expressions
	 * @param replacement         The replacement for circular references
	 * @return a copy of the passed expression without any circular references
	 */
	@SuppressWarnings("unchecked")
	private static MetaExpression removeCircularReference(final MetaExpression metaExpression,
																												final List<MetaExpression> currentlyProcessing, final MetaExpression replacement) {
		MetaExpression result = ExpressionBuilderHelper.NULL;
		currentlyProcessing.add(metaExpression);

		switch (metaExpression.getType()) {
			case LIST:
				List<MetaExpression> resultListValue = new ArrayList<>();

				for (MetaExpression child : (List<MetaExpression>) metaExpression.getValue()) {
					if (currentlyProcessing.stream().anyMatch(metaExp -> metaExp == child)) {
						// Circular reference
						resultListValue.add(replacement);
					} else {
						// No Circular reference
						resultListValue.add(removeCircularReference(child, currentlyProcessing, replacement));
					}
				}

				result = ExpressionBuilderHelper.fromValue(resultListValue);

				break;
			case OBJECT:
				LinkedHashMap<String, MetaExpression> resultMapValue = new LinkedHashMap<>();

				for (Map.Entry<String, MetaExpression> pair : ((Map<String, MetaExpression>) metaExpression.getValue())
					.entrySet()) {

					if (currentlyProcessing.stream().anyMatch(metaExp -> metaExp == pair.getValue())) {
						// Circular reference
						resultMapValue.put(pair.getKey(), replacement);
					} else {
						resultMapValue.put(pair.getKey(), removeCircularReference(pair.getValue(), currentlyProcessing, replacement));
					}
				}
				result = ExpressionBuilderHelper.fromValue(resultMapValue);
				break;

			case ATOMIC:
				result = metaExpression;
				break;
		}

		currentlyProcessing.remove(metaExpression);
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof MetaExpression) {
			return equals((MetaExpression) obj);
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		if (getType().equals(ExpressionDataType.ATOMIC)) {
			return getStringValue().hashCode();
		}
		return getValue().toString().hashCode();
	}

	/**
	 * Check equality to other {@link MetaExpression}
	 *
	 * @param other the other
	 * @return true if and only if the expressions have equal value
	 */
	public boolean equals(final MetaExpression other) {

		// Compare the type
		if (getType() != other.getType()) {
			return false;
		}

		switch (getType()) {
			case ATOMIC:
				return getBooleanValue() == other.getBooleanValue() &&
					getStringValue().equals(other.getStringValue()) &&
					(getNumberValue().doubleValue() == other.getNumberValue().doubleValue()
						|| Double.isNaN(getNumberValue().doubleValue()) && Double.isNaN(getNumberValue().doubleValue()));
			case LIST:
				return getValue().equals(other.getValue());
			case OBJECT:
				return getValue().equals(other.getValue());
		}

		return false;
	}

	@Override
	public InstructionFlow<MetaExpression> process(final Debugger debugger) {
		return InstructionFlow.doResume(this);
	}

	/**
	 * Extracts the actual Java Object value for this expression.
	 *
	 * @param expression The {@link MetaExpression} to extract a value from.
	 * @return <ul>
	 * <li>{@link ExpressionDataType#ATOMIC}: returns an {@link Object} This represents a singular value that is parsed in the folowing way:
	 * <ol>
	 * <li>If the value is null: return null</li>
	 * <li>If the expression is created using {@link ExpressionBuilder#fromValue(boolean)}: return {@link Boolean}</li>
	 * <li>If the value can be a number (so also string constants like "5.7") it will return a {@link Double}</li>
	 * <li>In all other cases return a {@link String} representation.
	 * </ol>
	 * </li>
	 * <li>{@link ExpressionDataType#LIST}: returns a {@link List
	 * List&lt;Object&gt;} where {@link Object} is the result of a recursive call of this method.</li>
	 * <li>{@link ExpressionDataType#OBJECT}: returns a {@link Map
	 * Map&lt;String, Object&gt;} where {@link Object} is the result of a recursive call of this method.</li>
	 * </ul>
	 */
	public static Object extractValue(final MetaExpression expression) {
		return extractValue(expression, new LinkedHashMap<>());

	}

	@SuppressWarnings("unchecked")
	private static Object extractValue(final MetaExpression expression, final Map<MetaExpression, Object> results) {
		
		if (results.containsKey(expression)) {
			return results.get(expression);
		}
		Object result;
		switch (expression.getType()) {
			case ATOMIC:
				// null
				if (expression.isNull()) {
					return null;
				}

				// Boolean
				if (expression.getValue() instanceof BooleanBehavior) {
					result = expression.getBooleanValue();
				}

				// String
				else if (Double.isNaN(expression.getNumberValue().doubleValue())) {
					result = expression.getStringValue();
				}

				// Int
				else if (expression.getNumberValue().intValue() == expression.getNumberValue().doubleValue()) {
					result = expression.getNumberValue().intValue();
				} else {

					// Double
					result = expression.getNumberValue();
				}
				break;
			case LIST:
				List<Object> resultList = new ArrayList<>();
				results.put(expression, resultList);
				resultList.addAll(((List<MetaExpression>) expression.getValue()).stream().map(v -> extractValue(v, results))
					.collect(Collectors.toList()));
				result = resultList;
				break;
			case OBJECT:
				Map<String, Object> resultObject = new LinkedHashMap<>();
				results.put(expression, resultObject);
				for (Entry<String, MetaExpression> pair : ((Map<String, MetaExpression>) expression.getValue()).entrySet()) {
					resultObject.put(pair.getKey(), extractValue(pair.getValue(), results));
				}
				result = resultObject;
				break;
			default:
				throw new NotImplementedException("This type has not been implemented.");
		}
		return result;

	}

	/**
	 * Register a reference to this variable.
	 * This generally only happens during assignment
	 */
	public final void registerReference() {
		// When this variable is assigned, take over ownership
		preventDispose = false;
		referenceCount++;
	}

	/**
	 * Release a reference to this expression
	 * This generally only happens at the end of scope
	 */
	public final void releaseReference() {
		referenceCount--;

		if (!preventDispose && referenceCount <= 0) {
			try {
				close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		preventDispose = false;
	}

	/**
	 * Next time a reference is released, this expression won't be disposed.
	 */
	public final void preventDisposal() {
		preventDispose = true;
	}

	/**
	 * @return true if this expression has been closed using {@link MetaExpression#close()}
	 */
	public final boolean isClosed() {
		return isClosed;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void close() throws Exception {
		if (isClosed || this == ExpressionBuilderHelper.NULL) {
			return;
		}

		isClosed = true;
		closeMetaPool();

		// Close children
		switch (type) {
			case LIST:
				((List<MetaExpression>) value).forEach(nl.xillio.xill.api.components.MetaExpression::releaseReference);
				break;
			case OBJECT:
				((Map<String, MetaExpression>) value).values().forEach(nl.xillio.xill.api.components.MetaExpression::releaseReference);
				break;
			default:
				break;
		}
		value = null;
	}

	/**
	 * Dispose all items in the {@link MetadataExpressionPool}
	 */
	protected void closeMetaPool() {
		try {
			metadataPool.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Reset the reference count to 0
	 */
	protected void resetReferences() {
		referenceCount = 0;
	}

	/**
	 * Attempt to parse an object into a MetaExpression
	 *
	 * @param value the object to parse
	 * @return a {@link MetaExpression}, not null
	 * @throws IllegalArgumentException when the value cannot be parsed
	 */
	public static MetaExpression parseObject(final Object value) throws IllegalArgumentException {
		return parseObject(value, new IdentityHashMap<>());
	}

	@SuppressWarnings("unchecked")
	private static MetaExpression parseObject(final Object root, final Map<Object, MetaExpression> cache) throws IllegalArgumentException {
		// Check the cache (Don't use key because we need REFERENCE equality not CONTENT)
		for (Map.Entry<Object, MetaExpression> entry : cache.entrySet()) {
			if (entry.getKey() == root) {
				// It seems like we found our match
				return entry.getValue();
			}
		}

		if (root == null) {
			return ExpressionBuilder.NULL;
		}

		if (root instanceof List) {
			// Push stub
			MetaExpression result = ExpressionBuilderHelper.emptyList();
			cache.put(root, result);
			List<MetaExpression> values = (List<MetaExpression>) result.getValue();

			// Parse children
			for (Object child: (List<?>) root) {
				MetaExpression current = parseObject(child, cache);
				values.add(current);
				current.registerReference();
			}

			// Push list
			result.setValue(values);

			return result;
		}

		if (root instanceof Map) {
			// Push stub
			MetaExpression result = ExpressionBuilderHelper.emptyObject();
			cache.put(root, result);
			LinkedHashMap<String, MetaExpression> values = (LinkedHashMap<String, MetaExpression>) result.getValue();

			// Parse children
			for (Entry<?, ?> child : ((Map<?, ?>) root).entrySet()) {
				MetaExpression current = parseObject(child.getValue(), cache);
				values.put(child.getKey().toString(), current);
				current.registerReference();
			}

			// Push map
			result.setValue(values);

			return result;
		}

		// No list, no map. This must be an atomic value. No need to cache those since they cannot be circular
		// Boolean
		if (root instanceof Boolean) {
			return ExpressionBuilderHelper.fromValue((Boolean) root);
		}

		// Numbers
		if (root instanceof Integer) {
			return ExpressionBuilderHelper.fromValue((Integer) root);
		}

		if (root instanceof Long) {
			return ExpressionBuilderHelper.fromValue((Long) root);
		}

		if (root instanceof Double) {
			return ExpressionBuilderHelper.fromValue((Double) root);
		}

		if (root instanceof String) {
			return ExpressionBuilderHelper.fromValue((String) root);
		}

		throw new IllegalArgumentException("The class type " + root.getClass().getName() + " has not been implemented by parseObject");
	}
}
