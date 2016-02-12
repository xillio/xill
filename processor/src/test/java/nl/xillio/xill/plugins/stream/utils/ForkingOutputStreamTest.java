package nl.xillio.xill.plugins.stream.utils;

import nl.xillio.xill.TestUtils;
import nl.xillio.xill.api.components.MetaExpression;
import org.testng.annotations.Test;

import java.io.OutputStream;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;


public class ForkingOutputStreamTest extends TestUtils {

    @Test
    public void testWriteInt() throws Exception {
        OutputStream stream1 = mock(OutputStream.class);
        OutputStream stream2 = mock(OutputStream.class);
        OutputStream fork = new ForkingOutputStream(NULL, stream1, stream2);
        int data = 222;
        fork.write(data);

        verify(stream1).write(data);
        verify(stream2).write(data);
    }


    @Test
    public void testWriteArray() throws Exception {
        OutputStream stream1 = mock(OutputStream.class);
        OutputStream stream2 = mock(OutputStream.class);
        OutputStream fork = new ForkingOutputStream(NULL, stream1, stream2);
        byte[] data = new byte[]{1, 2, 5, 3, 2};
        fork.write(data);

        verify(stream1).write(data);
        verify(stream2).write(data);
    }

    @Test
    public void testWrite2() throws Exception {
        OutputStream stream1 = mock(OutputStream.class);
        OutputStream stream2 = mock(OutputStream.class);
        OutputStream fork = new ForkingOutputStream(NULL, stream1, stream2);
        byte[] data = new byte[]{1, 2, 5, 3, 2};
        fork.write(data, 5, 10);

        verify(stream1).write(data, 5, 10);
        verify(stream2).write(data, 5, 10);
    }

    @Test
    public void testFlush() throws Exception {
        OutputStream stream1 = mock(OutputStream.class);
        OutputStream stream2 = mock(OutputStream.class);
        OutputStream fork = new ForkingOutputStream(NULL, stream1, stream2);
        fork.flush();

        verify(stream1).flush();
        verify(stream2).flush();
    }

    @Test
    public void testClose() throws Exception {
        OutputStream stream1 = mock(OutputStream.class);
        OutputStream stream2 = mock(OutputStream.class);
        MetaExpression expression = mock(MetaExpression.class);
        OutputStream fork = new ForkingOutputStream(expression, stream1, stream2);
        verify(expression).registerReference();
        fork.close();

        verify(stream1).close();
        verify(stream2).close();
        verify(expression).releaseReference();
    }
}