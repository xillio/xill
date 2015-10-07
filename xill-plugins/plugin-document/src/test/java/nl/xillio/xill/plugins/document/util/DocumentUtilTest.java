package nl.xillio.xill.plugins.document.util;

import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.testng.annotations.Test;

import nl.xillio.xill.ConstructTest;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.errors.RobotRuntimeException;

/**
 * 
 * Test the methods in {@link DocumentUtil}
 * 
 * @author Geert Konijnendijk
 *
 */
public class DocumentUtilTest extends ConstructTest {

	/**
	 * Test {@link DocumentUtil#expressionBodyToMap(MetaExpression)} under normal circumstances
	 */
	@Test
	public void testExpressionBodyToMapNormal(){
		// Mock
		Map<String, Map<String, String>> bodyMap = new HashMap<>();
		Map<String, String> content1 = new HashMap<>();
		content1.put("k1", "v1");
		Map<String, String> content2 = new HashMap<>();
		content1.put("k1", "v1");
		content1.put("k2", "v2");
		bodyMap.put("c1", content1);
		bodyMap.put("c2", content2);
		
		// Mock a MetaExpression willed with MetaExpressions
		Map<String, MetaExpression> backingMap = bodyMap.entrySet().stream()
			.collect(Collectors.toMap(e -> e.getKey(),
				e -> mockReadableObject(e.getValue().entrySet().stream()
					.collect(Collectors.toMap(e2 -> e2.getKey(), e2 -> mockExpression(ATOMIC, false, Double.NaN, e2.getValue()))))));
		MetaExpression body = mockReadableObject(backingMap);

		// Run
		Map<String, Map<String, Object>> result = DocumentUtil.expressionBodyToMap(body);
		
		// Verify
		
		// Assert
		assertEquals(result, bodyMap);
	}

	/**
	 * Test that {@link DocumentUtil#expressionBodyToMap(MetaExpression)} throws a {@link RobotRuntimeException} when it receives a {@link MetaExpression} that is not composed fully of objects.
	 */
	@Test(expectedExceptions = RobotRuntimeException.class)
	public void testExpressionBodyToMapInvalid() {
		// Mock
		MetaExpression body = mockExpression(OBJECT);
		Map<String, MetaExpression> backingMap = new HashMap<>();
		backingMap.put("value", mockExpression(ATOMIC, false, 42, null));
		when(body.getValue()).thenReturn(backingMap);

		// Run
		Map<String, Map<String, Object>> result = DocumentUtil.expressionBodyToMap(body);
	}

	/**
	 * Mocks a readable {@link MetaExpression} of type OBJECT.
	 * 
	 * @param backingMap
	 *        The map that will be read from
	 * @return A mocked {@link MetaExpression}
	 */
	private MetaExpression mockReadableObject(Map<String, MetaExpression> backingMap) {
		MetaExpression expression = mockExpression(OBJECT);
		when(expression.getValue()).thenReturn(backingMap);
		return expression;
	}

}
