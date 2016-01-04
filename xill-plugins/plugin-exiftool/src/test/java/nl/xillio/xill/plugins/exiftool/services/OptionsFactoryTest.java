package nl.xillio.xill.plugins.exiftool.services;

import nl.xillio.exiftool.LowerCamelCaseNameConvention;
import nl.xillio.exiftool.query.FileQueryOptions;
import nl.xillio.exiftool.query.FolderQueryOptions;
import nl.xillio.xill.TestUtils;
import nl.xillio.xill.api.components.MetaExpression;
import org.testng.annotations.Test;

import java.util.Arrays;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class OptionsFactoryTest {

    @Test
    public void testBuildFolderOptions() throws Exception {
        MetaExpression optionsExpression = TestUtils.parseJson("{\"recursive\": false, \"extensions\": {\"jpg\": true, \"other\": false}, \"nameConvention\": \"lcc\"}");

        OptionsFactory optionsFactory = new OptionsFactory(new ProjectionFactory());

        FolderQueryOptions options = optionsFactory.buildFolderOptions(optionsExpression);

        assertEquals(options.buildArguments(), Arrays.asList("-ext", "jpg", "--ext", "other"));
        assertEquals(options.getTagNameConvention().getClass(), LowerCamelCaseNameConvention.class);
    }

    @Test
    public void testBuildFileOptions() throws Exception {
        MetaExpression optionsExpression = TestUtils.parseJson("{\"nameConvention\": \"lcc\"}");

        OptionsFactory optionsFactory = new OptionsFactory(new ProjectionFactory());

        FileQueryOptions options = optionsFactory.buildFileOptions(optionsExpression);

        assertTrue(options.buildArguments().isEmpty());
        assertEquals(options.getTagNameConvention().getClass(), LowerCamelCaseNameConvention.class);
    }
}
