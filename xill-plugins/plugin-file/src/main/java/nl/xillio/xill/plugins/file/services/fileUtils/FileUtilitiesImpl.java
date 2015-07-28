package nl.xillio.xill.plugins.file.services.fileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.google.inject.Singleton;

import nl.xillio.xill.api.errors.RobotRuntimeException;

@Singleton
public class FileUtilitiesImpl implements FileUtilities {

	@Override
	public void copy(final String source, final String target) {
		source.replaceAll("\\\\", "/");
		target.replaceAll("\\\\", "/");
		try {
			FileUtils.copyFile(new File(source), new File(target));
		} catch (IOException e) {
			throw new RobotRuntimeException(e.getLocalizedMessage());
		}
	}

	@Override
	public void createFolder(final String uriVar) {
		String uri = uriVar.replaceAll("\\\\", "/");

		try {
			new File(uri).mkdirs();
		} catch (Exception e) {
			throw new RobotRuntimeException(e.getLocalizedMessage());
		}
	}

	@Override
	public boolean exists(final String uri) {
		return Files.exists(Paths.get(uri));
	}

	@Override
	public long size(final String uri) {
		File file = new File(uri);

		if (!file.exists()) {
			throw new RobotRuntimeException("File does not exist: " + uri);
		}

		if (file.isDirectory()) {
			return 0;
		}
		return file.length();
	}

	@Override
	public List<String> listFolders(final String uri, final boolean recursive, final boolean showAccesVar) {
		// TODO:: do this class later when we know how.
		if (uri.isEmpty()) {
			return null;
		}
		Path dir = Paths.get(uri);
		if (Files.notExists(dir)) {
			throw new RobotRuntimeException("No such path: " + uri);
		}
		if (!Files.isDirectory(dir)) {
			throw new RobotRuntimeException("Not a path: " + uri);
		}

		// try {
		// return new FileListVariable(dir, isRecursive, FileListType.FOLDERS, showAccess);
		return null;
		// } catch (IOException e) {
		// throw new RobotRuntimeException("No access to directory" + e.getMessage());
		// }
	}

	@Override
	public void delete(final String uri) {
		String fileuri = uri.replaceAll("\\\\", "/");
		File currentFile = new File(fileuri);

		if (currentFile.exists() && !currentFile.delete()) {
			try {
				FileUtils.deleteDirectory(currentFile);
			} catch (Exception e) {
				throw new RobotRuntimeException("deletion failed" + e.getMessage());
			}
		}
	}

	@Override
	public String getText(String uri) {
		 BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(uri));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 String result = "";
     try {
    	 	 String line = br.readLine();
         result = line;
         while (line != null) {
        	   line = br.readLine();
             result+= "\t" + line;
         }       
     } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
         try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
         
     }
     
     return result;
 }
	

}
