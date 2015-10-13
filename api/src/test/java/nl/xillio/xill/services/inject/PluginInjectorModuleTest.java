package nl.xillio.xill.services.inject;

import com.google.inject.Binder;
import com.google.inject.Guice;
import nl.xillio.plugins.XillPlugin;
import org.testng.annotations.Test;

import java.util.LinkedList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

/**
 * Created by ernst on 13/10/15.
 */
public class PluginInjectorModuleTest {

    @Test
    public void configure() {

        // Mocks
        XillPlugin plugin1 = mock(XillPlugin.class);
        XillPlugin plugin2 = mock(XillPlugin.class);
        List<XillPlugin> pluginList = new LinkedList<>();
        pluginList.add(plugin1);
        pluginList.add(plugin2);

        PluginInjectorModule pluginInjectorModule = new PluginInjectorModule(pluginList);

        // Run test
        Guice.createInjector(pluginInjectorModule);

        // Verify
        verify(plugin1, times(1)).configure(any(Binder.class));
        verify(plugin2, times(1)).configure(any(Binder.class));
    }



    @Test(expectedExceptions = {Exception.class})
    public void configureWithException() {
        // Mocks
        XillPlugin plugin1 = mock(XillPlugin.class);

        doThrow(new Exception()).when(plugin1).configure(any());

        List<XillPlugin> pluginList = new LinkedList<>();
        pluginList.add(plugin1);

        PluginInjectorModule pluginInjectorModule = new PluginInjectorModule(pluginList);

        // Run test
        Guice.createInjector(pluginInjectorModule);

        // Verify
        verify(plugin1, times(1)).configure(any(Binder.class));

    }
}