package nl.xillio.xill.plugins.concurrency.data;

import nl.xillio.xill.TestUtils;
import org.testng.annotations.Test;

import java.util.NoSuchElementException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;


public class XillQueueTest extends TestUtils {

    @Test
    public void testIterateClosedQueue() {
        XillQueue queue = new XillQueue(100);

        queue.close();
        assertEquals(queue.pop(), NULL);
    }

    @Test
    public void testIterateClosedQueueWithItems() {
        XillQueue queue = new XillQueue(10);

        queue.push(fromValue("firstItem"));
        queue.push(fromValue("SECOND_ITEM"));
        queue.push(emptyList());
        queue.close();

        assertEquals(queue.pop().getStringValue(), "firstItem");
        assertEquals(queue.pop().getStringValue(), "SECOND_ITEM");
        assertEquals(queue.pop().getStringValue(), "[]");
        assertEquals(queue.pop(), NULL);
    }

    @Test
    public void testCloseAndDrain() {
        XillQueue queue = new XillQueue(10);

        queue.push(fromValue("firstItem"));
        queue.push(fromValue("SECOND_ITEM"));
        queue.push(emptyList());
        queue.closeAndClear();

        assertEquals(queue.pop(), NULL);
    }
}