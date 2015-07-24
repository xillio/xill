package nl.xillio.xill.plugins.system.constructs;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.mockito.ArgumentCaptor;
import org.testng.annotations.Test;

import nl.xillio.xill.api.ConstructTest;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.plugins.system.exec.ProcessDescription;
import nl.xillio.xill.plugins.system.exec.ProcessFactory;

/**
 * Test the {@link ExecConstruct}
 */
public class ExecConstructTest extends ConstructTest {

	/**
	 * Test invoke the construct with a string as command and default working directory
	 * 
	 *
	 */
	@Test
	public void testProcessString() {
		//Mock the context
		MetaExpression arguments = mockExpression(ATOMIC);
		when(arguments.getStringValue()).thenReturn("TestCommand");

		MetaExpression directory = NULL;
		Logger log = mock(Logger.class);
		ProcessFactory processFactory = mock(ProcessFactory.class);
		Process process = mock(Process.class);
		InputStream out = mockStream(1);
		InputStream err = mockStream(2);
		when(process.getInputStream()).thenReturn(out);
		when(process.getErrorStream()).thenReturn(err);
		when(processFactory.apply(any())).thenReturn(process);

		ArgumentCaptor<ProcessDescription> descriptionCaptor = ArgumentCaptor.forClass(ProcessDescription.class);

		//Run the method
		MetaExpression result = ExecConstruct.process(arguments, directory, log, processFactory);

		
		//Make assertions
		verify(processFactory).apply(descriptionCaptor.capture());

		assertEquals(descriptionCaptor.getValue().getCommands(), new String[] {"TestCommand"});
		assertEquals(result.getType(), OBJECT);

		@SuppressWarnings("unchecked")
		Map<String, MetaExpression> value = (Map<String, MetaExpression>) result.getValue();

		assertEquals(value.get("output").getStringValue(), "[\"\\u0000\"]");
		assertEquals(value.get("output").getType(), LIST);
		assertEquals(value.get("errors").getStringValue(), "[\"\\u0000\\u0000\"]");
		assertEquals(value.get("errors").getType(), LIST);
		assertNotNull(value.get("runtime"));
		assertEquals(value.get("runtime").getType(), ATOMIC);
	}

	/**
	 * Test invoke the construct with a list as command and default working directory
	 *
	 */
	@Test
	public void testProcessList() {
		//Mock the context
		MetaExpression arguments = mockExpression(ATOMIC);
		when(arguments.getType()).thenReturn(LIST);
		when(arguments.getValue()).thenReturn(Arrays.asList(
			fromValue("Test"),
			fromValue("command"),
			fromValue("-t")));

		MetaExpression directory = NULL;
		Logger log = mock(Logger.class);
		ProcessFactory processFactory = mock(ProcessFactory.class);
		Process process = mock(Process.class);
		InputStream out = mockStream(6);
		InputStream err = mockStream(3);
		when(process.getInputStream()).thenReturn(out);
		when(process.getErrorStream()).thenReturn(err);
		when(processFactory.apply(any())).thenReturn(process);

		ArgumentCaptor<ProcessDescription> descriptionCaptor = ArgumentCaptor.forClass(ProcessDescription.class);

		//Run the method
		MetaExpression result = ExecConstruct.process(arguments, directory, log, processFactory);

		//Make assertions
		verify(processFactory).apply(descriptionCaptor.capture());

		assertEquals(descriptionCaptor.getValue().getCommands(), new String[] {"Test", "command", "-t"});

		assertEquals(result.getType(), OBJECT);

		@SuppressWarnings("unchecked")
		Map<String, MetaExpression> value = (Map<String, MetaExpression>) result.getValue();

		assertEquals(value.get("output").getStringValue(), "[\"\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\"]");
		assertEquals(value.get("output").getType(), LIST);
		assertEquals(value.get("errors").getStringValue(), "[\"\\u0000\\u0000\\u0000\"]");
		assertEquals(value.get("errors").getType(), LIST);
		assertNotNull(value.get("runtime"));
		assertEquals(value.get("runtime").getType(), ATOMIC);
	}

	/**
	 * Make an input stream that will return a certain amount of null characters
	 * @param length
	 * @return
	 */
	private static InputStream mockStream(final int length) {
		InputStream stream = mock(InputStream.class);

		try {
			when(stream.read(any(), anyInt(), anyInt())).thenReturn(length, -1);

			when(stream.available()).thenReturn(length);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return stream;
	}
}
