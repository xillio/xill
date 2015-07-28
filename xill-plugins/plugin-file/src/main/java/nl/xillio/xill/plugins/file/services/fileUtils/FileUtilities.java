package nl.xillio.xill.plugins.file.services.fileUtils;

import java.util.List;

import nl.xillio.xill.services.XillService;

public interface FileUtilities extends XillService  {

	public void copy(String source, String target);

	public void createFolder(String uri);
	
	public boolean exists(String uri);
	
	public long size(String uri);
	
	public List<String> listFolders(String uri, boolean recursive, boolean showAccesVar); //does not work yet
	
	public void delete(String uri);
	
	public String getText(String uri);
}
