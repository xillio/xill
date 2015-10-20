package nl.xillio.xill.plugins.list;

import com.google.inject.Binder;

import nl.xillio.plugins.XillPlugin;
import nl.xillio.xill.plugins.list.services.reverse.Reverse;
import nl.xillio.xill.plugins.list.services.reverse.ReverseImpl;
import nl.xillio.xill.plugins.list.services.sort.Sort;
import nl.xillio.xill.plugins.list.services.sort.SortImpl;

/**
 * This package includes all list constructs
 */
public class ListXillPlugin extends XillPlugin {

	@Override
	public void configure(final Binder binder) {
		super.configure(binder);

		binder.bind(Sort.class).to(SortImpl.class);
		binder.bind(Reverse.class).to(ReverseImpl.class);
	}
}
