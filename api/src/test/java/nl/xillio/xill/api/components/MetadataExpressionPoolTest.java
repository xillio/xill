package nl.xillio.xill.api.components;

import org.testng.annotations.Test;

import java.io.Serializable;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.testng.Assert.*;


public class MetadataExpressionPoolTest {

    @Test
    public void testGetByInterface() throws Exception {
        MetadataExpressionPool<Object> pool = new MetadataExpressionPool<>();
        TestObject object = new TestObject();

        assertFalse(pool.hasValue(Serializable.class));
        assertFalse(pool.hasValue(TestObject.class));

        pool.put(object);

        assertTrue(pool.hasValue(Serializable.class));
        assertTrue(pool.hasValue(TestObject.class));
        assertSame(pool.get(Serializable.class), object);
        assertSame(pool.get(TestObject.class), object);
    }

    @Test
    public void testClose() {
        MetadataExpressionPool<Object> pool = new MetadataExpressionPool<>();
        TestObject obj = mock(TestObject.class);

        assertEquals(pool.size(), 0);
        pool.put(obj);
        assertEquals(pool.size(), 1);

        pool.close();
        assertEquals(pool.size(), 0);

        verify(obj).close();
    }

    class TestObject implements Serializable, AutoCloseable {

        @Override
        public void close() {

        }
    }
}