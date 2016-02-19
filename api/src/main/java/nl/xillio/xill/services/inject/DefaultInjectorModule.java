package nl.xillio.xill.services.inject;

import com.google.inject.AbstractModule;
import me.biesaart.utils.Log;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.services.json.JacksonParser;
import nl.xillio.xill.services.json.JsonParser;
import nl.xillio.xill.services.json.PrettyJsonParser;
import org.slf4j.Logger;

/**
 * This module is the main module that will run for the injector at runtime.
 *
 * @author Thomas Biesaart
 */
public class DefaultInjectorModule extends AbstractModule {

    private static final Logger LOGGER = Log.get();

    @Override
    protected void configure() {
        try {
            //Some default injectors
            bind(String[].class).toInstance(new String[0]);
            bind(int[].class).toInstance(new int[0]);
            bind(boolean[].class).toInstance(new boolean[0]);

            //Some generic dependencies for plugins
            bind(ProcessBuilder.class).toConstructor(ProcessBuilder.class.getConstructor(String[].class));
            bind(JsonParser.class).toInstance(new JacksonParser(false));
            bind(PrettyJsonParser.class).toInstance(new JacksonParser(true));

            requestStaticInjection(Construct.class);
            requestStaticInjection(MetaExpression.class);

        } catch (NoSuchMethodException | SecurityException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

}
