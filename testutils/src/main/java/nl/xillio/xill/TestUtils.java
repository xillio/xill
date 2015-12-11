package nl.xillio.xill;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import nl.xillio.xill.api.behavior.StringBehavior;
import nl.xillio.xill.api.components.ExpressionDataType;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ExpressionBuilderHelper;
import nl.xillio.xill.api.errors.NotImplementedException;
import nl.xillio.xill.services.files.FileResolver;
import nl.xillio.xill.services.json.JacksonParser;
import nl.xillio.xill.services.json.JsonParser;
import nl.xillio.xill.services.json.PrettyJsonParser;

import java.io.File;
import java.util.Collections;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * This class represents various utilities you could need during testing of a xill plugin
 */
public class TestUtils extends ExpressionBuilderHelper {
    public static final FileResolver CONSTRUCT_FILE_RESOLVER;

    static {
        CONSTRUCT_FILE_RESOLVER = mock(FileResolver.class);
        Guice.createInjector(new TestModule());
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
        doReturn(file).when(CONSTRUCT_FILE_RESOLVER).buildFile(any(), anyString());
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
        switch (type) {
            case ATOMIC:
                when(expression.getValue()).thenReturn(new StringBehavior(""));
                break;
            case LIST:
                when(expression.getValue()).thenReturn(Collections.<MetaExpression>emptyList());
                break;
            case OBJECT:
                when(expression.getValue()).thenReturn(Collections.<String, MetaExpression>emptyMap());
                break;
            default:
                throw new NotImplementedException("This type has not been implemented.");
        }
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
