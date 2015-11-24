package nl.xillio.xill.plugins.date;

import com.google.inject.Binder;
import nl.xillio.plugins.XillPlugin;
import nl.xillio.xill.api.data.DateFactory;
import nl.xillio.xill.plugins.date.services.DateService;
import nl.xillio.xill.plugins.date.services.DateServiceImpl;

/**
 * This package includes all date constructs
 */
public class DateXillPlugin extends XillPlugin {

    @Override
    public void configure(Binder binder) {
        super.configure(binder);

        binder.bind(DateFactory.class).to(DateServiceImpl.class);
    }
}
