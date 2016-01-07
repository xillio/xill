package nl.xillio.xill.components.instructions;

import junit.framework.Assert;
import nl.xillio.xill.api.components.InstructionFlow;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.Processable;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.debugging.ErrorBlockDebugger;
import nl.xillio.xill.debugging.XillDebugger;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.Assertion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Created by Sander on 5-1-2016.
 */
public class ErrorInstructionTest {

    private XillDebugger xillDebugger;
    private InstructionSet doBlock;
    private InstructionSet errorBlock;
    private InstructionSet successBlock;
    private InstructionSet finallyBlock;
    private ErrorBlockDebugger errorDebugger;
    private InstructionFlow<MetaExpression> result;

    @BeforeMethod
    public void setUp() {
        xillDebugger = new XillDebugger();
        doBlock = new InstructionSet(xillDebugger);
        errorBlock = new InstructionSet(xillDebugger);
        successBlock = new InstructionSet(xillDebugger);
        finallyBlock = new InstructionSet(xillDebugger);
        errorDebugger = new ErrorBlockDebugger();


    }

    @Test
    public void testProcessSuccess() {

        ErrorInstruction instruction = new ErrorInstruction(doBlock, successBlock, errorBlock, finallyBlock, null);

        InstructionFlow<MetaExpression> returnedValue = instruction.process(xillDebugger);

    }

    @Test
    public void testProcessReturn() {
        result = (InstructionFlow<MetaExpression>) mock(InstructionFlow.class);
        InstructionSet mockDoBlock = mock(InstructionSet.class);
        ErrorInstruction instruction = new ErrorInstruction(mockDoBlock, successBlock, errorBlock, finallyBlock, null);

        when(mockDoBlock.process(any())).thenReturn(result);
        when(result.returns()).thenReturn(true);

        InstructionFlow<MetaExpression> returnedValue = instruction.process(xillDebugger);
        Assert.assertNotSame(returnedValue,InstructionFlow.doResume());
    }

    @Test
    public void testProcessFailNoCause() {
        result = (InstructionFlow<MetaExpression>) mock(InstructionFlow.class);
        InstructionSet mockDoBlock = mock(InstructionSet.class);
        ErrorInstruction instruction = new ErrorInstruction(mockDoBlock, successBlock, errorBlock, finallyBlock, null);
        when(mockDoBlock.process(any())).thenThrow(new RobotRuntimeException("fail"));

        instruction.process(xillDebugger);
    }

    @Test
    public void testProcessFailWithCause()  {
        result = (InstructionFlow<MetaExpression>) mock(InstructionFlow.class);
        InstructionSet mockDoBlock = mock(InstructionSet.class);
        Processable mockProcessable = mock(Processable.class);
        VariableDeclaration cause = new VariableDeclaration(mockProcessable, "fail");
        ErrorInstruction instruction = new ErrorInstruction(mockDoBlock, successBlock, errorBlock, finallyBlock, cause);
        when(mockDoBlock.process(any())).thenThrow(new RobotRuntimeException("fail"));

        InstructionFlow<MetaExpression> returnedValue = instruction.process(xillDebugger);

    }

    @Test
    public void testProcessOptionals(){
        result = (InstructionFlow<MetaExpression>) mock(InstructionFlow.class);
        InstructionSet mockSuccesBlock = mock(InstructionSet.class);
        InstructionSet mockErrorBlock = mock(InstructionSet.class);
        InstructionSet mockFinallyBlock = mock(InstructionSet.class);

        ErrorInstruction instruction = new ErrorInstruction(doBlock, null, mockErrorBlock, null, null);

        when(result.returns()).thenReturn(false);

        InstructionFlow<MetaExpression> returnedValue = instruction.process(xillDebugger);

        verify(mockSuccesBlock,never()).process(any());
        verify(mockFinallyBlock,never()).process(any());

    }

    @Test
    public void testGetChildren(){

        Processable mockProcessable = mock(Processable.class);
        VariableDeclaration cause = new VariableDeclaration(mockProcessable, "fail");

        List<Processable> children = new ArrayList<>();
        children.add(doBlock);
        children.add(successBlock);
        children.add(errorBlock);
        children.add(finallyBlock);
        children.add(cause);

        ErrorInstruction instruction = new ErrorInstruction(doBlock, successBlock, errorBlock, finallyBlock, cause);

        Collection<Processable> returnValue = instruction.getChildren();
    }
}