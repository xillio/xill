package nl.xillio.xill.components.instructions;

import nl.xillio.xill.TestUtils;
import nl.xillio.xill.api.components.InstructionFlow;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.MetaExpressionIterator;
import nl.xillio.xill.api.components.Processable;
import nl.xillio.xill.debugging.XillDebugger;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.*;
import java.util.function.Function;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public class ForeachInstructionTest extends TestUtils {
    private XillDebugger debugger;
    private InstructionSet instructions;

    @BeforeMethod
    public void setUp() {
        debugger = mock(XillDebugger.class);
        instructions = spy(new InstructionSet(debugger));
    }

    private void verifyAll(VariableDeclaration keyVar, List<MetaExpression> keys, VariableDeclaration valueVar, List<MetaExpression> values) {
        // Verify the instructions were processed as many times as there are values.
        verify(instructions, times(values != null ? values.size() : 0)).process(debugger);

        // For each key and value, verify it was pushed to the var once. If the list is null, verify nothing was pushed.
        if (keys != null) {
            keys.forEach(verify(keyVar, times(1))::pushVariable);
        } else {
            verify(keyVar, times(0)).pushVariable(any());
        }
        if (values != null) {
            values.forEach(verify(valueVar, times(1))::pushVariable);
        } else {
            verify(valueVar, times(0)).pushVariable(any());
        }
    }

    @Test
    public void testProcessNull() {
        // Mock the key and value var.
        VariableDeclaration key = mock(VariableDeclaration.class);
        VariableDeclaration value = mock(VariableDeclaration.class);

        // Process.
        ForeachInstruction foreach = new ForeachInstruction(instructions, NULL, value, key);
        InstructionFlow<MetaExpression> returned = foreach.process(debugger);

        // Assert that the result is a resumed instruction flow.
        assertEquals(returned.resumes(), true);
        assertThrows(NoSuchElementException.class, returned::get);

        // Verify.
        verifyAll(key, null, value, null);
    }

    @Test
    public void testProcessAtomic() {
        // Create the atomic.
        MetaExpression atomic = fromValue("foo");

        // Mock the key and value var.
        VariableDeclaration key = mock(VariableDeclaration.class);
        VariableDeclaration value = mock(VariableDeclaration.class);

        // Process.
        ForeachInstruction foreach = new ForeachInstruction(instructions, atomic, value, key);
        foreach.process(debugger);

        // Verify.
        verifyAll(key, Collections.singletonList(fromValue(0)), value, Collections.singletonList(atomic));
    }

    @Test
    public void testProcessAtomicWithIterator() {
        // Create the atomic and iterator.
        MetaExpression atomic = fromValue("foo");
        List<MetaExpression> valueList = Arrays.asList(fromValue("x"), fromValue("y"), fromValue("z"));
        MetaExpressionIterator iterator = new MetaExpressionIterator(valueList.iterator(), Function.identity());
        atomic.storeMeta(iterator);

        // Mock the key and value var.
        VariableDeclaration key = mock(VariableDeclaration.class);
        VariableDeclaration value = mock(VariableDeclaration.class);

        // Process.
        ForeachInstruction foreach = new ForeachInstruction(instructions, atomic, value, key);
        foreach.process(debugger);

        // Verify.
        List<MetaExpression> keyList = new ArrayList<>(valueList.size()); // Not used in the actual list, just to verify.
        for (int i = 0; i < valueList.size(); i++) {
            keyList.add(fromValue(i));
        }
        verifyAll(key, keyList, value, valueList);
    }

    @Test
    public void testProcessList() {
        // Create the list.
        List<MetaExpression> valueList = Arrays.asList(fromValue("a"), fromValue("b"), fromValue("c"));
        MetaExpression list = fromValue(valueList);

        // Mock the key and value var.
        VariableDeclaration key = mock(VariableDeclaration.class);
        VariableDeclaration value = mock(VariableDeclaration.class);

        // Process.
        ForeachInstruction foreach = new ForeachInstruction(instructions, list, value, key);
        foreach.process(debugger);

        // Verify.
        List<MetaExpression> keyList = new ArrayList<>(valueList.size()); // Not used in the actual list, just to verify.
        for (int i = 0; i < valueList.size(); i++) {
            keyList.add(fromValue(i));
        }
        verifyAll(key, keyList, value, valueList);
    }

    @Test
    public void testProcessObject() {
        // Create the map.
        LinkedHashMap<String, MetaExpression> values = new LinkedHashMap<>();
        values.put("a", fromValue(true));
        values.put("b", fromValue(false));
        MetaExpression map = fromValue(values);

        // Mock the key and value var.
        VariableDeclaration key = mock(VariableDeclaration.class);
        VariableDeclaration value = mock(VariableDeclaration.class);

        // Process.
        ForeachInstruction foreach = new ForeachInstruction(instructions, map, value, key);
        foreach.process(debugger);

        // Verify.
        List<MetaExpression> keyList = new ArrayList<>();
        values.keySet().forEach(k -> keyList.add(fromValue(k)));
        List<MetaExpression> valueList = new ArrayList<>(values.values());
        verifyAll(key, keyList, value, valueList);
    }

    @Test
    public void testGetChildrenWithoutKey() {
        // The iterable and value.
        MetaExpression atomic = fromValue("bar");
        VariableDeclaration value = mock(VariableDeclaration.class);

        // Create the instruction.
        ForeachInstruction foreach = new ForeachInstruction(instructions, atomic, value);

        // Verify that the children match the created items.
        assertEqualsNoOrder(foreach.getChildren().toArray(), new Processable[]{value, atomic, instructions});
    }

    @Test
    public void testGetChildrenWithKey() {
        // The iterable, key and value.
        MetaExpression atomic = fromValue("bar");
        VariableDeclaration value = mock(VariableDeclaration.class);
        VariableDeclaration key = mock(VariableDeclaration.class);

        // Create the instruction.
        ForeachInstruction foreach = new ForeachInstruction(instructions, atomic, value, key);

        // Verify that the children match the created items.
        assertEqualsNoOrder(foreach.getChildren().toArray(), new Processable[]{value, key, atomic, instructions});
    }
}
