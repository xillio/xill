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
    public static final int STACK_DEPTH = 2;
    private XillDebugger debugger;
    private InstructionSet instructions;

    @BeforeMethod
    public void setUp() {
        debugger = mock(XillDebugger.class);
        when(debugger.getStackDepth()).thenReturn(STACK_DEPTH);
        instructions = spy(new InstructionSet(debugger));
    }

    private void verifyAll(VariableDeclaration keyVar, List<MetaExpression> keys, VariableDeclaration valueVar, List<MetaExpression> values, InstructionFlow<MetaExpression> result) {
        // Verify the instructions were processed as many times as there are values.
        verify(instructions, times(values != null ? values.size() : 0)).process(debugger);

        // For each key and value, verify it was pushed to the var once. If the list is null, verify nothing was pushed.
        if (keys != null) {
            keys.forEach(key -> verify(keyVar, times(1)).pushVariable(eq(key), eq(STACK_DEPTH)));
        } else {
            verify(keyVar, times(0)).pushVariable(any(), anyInt());
        }
        if (values != null) {
            values.forEach(value -> verify(valueVar, times(1)).pushVariable(eq(value), eq(STACK_DEPTH)));
        } else {
            verify(valueVar, times(0)).pushVariable(any(), anyInt());
        }

        // Assert that the result does not have a value.
        if (result != null) {
            assertThrows(NoSuchElementException.class, result::get);
        }
    }

    @Test
    public void testProcessNull() {
        // Mock the key and value var.
        VariableDeclaration key = mock(VariableDeclaration.class);
        VariableDeclaration value = mock(VariableDeclaration.class);

        // Process.
        ForeachInstruction foreach = new ForeachInstruction(instructions, NULL, value, key);
        InstructionFlow<MetaExpression> result = foreach.process(debugger);

        // Verify.
        verifyAll(key, null, value, null, result);
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
        InstructionFlow<MetaExpression> result = foreach.process(debugger);

        // Verify.
        verifyAll(key, Collections.singletonList(fromValue(0)), value, Collections.singletonList(atomic), result);
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
        InstructionFlow<MetaExpression> result = foreach.process(debugger);

        // Verify.
        List<MetaExpression> keyList = new ArrayList<>(valueList.size()); // Not used in the actual list, just to verify.
        for (int i = 0; i < valueList.size(); i++) {
            keyList.add(fromValue(i));
        }
        verifyAll(key, keyList, value, valueList, result);
    }

    @Test
    public void testProcessList() {
        // Create the list.
        List<MetaExpression> valueList = Arrays.asList(fromValue("a"), fromValue("b"), fromValue("c"));
        MetaExpression metaValueList = fromValue(valueList);

        // Prevent expressions from being closed
        metaValueList.preventDisposal();

        // Mock the key and value var.
        VariableDeclaration key = mock(VariableDeclaration.class);
        VariableDeclaration value = mock(VariableDeclaration.class);

        // Process.
        ForeachInstruction foreach = new ForeachInstruction(instructions, metaValueList, value, key);
        InstructionFlow<MetaExpression> result = foreach.process(debugger);

        // Verify.
        List<MetaExpression> keyList = new ArrayList<>(valueList.size()); // Not used in the actual list, just to verify.
        for (int i = 0; i < valueList.size(); i++) {
            keyList.add(fromValue(i));
        }
        verifyAll(key, keyList, value, valueList, result);
    }

    @Test
    public void testProcessObject() {
        // Create the map.
        LinkedHashMap<String, MetaExpression> map = new LinkedHashMap<>();
        map.put("a", fromValue(true));
        map.put("b", fromValue(false));

        // Mock the key and value var.
        VariableDeclaration key = mock(VariableDeclaration.class);
        VariableDeclaration value = mock(VariableDeclaration.class);

        // Process.
        ForeachInstruction foreach = new ForeachInstruction(instructions, fromValue(map), value, key);
        InstructionFlow<MetaExpression> result = foreach.process(debugger);

        // Verify.
        List<MetaExpression> keyList = new ArrayList<>();
        map.keySet().forEach(k -> keyList.add(fromValue(k)));
        List<MetaExpression> valueList = new ArrayList<>(map.values());
        verifyAll(key, keyList, value, valueList, result);
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

    @Test
    public void testProcessInstructionReturns() {
        // Create the list.
        List<MetaExpression> valueList = Collections.singletonList(fromValue("baz"));

        // Create the return instruction.
        MetaExpression toReturn = fromValue(3.14);
        instructions.add(new ReturnInstruction(toReturn));

        // Mock the key and value var.
        VariableDeclaration key = mock(VariableDeclaration.class);
        VariableDeclaration value = mock(VariableDeclaration.class);

        // Process.
        ForeachInstruction foreach = new ForeachInstruction(instructions, fromValue(valueList), value, key);
        InstructionFlow<MetaExpression> result = foreach.process(debugger);

        // Verify.
        assertEquals(result.get(), toReturn);
        verifyAll(key, Collections.singletonList(fromValue(0)), value, valueList, null);
    }

    @Test
    public void testIndexIncreases() {
        instructions.add(new ContinueInstruction());

        testProcessList();
    }
}
