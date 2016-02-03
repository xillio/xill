package nl.xillio.xill;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import nl.xillio.xill.api.components.ExpressionBuilderHelper;
import nl.xillio.xill.api.components.ExpressionDataType;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.services.files.FileResolver;
import nl.xillio.xill.services.json.JacksonParser;
import nl.xillio.xill.services.json.JsonException;
import nl.xillio.xill.services.json.JsonParser;
import nl.xillio.xill.services.json.PrettyJsonParser;

import java.io.File;
import java.nio.file.Path;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * This class represents various utilities you could need during testing of a xill plugin
 */
public class TestUtils extends ExpressionBuilderHelper {
    public static final FileResolver CONSTRUCT_FILE_RESOLVER;
    private static final JsonParser jsonParser = new JacksonParser(true);

    static {
        CONSTRUCT_FILE_RESOLVER = mock(FileResolver.class);
        Guice.createInjector(new TestModule());
    }

    public static MetaExpression parseJson(String json) throws JsonException {
        return parseObject(jsonParser.fromJson(json, Object.class));
    }

    static class TestModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(FileResolver.class).toInstance(CONSTRUCT_FILE_RESOLVER);
            bind(JsonParser.class).toInstance(new JacksonParser(false));
            bind(PrettyJsonParser.class).toInstance(new JacksonParser(true));
            requestStaticInjection(Construct.class);
            requestStaticInjection(MetaExpression.class);
        }
    }

    public static void setFileResolverReturnValue(File file) {
        doReturn(file.toPath()).when(CONSTRUCT_FILE_RESOLVER).buildPath(any(), anyString());
    }

    public static void setFileResolverReturnValue(Path file) {
        doReturn(file).when(CONSTRUCT_FILE_RESOLVER).buildPath(any(), anyString());
    }


    /**
     * Mock a {@link MetaExpression} of a specific type
     *
     * @param type the type of the {@link MetaExpression}
     * @return the {@link MetaExpression}
     */
    protected static MetaExpression mockExpression(final ExpressionDataType type) {
        MetaExpression expression = mock(MetaExpression.class);
        when(expression.getType()).thenReturn(type);
        return expression;
    }

    /**
     * Mock a {@link MetaExpression} that holds a certain value
     *
     * @param type        the type
     * @param boolValue   the result of {@link MetaExpression#getBooleanValue()}
     * @param numberValue the result of {@link MetaExpression#getStringValue()}
     * @param stringValue the result of {@link MetaExpression#getNumberValue()}
     * @return the expression
     */
    protected static MetaExpression mockExpression(final ExpressionDataType type, final boolean boolValue, final double numberValue, final String stringValue) {
        MetaExpression expression = mockExpression(type);

        when(expression.getBooleanValue()).thenReturn(boolValue);
        when(expression.getNumberValue()).thenReturn(numberValue);
        when(expression.getStringValue()).thenReturn(stringValue);
        return expression;
    }
}
