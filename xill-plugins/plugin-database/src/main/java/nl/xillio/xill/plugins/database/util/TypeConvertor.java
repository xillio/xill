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
 *TODO: add javadoc, add unit tests
 */
public enum TypeConvertor {

	/**
	 * 
	 */
	BYTE(Byte.class) {
		@Override
		protected Object convert(Object o) throws SQLException {
			return ((Byte) o).intValue();
		}
	},
	/**
	 * 
	 */
	SHORT(Short.class) {
		@Override
		protected Object convert(Object o) throws SQLException {
			return ((Short) o).intValue();
		}
	},
	/**
	 * 
	 */
	FLOAT(Float.class) {
		@Override
		protected Object convert(Object o) throws SQLException {
			return ((Float) o).doubleValue();
		}
	},
	/**
	 * 
	 */
	BIG_DECIMAL(BigDecimal.class) {
		@Override
		public Object convert(Object o) {
			return ((BigDecimal) o).doubleValue();
		}
	},
	/**
	 * 
	 */
	CLOB(Clob.class) {
		@Override
		protected Object convert(Object o) throws SQLException {
			Clob clob = (Clob) o;
			return clob.getSubString(1, (int) clob.length());
		}
	},
	/**
	 * 
	 *
	 */
	DATE(Date.class) {
		@Override
		protected Object convert(Object o) {
			return DATE_FORMAT.format((Date) o);
		}
	},
	/**
	 * 
	 *
	 */
	ARRAY(Array.class) {
		@Override
		protected Object convert(Object o) throws SQLException {
			List<Object> result = new ArrayList<>();
			ResultSet array = ((Array) o).getResultSet();
			while (!array.isAfterLast()) {
				result.add(convertJDBCType(array.getObject(ARRAY_RESULTSET_VALUE)));
			}
			return result;
		}
	};

	private static final int ARRAY_RESULTSET_VALUE = 2;
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat();

	private Class<?> accepts;

	private TypeConvertor(Class<?> accepts) {
		this.accepts = accepts;
	}

	/**
	 * Convert an accepted type to a MetaExpression suited type
	 * 
	 * @param o
	 * @return
	 */
	protected abstract Object convert(Object o) throws SQLException;

	/**
	 * Convert a JDBC type to a MetaExpression suited type.
	 * 
	 * @param o
	 * @return An object that can be passed to {@link MetaExpression#parseObject(Object)}
	 * @throws SQLException 
	 */
	public static Object convertJDBCType(Object o) throws SQLException {
		if(o == null)
			return o;
		// Search for a suitable convertor
		for (TypeConvertor convertor : values()) {
			// Convert if the given object is the same type or a super type of the object that is accepted
			if (convertor.accepts.isAssignableFrom(o.getClass())) {
				return convertor.convert(o);
			}
		}
		// No conversion necessary
		return o;
	}

}
