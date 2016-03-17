package nl.xillio.xill.plugins.concurrency.data;

import nl.xillio.xill.TestUtils;
import org.testng.annotations.Test;

import java.util.NoSuchElementException;

import static org.testng.Assert.*;


public class XillQueueTest extends TestUtils {

    @Test
    public void testIterateClosedQueue() {
        XillQueue queue = new XillQueue(100);

        queue.close();
        assertFalse(queue.hasNext());
    }

    @Test
    public void testIterateClosedQueueWithItems() {
        XillQueue queue = new XillQueue(10);

        queue.push(fromValue("firstItem"));
        queue.push(fromValue("SECOND_ITEM"));
        queue.push(emptyList());
        queue.close();

        assertTrue(queue.hasNext());
        assertEquals(queue.next().getStringValue(), "firstItem");
        assertEquals(queue.next().getStringValue(), "SECOND_ITEM");
        assertEquals(queue.next().getStringValue(), "[]");
        assertFalse(queue.hasNext());
    }

    @Test
    public void testCloseAndDrain() {
        XillQueue queue = new XillQueue(10);

        queue.push(fromValue("firstItem"));
        queue.push(fromValue("SECOND_ITEM"));
        queue.push(emptyList());
        queue.closeAndClear();

        assertFalse(queue.hasNext());
    }

    @Test(expectedExceptions = NoSuchElementException.class)
    public void testNextOnClosedEmptyQueue() {
        XillQueue queue = new XillQueue(10);

        queue.close();
        queue.next();
    }
}