package nl.xillio.xill.plugins.system.constructs;

import nl.xillio.xill.TestUtils;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.plugins.system.exec.ProcessDescription;
import nl.xillio.xill.plugins.system.exec.ProcessFactory;
import org.mockito.ArgumentCaptor;
import org.slf4j.Logger;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * Test the {@link ExecConstruct}
 */
public class ExecConstructTest extends TestUtils {

    /**
     * Test invoke the construct with a string as command and default working directory
     */
    @Test
    public void testProcessString() throws InterruptedException {
        // Mock the context
        MetaExpression arguments = mockAtomicCommand();

        MetaExpression directory = NULL;
        Logger log = mock(Logger.class);
        ProcessFactory processFactory = mockProcessFactory(1, 2, 0);

        ArgumentCaptor<ProcessDescription> descriptionCaptor = ArgumentCaptor.forClass(ProcessDescription.class);

        // Run the method
        MetaExpression result = ExecConstruct.process(arguments, directory, log, processFactory);

        // Make assertions
        verify(processFactory).apply(descriptionCaptor.capture());
        verify(log, times(0)).error(any());

        assertEquals(descriptionCaptor.getValue().getCommands(), new String[]{"TestCommand"});
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
     */
    @Test
    public void testProcessList() throws InterruptedException {
        // Mock the context
        MetaExpression arguments = mockExpression(LIST);
        when(arguments.getValue()).thenReturn(Arrays.asList(
                fromValue("Test"),
                fromValue("command"),
                fromValue("-t")));

        MetaExpression directory = NULL;
        Logger log = mock(Logger.class);
        ProcessFactory processFactory = mockProcessFactory(6, 3, 0);

        ArgumentCaptor<ProcessDescription> descriptionCaptor = ArgumentCaptor.forClass(ProcessDescription.class);

        // Run the method
        MetaExpression result = ExecConstruct.process(arguments, directory, log, processFactory);

        // Make assertions
        verify(processFactory).apply(descriptionCaptor.capture());

        assertEquals(descriptionCaptor.getValue().getCommands(), new String[]{"Test", "command", "-t"});

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
     * Test that an error is logged when the process ends with a non-zero exit code
     */
    @Test
    public void testProcessError() throws InterruptedException {
        // Mock the context
        MetaExpression arguments = mockAtomicCommand();

        MetaExpression directory = NULL;
        Logger log = mock(Logger.class);
        ProcessFactory processFactory = mockProcessFactory(1, 2, 1);


        // Run the method
        MetaExpression result = ExecConstruct.process(arguments, directory, log, processFactory);

        // Make assertions
        verify(log, times(1)).error(any());
    }

    /**
     * Make an input stream that will return a certain amount of null characters
     *
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

    /**
     * Mock a {@link ProcessFactory} that will output a given number of null characters on the output and error stream
     * @param outputStreamLength Number of null characters the output stream returns
     * @param errorStreamLength Number of null characters the error stream returns
     *                          @param exitCode The exit code the process should have
     * @return The mocked {@link ProcessFactory}
     */
    private ProcessFactory mockProcessFactory(int outputStreamLength, int errorStreamLength, int exitCode) throws InterruptedException {
        ProcessFactory processFactory = mock(ProcessFactory.class);
        Process process = mock(Process.class);
        InputStream out = mockStream(outputStreamLength);
        InputStream err = mockStream(errorStreamLength);
        when(process.getInputStream()).thenReturn(out);
        when(process.getErrorStream()).thenReturn(err);
        when(process.waitFor()).thenReturn(exitCode);
        when(processFactory.apply(any())).thenReturn(process);
        return processFactory;
    }

    /**
     * @return A single atomic argument containing a string value
     */
    private MetaExpression mockAtomicCommand() {
        MetaExpression arguments = mockExpression(ATOMIC);
        when(arguments.getStringValue()).thenReturn("TestCommand");
        return arguments;
    }

}
