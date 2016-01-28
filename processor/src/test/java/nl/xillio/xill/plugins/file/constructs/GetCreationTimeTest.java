package nl.xillio.xill.plugins.file.constructs;

import nl.xillio.xill.TestUtils;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.RobotID;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.data.Date;
import nl.xillio.xill.api.data.DateFactory;
import nl.xillio.xill.plugins.file.services.files.FileUtilities;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;

/**
 * @author Folkert van Verseveld
 */
public class GetCreationTimeTest extends TestUtils {
    @Test
    public void now() throws IOException {
        String path = "This is some dummy path";
        MetaExpression expr = mock(MetaExpression.class);
        when(expr.getStringValue()).thenReturn(path);

        // Context
        RobotID robotID = mock(RobotID.class);
        ConstructContext context = mock(ConstructContext.class);
        when(context.getRobotID()).thenReturn(robotID);
        // File
        File file = new File("yay");
        setFileResolverReturnValue(file);

        // FileUtils
        Instant now = Instant.now();
        FileTime time = FileTime.from(now);
        FileUtilities io = mock(FileUtilities.class);
        when(io.getCreationDate(file)).thenReturn(time);

        ZonedDateTime ztime = ZonedDateTime.ofInstant(now, ZoneId.systemDefault());
        Date d = mock(Date.class);
        when(d.getZoned()).thenReturn(ztime);
        DateFactory date = mock(DateFactory.class);
        when(date.from(any())).thenReturn(d);
        // evaluate
        MetaExpression lvalue = GetCreationDate.process(context, date, io, expr);
        // confirm
        verify(io, times(1)).getCreationDate(file);
        assertEquals(lvalue.getStringValue(), d.toString());
    }
}
