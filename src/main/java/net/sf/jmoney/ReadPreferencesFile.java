package net.sf.jmoney;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.swing.filechooser.FileSystemView;

public class ReadPreferencesFile {
	private UserProperties userProperties = new UserProperties();
	Properties properties = new Properties();

	public ReadPreferencesFile() {
		try {
			this.userProperties = readPropertiesFile();
		} catch (IOException e) {
			System.err.println("IO Exception occured: " + e.getMessage());
		}
	}

	public UserProperties getUserProperties() {
		return userProperties;
	}

	public Properties getProperties() {
		return properties;
	}

	private UserProperties readPropertiesFile() throws IOException {
		// read properties
		File propertiesFile = null;
		File homeDir = FileSystemView.getFileSystemView().getDefaultDirectory();
		File jMoneyDir = new File(homeDir, ".jmoney");
		if (!jMoneyDir.exists()) {
			jMoneyDir.mkdir();
		}
		if (jMoneyDir.isDirectory()) {
			propertiesFile = new File(jMoneyDir, "preferences.txt");
			if (!propertiesFile.exists()) {
				propertiesFile.createNewFile();
			}
		}
		InputStream in = new FileInputStream(propertiesFile);
		properties.load(in);
		userProperties.setProperties(properties);
		return userProperties;
	}
}
