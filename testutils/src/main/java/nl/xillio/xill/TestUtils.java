package nl.xillio.xill;

import com.google.inject.AbstractModule;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.services.files.FileResolver;
import nl.xillio.xill.services.inject.DefaultInjectorModule;
import nl.xillio.xill.services.inject.InjectorUtils;
import nl.xillio.xill.services.json.JacksonParser;
import nl.xillio.xill.services.json.JsonParser;
import nl.xillio.xill.services.json.PrettyJsonParser;

import java.io.File;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * This class represents various utilities you could need during testing of a xill plugin
 */
public class TestUtils extends ConstructTest {
    public static final FileResolver CONSTRUCT_FILE_RESOLVER;

    static {
        CONSTRUCT_FILE_RESOLVER = mock(FileResolver.class);
        InjectorUtils.initialize(new TestModule());
        InjectorUtils.getGlobalInjector();
    }

    static class TestModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(FileResolver.class).toInstance(CONSTRUCT_FILE_RESOLVER);
            bind(JsonParser.class).toInstance(new JacksonParser(false));
            bind(PrettyJsonParser.class).toInstance(new JacksonParser(true));
            requestStaticInjection(Construct.class);
        }
    }

    public static void setFileResolverReturnValue(File file) {
        doReturn(file).when(CONSTRUCT_FILE_RESOLVER).buildFile(any(), anyString());
    }
}
