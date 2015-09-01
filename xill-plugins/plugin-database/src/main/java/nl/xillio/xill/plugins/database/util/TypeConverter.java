package nl.xillio.xill.plugins.database.util;

import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nl.xillio.xill.api.components.MetaExpression;

/**
 * Converts from one JDBC type to a type suited for a MetaExpression.
 * 
 * 
 * @author Geert Konijnendijk
 */
public enum TypeConverter {

	/**
	 * Converts a {@link Byte} to an int
	 */
	BYTE(Byte.class) {
		@Override
		protected Object convert(Object o) throws SQLException {
			return ((Byte) o).intValue();
		}
	},
	/**
	 * Converts a {@link Short} to a int
	 */
	SHORT(Short.class) {
		@Override
		protected Object convert(Object o) throws SQLException {
			return ((Short) o).intValue();
		}
	},
	/**
	 * Converts a {@link Float} to a double
	 */
	FLOAT(Float.class) {
		@Override
		protected Object convert(Object o) throws SQLException {
			return ((Float) o).doubleValue();
		}
	},
	/**
	 * Converts a {@link BigDecimal} to a double. A double is the largest a {@link MetaExpression} can handle, this might return {@link Double#POSITIVE_INFINITY} or {@link Double#NEGATIVE_INFINITY}.
	 */
	BIG_DECIMAL(BigDecimal.class) {
		@Override
		public Object convert(Object o) {
			return ((BigDecimal) o).doubleValue();
		}
	},
	/**
	 * Convert a {@link Clob} (Character Large Object) to String.
	 */
	CLOB(Clob.class) {
		@Override
		protected Object convert(Object o) throws SQLException, ConversionException {
			Clob clob = (Clob) o;
			long length = clob.length();
			if (length > Integer.MAX_VALUE)
				throw new ConversionException("Clob is too long");
			return clob.getSubString(1, (int) length);
		}
	},
	/**
	 * Converts a {@link Date} to a String representation
	 */
	DATE(Date.class) {
		@Override
		protected Object convert(Object o) {
			return DATE_FORMAT.format((Date) o);
		}
	},
	/**
	 * Recursively converts an {@link Array} to a {@link List}
	 */
	ARRAY(Array.class) {
		@Override
		protected Object convert(Object o) throws SQLException, ConversionException {
			List<Object> result = new ArrayList<>();
			ResultSet array = ((Array) o).getResultSet();
			while (array.next()) {
				result.add(convertJDBCType(array.getObject(ARRAY_RESULTSET_VALUE)));
			}
			return result;
		}
	};

	// The index of the value in an Array ResultSet
	private static final int ARRAY_RESULTSET_VALUE = 2;

	// Formatter to use when formatting Dates
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	// A convertor should be able to convert this class and all extending classes
	private Class<?> accepts;

	private TypeConverter(Class<?> accepts) {
		this.accepts = accepts;
	}

	/**
	 * Convert an accepted type to a MetaExpression suited type
	 * 
	 * @param o
	 * @return
	 * @throws SQLException
	 *         When a database error occurs
	 * @throws ConversionException
	 *         When a type cannot be converted
	 */
	protected abstract Object convert(Object o) throws SQLException, ConversionException;

	/**
	 * Convert a JDBC type to a MetaExpression suited type.
	 * 
	 * @param o
	 * @return An object that can be passed to {@link MetaExpression#parseObject(Object)}
	 * @throws SQLException
	 * @throws ConversionException
	 */
	public static Object convertJDBCType(Object o) throws SQLException, ConversionException {
		if (o == null)
			return o;
		// Search for a suitable convertor
		for (TypeConverter convertor : values()) {
			// Convert if the given object is the same type or a super type of the object that is accepted
			if (convertor.accepts.isAssignableFrom(o.getClass())) {
				return convertor.convert(o);
			}
		}
		// No conversion necessary
		return o;
	}

	/**
	 * Thrown when a value cannot be converted by the {@link TypeConverter}
	 * 
	 * @author Geert Konijnendijk
	 *
	 */
	@SuppressWarnings("javadoc")
	public static class ConversionException extends Exception {

		public ConversionException() {
			super();
		}

		public ConversionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
			super(message, cause, enableSuppression, writableStackTrace);
		}

		public ConversionException(String message, Throwable cause) {
			super(message, cause);
		}

		public ConversionException(String message) {
			super(message);
		}

		public ConversionException(Throwable cause) {
			super(cause);
		}
	}

}
