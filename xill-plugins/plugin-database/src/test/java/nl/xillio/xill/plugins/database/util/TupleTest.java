package nl.xillio.xill.plugins.database.util;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Unit tests for the {@link Tuple} utility
 *
 * @author Daan Knoope
 */
public class TupleTest {

    /**
     * Tests creation and properties of the tuple
     *
     * @throws Exception
     */
    @Test
    public void tupleTest() throws Exception {
        Tuple<Integer, String> tuple = new Tuple<>(1, "value");
        assertEquals(tuple.getValue(), "value");
        assertEquals((int) tuple.getKey(), 1);
    }

}
