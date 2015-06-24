package nl.xillio.migrationtool.dialogs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.util.Pair;

import javax.xml.bind.DatatypeConverter;

import nl.xillio.migrationtool.gui.ProjectPane;
import nl.xillio.sharedlibrary.settings.SettingsHandler;
import nl.xillio.xill.api.Xill;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

/**
 * A dialog to upload an item to the server.
 */
public class UploadToServerDialog extends FXMLDialog {
	private static final SettingsHandler settings = SettingsHandler.getSettingsHandler();
	private static final String SERVERHOST = "ServerHost", SERVERUSER = "ServerUsername", SERVERPASS = "ServerPassword";
	private static final CloseableHttpClient client = HttpClientBuilder.create().build();

	@FXML
	private TextField tfserver, tfusername, tfpassword;

	private String serverResult, usernameResult, passwordResult;
	private final TreeItem<Pair<File, String>> treeItem;
	private final ProjectPane projectPane;

	/**
	 * Default constructor.
	 *
	 * @param projectPane
	 *        the projectPane to which this dialog is attached to.
	 * @param treeItem
	 *        the tree item on which the item will be deleted
	 */
	public UploadToServerDialog(final ProjectPane projectPane, final TreeItem<Pair<File, String>> treeItem) {
		super("/fxml/dialogs/UploadToServer.fxml");
		this.treeItem = treeItem;
		this.projectPane = projectPane;

		setTitle("Upload to server");
		loadDefaults();
	}

	@FXML
	private void cancelBtnPressed(@SuppressWarnings("unused") final ActionEvent event) {
		close();
	}

	@FXML
	private void okayBtnPressed(@SuppressWarnings("unused") final ActionEvent event) {
		uploadToServer(treeItem, getServer(), getUsername(), getPassword(), treeItem.isLeaf());
		close();
	}

	private void loadDefaults() {
		tfserver.setText(settings.getSimpleSetting(SERVERHOST));
		tfusername.setText(settings.getSimpleSetting(SERVERUSER));
		tfpassword.setText(settings.getSimpleSetting(SERVERPASS));
	}

	private String getServer() {
		serverResult = tfserver.getText();
		while (serverResult.matches(".*//*$")) {
			serverResult = serverResult.replaceAll("/$", "");
		}
		settings.saveSimpleSetting(SERVERHOST, serverResult);
		return serverResult;
	}

	private String getUsername() {
		usernameResult = tfusername.getText();
		settings.saveSimpleSetting(SERVERUSER, usernameResult);
		return usernameResult;
	}

	private String getPassword() {
		passwordResult = tfpassword.getText();
		settings.saveSimpleSetting(SERVERPASS, passwordResult);
		return passwordResult;
	}

	private void uploadToServer(final TreeItem<Pair<File, String>> item, final String server, final String username, final String password, final boolean singleBot) {
		try {
			// First try and login to create a proper session
			HttpPost authPost = new HttpPost(server + "/j_security_check");
			List<NameValuePair> nameValuePairs = new ArrayList<>();
			nameValuePairs.add(new BasicNameValuePair("j_username", username));
			nameValuePairs.add(new BasicNameValuePair("j_password", password));
			authPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			try (CloseableHttpResponse httpResponse = client.execute(authPost)) {
				HttpEntity responseEntity = httpResponse.getEntity();
				String strResponse = EntityUtils.toString(responseEntity);
				EntityUtils.consume(responseEntity);
				if (strResponse.contains("Invalid credentials")) {
					throw new Exception("Invalid credentials provided!");
				}
			}
			// create project
			HttpPost securedResource = new HttpPost(server + "/addeditdeleteproject");
			nameValuePairs.clear();
			nameValuePairs.add(new BasicNameValuePair("data[action]", "add"));
			nameValuePairs.add(new BasicNameValuePair("data[project_path]", projectPane.getProject(item).getValue().getKey().getAbsolutePath()));
			nameValuePairs.add(new BasicNameValuePair("data[project_name]", item.getValue().getValue()));
			securedResource.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			try (CloseableHttpResponse httpResponse = client.execute(securedResource)) {
				HttpEntity responseEntity = httpResponse.getEntity();
				EntityUtils.consume(responseEntity);
			}

			// upload only selected item if bot, otherwise item + all its children
			if (singleBot) {
				try {
					String path = item.getValue().getKey().getAbsolutePath().replace("\\", "/").replaceAll("(.*)(/.*)", "$1");

					MultipartEntity entity = new MultipartEntity();
					entity.addPart("file", new FileBody(item.getValue().getKey()));
					entity.addPart("source", new StringBody("contenttools"));
					entity.addPart("path", new StringBody(path));

					DatatypeConverter.printBase64Binary((username + ":" + password).getBytes());

					HttpPost request = new HttpPost(server + "/addbot");
					request.setEntity(entity);
					try (CloseableHttpResponse response = client.execute(request)) {}
				} catch (Exception ex) {}
			} else {
				uploadChildrenToServer(item, server);
			}	
		} catch (Exception ex) {}
	}

	private void uploadChildrenToServer(final TreeItem<Pair<File, String>> parent, final String server) {
		String pathAbsolute;
		try {
			for (final TreeItem<Pair<File, String>> item : parent.getChildren()) {
				pathAbsolute = item.getValue().getKey().getAbsolutePath().replace("\\", "/");

				if (pathAbsolute.endsWith("." + Xill.FILE_EXTENSION)) {
					MultipartEntity entity = new MultipartEntity();
					entity.addPart("file", new FileBody(item.getValue().getKey()));
					entity.addPart("source", new StringBody("contenttools"));
					entity.addPart("path", new StringBody(pathAbsolute.replaceAll("(.*)(/.*)", "$1")));

					HttpPost request = new HttpPost(server + "/addbot");
					request.setEntity(entity);
					try (CloseableHttpResponse httpResponse = client.execute(request)) {
						EntityUtils.consumeQuietly(httpResponse.getEntity());
					}
				} else {
					uploadChildrenToServer(item, server);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
