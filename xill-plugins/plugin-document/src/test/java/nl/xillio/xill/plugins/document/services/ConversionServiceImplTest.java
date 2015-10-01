package nl.xillio.xill.plugins.document.services;

import static nl.xillio.xill.plugins.document.util.DocumentTestUtil.mockDecoratorBuilder;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import nl.xillio.udm.builders.DecoratorBuilder;
import nl.xillio.udm.builders.DocumentRevisionBuilder;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
/**
 * Tests the methods in the {@link ConversionServiceImpl}.
 * 
 * @author Geert Konijnendijk
 *
 */
public class ConversionServiceImplTest {

	private ConversionServiceImpl service;

	/**
	 * Create the {@link ConversionServiceImpl}
	 */
	@BeforeClass
	public void initialize() {
		service = new ConversionServiceImpl();
	}

	/**
	 * Test {@link ConversionServiceImpl#mapToUdm(Map, DocumentRevisionBuilder)}
	 */
	@Test
	public void testMapToUdm() {
		// Mock
		Map<String, Map<String, Object>> object = createDecoratorMap();

		DocumentRevisionBuilder builder = mock(DocumentRevisionBuilder.class);

		Map<String, DecoratorBuilder> decoratorBuilders = new HashMap<>();
		
		for (String decoratorName : object.keySet()) {
			DecoratorBuilder decoratorBuilder = mockDecoratorBuilder();
			decoratorBuilders.put(decoratorName, decoratorBuilder);
			when(builder.decorator(decoratorName)).thenReturn(decoratorBuilder);
		}

		// Run
		service.mapToUdm(object, builder);

		// Verify
		verify(builder).commit();
		for (String decoratorName : object.keySet()){
			verify(builder).decorator(decoratorName);
			DecoratorBuilder decoratorBuilder = decoratorBuilders.get(decoratorName);
			Map<String, Object> decorator = object.get(decoratorName);
			for (String fieldName : decorator.keySet()) {
				verify(decoratorBuilder).field(fieldName, decorator.get(fieldName));
			}
		}

		// Assert
	}

	/**
	 * Test {@link ConversionServiceImpl#udmToMap(DocumentRevisionBuilder)}
	 */
	@Test
	public void testUdmToMap() {
		// Mock		
		Map<String, Map<String, Object>> object = createDecoratorMap();
		
		DocumentRevisionBuilder builder = mock(DocumentRevisionBuilder.class);
		when(builder.decorators()).thenReturn(new ArrayList<>(object.keySet()));
		
		Map<String, DecoratorBuilder> decorators=new HashMap<>();
		for (String decoratorName : object.keySet()) {
			DecoratorBuilder decoratorBuilder = mockReadableDecoratorBuilder(object.get(decoratorName));
			when(builder.decorator(decoratorName)).thenReturn(decoratorBuilder);
			decorators.put(decoratorName, decoratorBuilder);
		}
		
		// Run
		Map<String, Map<String, Object>> result = service.udmToMap(builder);

		// Verify
		verify(builder).decorators();
		for (String decoratorName : object.keySet()){
			verify(builder).decorator(decoratorName);
			DecoratorBuilder decoratorBuilder = decorators.get(decoratorName);
			verify(decoratorBuilder).fields();
			for (String fieldName : object.get(decoratorName).keySet()){
				verify(decoratorBuilder).field(fieldName);
			}
		}

		// Assert
		assertEquals(result, object);
	}

	/**
	 * Creates a map representing a collection of decorators
	 * 
	 * @return A collection of decorators represented as a map
	 */
	private Map<String, Map<String, Object>> createDecoratorMap() {
		Map<String, Map<String, Object>> object = new HashMap<>();
		Map<String, Object> decorator1 = new HashMap<>();
		Map<String, Object> decorator2 = new HashMap<>();
		decorator1.put("test1", 1);
		decorator2.put("test2", 2);
		decorator2.put("test3", 3);
		object.put("d1", decorator1);
		object.put("d2", decorator2);
		return object;
	}

	private DecoratorBuilder mockReadableDecoratorBuilder(Map<String, Object> decorator) {
		DecoratorBuilder builder = mockDecoratorBuilder();
		when(builder.fields()).thenReturn(new ArrayList<>(decorator.keySet()));
		when(builder.field(anyString())).thenAnswer(i -> decorator.get(i.getArgumentAt(0, String.class)));
		return builder;
	}

}
