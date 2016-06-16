package nl.xillio.xill.api.components;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import nl.xillio.xill.api.data.DateFactory;
import org.testng.annotations.Test;

import java.lang.reflect.Field;
import java.sql.Date;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.testng.Assert.assertEquals;


public class MetaExpressionTest {

    // Sadly we have to test this using injection because of the static fields
    @Test
    public void testSqlDateExtraction() throws NoSuchFieldException, IllegalAccessException {
        // Inject factory
        Injector testInjector = getDateInjector();
        Field injectorField = MetaExpression.class.getDeclaredField("injector");
        injectorField.setAccessible(true);
        Object currentValue = injectorField.get(null);

        try {
            // Place the test injector for testing
            injectorField.set(null, testInjector);
            long time = 1465897665;

            Date inputDate = new Date(time);

            MetaExpression result = MetaExpression.parseObject(inputDate);
            nl.xillio.xill.api.data.Date date = result.getMeta(nl.xillio.xill.api.data.Date.class);
            long resultTime = date.getZoned().toEpochSecond();

            assertEquals(resultTime, time / 1000);

        } finally {
            // Recover the injector
            injectorField.set(null, currentValue);
        }

    }

    public Injector getDateInjector() {
        return Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(DateFactory.class).toInstance(
                        instant -> () -> ZonedDateTime.ofInstant(instant, ZoneId.systemDefault())
                );
            }
        });
    }
}
