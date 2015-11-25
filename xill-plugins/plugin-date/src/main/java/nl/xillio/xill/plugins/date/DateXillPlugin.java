package nl.xillio.xill.plugins.date;

import com.google.inject.Provides;
import nl.xillio.plugins.XillPlugin;
import nl.xillio.xill.api.data.DateFactory;
import nl.xillio.xill.plugins.date.services.DateServiceImpl;

/**
 * This package includes all date constructs.
 */
public class DateXillPlugin extends XillPlugin {

    @Provides
    DateFactory dateFactory() {
        return new DateServiceImpl();
    }
}
