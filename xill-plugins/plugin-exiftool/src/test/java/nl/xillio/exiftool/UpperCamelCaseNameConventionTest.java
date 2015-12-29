package nl.xillio.exiftool;

import nl.xillio.exiftool.query.TagNameConvention;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.*;


public class UpperCamelCaseNameConventionTest {

    @Test
    public void testToConvention() throws Exception {
        String expectedOutput = "TestCase";
        TagNameConvention convention = new UpperCamelCaseNameConvention();

        assertEquals(convention.toConvention("Test Case"), expectedOutput);
        assertEquals(convention.toConvention("Test_Case"), expectedOutput);
    }
}