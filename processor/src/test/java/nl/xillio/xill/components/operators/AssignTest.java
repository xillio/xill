package nl.xillio.xill.components.operators;

import nl.xillio.xill.TestUtils;
import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.NullDebugger;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.components.instructions.VariableDeclaration;
import org.testng.annotations.Test;

import java.util.*;

import static org.testng.Assert.assertEquals;


public class AssignTest extends TestUtils {
    private final Debugger debugger = new NullDebugger();

    @Test
    public void testAssignToAtomic() {
        VariableDeclaration variableDeclaration = new VariableDeclaration(fromValue("Hello"), "testVar");
        variableDeclaration.process(debugger);

        Assign assign = new Assign(variableDeclaration, Collections.emptyList(), fromValue("World"));

        assertEquals(variableDeclaration.getVariable().getStringValue(), "Hello");

        assign.process(debugger);
        assertEquals(variableDeclaration.getVariable().getStringValue(), "World");
    }

    @Test
    public void testAssignToList() {
        VariableDeclaration variableDeclaration = new VariableDeclaration(
                list(fromValue("Hello")),
                "testVar"
        );
        variableDeclaration.process(debugger);


        Assign assign = new Assign(variableDeclaration, Collections.singletonList(fromValue(1)), fromValue("World"));
        assertEquals(variableDeclaration.getVariable().getStringValue(), "[\"Hello\"]");

        assign.process(debugger);
        assertEquals(variableDeclaration.getVariable().getStringValue(), "[\"Hello\",\"World\"]");
    }

    @Test
    public void testComplexAssign() {
        MetaExpression value = list(
                map(
                        "test",
                        list(
                                map(
                                        "other",
                                        fromValue(4)
                                )
                        )
                )
        );

        VariableDeclaration variableDeclaration = new VariableDeclaration(value, "test");
        variableDeclaration.process(debugger);
        Assign assign = new Assign(
                variableDeclaration,
                Arrays.asList(
                        fromValue("hello"),
                        fromValue(0),
                        fromValue("test"),
                        fromValue(0)
                ),
                fromValue("New Value")
        );

        assertEquals(variableDeclaration.getVariable().getStringValue(), "[{\"test\":[{\"other\":4}]}]");

        assign.process(debugger);
        assertEquals(variableDeclaration.getVariable().getStringValue(), "[{\"test\":[{\"other\":4,\"hello\":\"New Value\"}]}]");
    }

    private MetaExpression map(String key, MetaExpression value) {
        LinkedHashMap<String, MetaExpression> result = new LinkedHashMap<>();
        result.put(key, value);
        return fromValue(result);
    }

    private MetaExpression list(MetaExpression item) {
        List<MetaExpression> result = new ArrayList<>();
        result.add(item);
        return fromValue(result);
    }
}