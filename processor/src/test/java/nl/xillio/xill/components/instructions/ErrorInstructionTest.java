package nl.xillio.xill.components.instructions;

import nl.xillio.xill.api.Xill;
import nl.xillio.xill.api.components.InstructionFlow;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.Processable;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.debugging.DelegateDebugger;
import nl.xillio.xill.debugging.ErrorBlockDebugger;
import nl.xillio.xill.debugging.XillDebugger;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockSettings;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

/**
 * Created by Sander on 5-1-2016.
 */
public class ErrorInstructionTest{
/*
    private XillDebugger xillDebugger;
    private InstructionSet doBlock;
    private InstructionSet errorBlock;
    private InstructionSet successBlock;
    private InstructionSet finallyBlock;
    private InstructionFlow<MetaExpression> result;

    @Mock private ErrorBlockDebugger errorDebugger;

    @BeforeMethod
    public void setUp(){
        xillDebugger = mock(XillDebugger.class);
        doBlock = mock(InstructionSet.class);
        errorBlock = mock(InstructionSet.class);
        successBlock = mock(InstructionSet.class);
        finallyBlock = mock(InstructionSet.class);

        result = (InstructionFlow<MetaExpression>) mock(InstructionFlow.class);

    }

    @Test
    public void testProcessSuccessNoReturn(){

        ErrorInstruction instruction = new ErrorInstruction(doBlock,successBlock,errorBlock,finallyBlock,null);
        MetaExpression expression = mock(MetaExpression.class);

        when(doBlock.process(any())).thenReturn(result);
        when((result).returns()).thenReturn(false); //no return
        when((result).get()).thenReturn(expression);
        //run
        instruction.process(xillDebugger);

        //verify
        verify(doBlock, times(1)).process(any()); //do once
        verify(result.get(), never()).preventDisposal(); //since there is no return.
        verify(errorBlock,never()).process(xillDebugger); //errorBlock never called
        verify(successBlock,times(1)).process(xillDebugger);
        verify(finallyBlock,times(1)).process(xillDebugger);
    }

    @Test
    public void testProcessSuccessReturn(){
        //mock

        ErrorInstruction instruction = new ErrorInstruction(doBlock,successBlock,errorBlock,finallyBlock,null);
        MetaExpression expression = mock(MetaExpression.class);

        when(doBlock.process(any())).thenReturn(result);
        when((result).returns()).thenReturn(true); //we return
        when((result).get()).thenReturn(expression);
        //run
        instruction.process(xillDebugger);

        //verify
        verify(doBlock, times(1)).process(any()); //do once
        verify(expression,atLeastOnce()).preventDisposal(); //since we return
        verify(errorBlock,never()).process(xillDebugger); //errorBlock never called
        verify(successBlock,never()).process(xillDebugger); //no succes when returning
        verify(finallyBlock,times(1)).process(xillDebugger); //finally block is called.
        verify(expression,atLeastOnce()).allowDisposal();

    }

    @Test void testProcessFailure(){
        ErrorInstruction instruction = new ErrorInstruction(doBlock,successBlock,errorBlock,finallyBlock,null);
        MetaExpression expression = mock(MetaExpression.class);

        when(doBlock.process(any())).thenThrow(new RobotRuntimeException("fail"));
        when((result).returns()).thenReturn(false); //no return
        when((result).get()).thenReturn(expression);
        //run
        instruction.process(xillDebugger);

        //verify
        verify(doBlock, times(1)).process(any()); //do once
        verify(finallyBlock,times(1)).process(xillDebugger); //finallyBlock always called
        verify(errorBlock,times(1)).process(xillDebugger); //errorBlock called
    }

    @Test void testProcessFailureWithCause(){
        VariableDeclaration cause = mock(VariableDeclaration.class);
        ErrorInstruction instruction = new ErrorInstruction(doBlock,successBlock,errorBlock,finallyBlock,cause);
        MetaExpression expression = mock(MetaExpression.class);

        when(doBlock.process(any())).thenThrow(new RobotRuntimeException("fail"));
        when((result).returns()).thenReturn(false); //no return
        when((result).get()).thenReturn(expression);
        //run
        instruction.process(xillDebugger);

        //verify
        verify(doBlock, times(1)).process(any()); //do once
        verify(finallyBlock,times(1)).process(xillDebugger); //finallyBlock always called
        verify(errorBlock,times(1)).process(xillDebugger); //errorBlock called
        verify(cause,times(1)).pushVariable(any());
    }*/
}