package nl.xillio.xill.plugins.file;

import java.io.File;

import static org.mockito.Mockito.*;

import com.google.inject.AbstractModule;

import nl.xillio.xill.api.components.RobotID;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.services.files.FileResolver;
import nl.xillio.xill.services.inject.InjectorUtils;

public class TestInjectorModule extends AbstractModule{
	public static final File FILE = mock(File.class);
	public static final String ABS_PATH = "RESULT PATH";
	public static final FileResolver RESOLVER = mock(FileResolver.class);
	
	static {
		InjectorUtils.initialize(new TestInjectorModule());
		InjectorUtils.getGlobalInjector();
		
		when(FILE.getAbsolutePath()).thenReturn(ABS_PATH);
		when(RESOLVER.buildFile(any(RobotID.class), anyString())).thenReturn(FILE);
	}
	
	@Override
	protected void configure() {
		bind(FileResolver.class).toInstance(RESOLVER);
		
		requestStaticInjection(Construct.class);
	}

}

