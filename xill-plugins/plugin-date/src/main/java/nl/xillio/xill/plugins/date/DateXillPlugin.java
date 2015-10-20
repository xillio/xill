package nl.xillio.xill.plugins.date;

import nl.xillio.plugins.XillPlugin;
import nl.xillio.xill.plugins.date.services.DateService;
import nl.xillio.xill.plugins.date.services.DateServiceImpl;

import com.google.inject.Binder;

/**
 * This package includes all date constructs
 */
public class DateXillPlugin extends XillPlugin {

	@Override
	public void configure(Binder binder) {
		super.configure(binder);

		binder.bind(DateService.class).to(DateServiceImpl.class);
	}
}
