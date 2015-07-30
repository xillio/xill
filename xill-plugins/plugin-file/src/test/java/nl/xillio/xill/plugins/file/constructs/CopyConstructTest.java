package nl.xillio.xill.plugins.file.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.RobotID;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.plugins.file.services.files.FileUtilities;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;

import static org.mockito.Mockito.*;

/**
 * Test the CopyConstruct
 */
public class CopyConstructTest {

    @Test
    public void testProcessNormal() throws Exception {
        //Source
        String sourceString = "This is the source file";
        File sourceFile = mock(File.class);
        MetaExpression source = mock(MetaExpression.class);
        when(source.getStringValue()).thenReturn(sourceString);

        //Target
        String targetString = "This is the target file";
        File targetFile = mock(File.class);
        MetaExpression target = mock(MetaExpression.class);
        when(target.getStringValue()).thenReturn(targetString);


        //Context
        RobotID robotID = mock(RobotID.class);
        ConstructContext context = mock(ConstructContext.class);
        when(context.getRobotID()).thenReturn(robotID);

        //FileUtils
        FileUtilities fileUtils = mock(FileUtilities.class);
        when(fileUtils.buildFile(robotID, sourceString)).thenReturn(sourceFile);
        when(fileUtils.buildFile(robotID, targetString)).thenReturn(targetFile);


        //Run the method
        CopyConstruct.process(context, fileUtils, source, target);

        //Verify
        verify(fileUtils, times(1)).copy(sourceFile, targetFile);
        verify(fileUtils, times(1)).buildFile(robotID, sourceString);
        verify(fileUtils, times(1)).buildFile(robotID, targetString);

    }

    @Test
    public void testProcessIOException() throws Exception {

        //Source
        String sourceString = "This is the source";
        MetaExpression source = mock(MetaExpression.class);
        when(source.getStringValue()).thenReturn(sourceString);

        //Target
        String targetString = "This is the target";
        MetaExpression target = mock(MetaExpression.class);
        when(target.getStringValue()).thenReturn(targetString);

        //Context
        Logger logger = mock(Logger.class);
        RobotID robotID = mock(RobotID.class);
        ConstructContext context = mock(ConstructContext.class);
        when(context.getRobotID()).thenReturn(robotID);
        when(context.getRootLogger()).thenReturn(logger);

        //FileUtilities
        File sourceFile = mock(File.class);
        when(sourceFile.getName()).thenReturn(sourceString);
        File targetFile = mock(File.class);
        when(targetFile.getName()).thenReturn(targetString);
        FileUtilities fileUtils = mock(FileUtilities.class);
        when(fileUtils.buildFile(robotID, sourceString)).thenReturn(sourceFile);
        when(fileUtils.buildFile(robotID, targetString)).thenReturn(targetFile);
        doThrow(new IOException("Something went wrong")).when(fileUtils).copy(sourceFile, targetFile);

        //Run the method
        CopyConstruct.process(context, fileUtils, source, target);

        //Verify the error that was logged
        verify(logger).error(eq("Failed to copy " + sourceString + " to " + targetString + ": Something went wrong"), any(IOException.class));


    }
}