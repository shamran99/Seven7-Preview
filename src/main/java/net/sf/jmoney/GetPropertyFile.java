package net.sf.jmoney;

import java.io.FileInputStream;
import java.util.PropertyResourceBundle;
import java.util.regex.Matcher;

public class GetPropertyFile {
	private String file;

	public GetPropertyFile(String file) {
		String replaced = System.getProperty("user.dir").replaceAll("[/\\\\]+",
				Matcher.quoteReplacement(System.getProperty("file.separator")));
		this.file = replaced + "/" + file;
	}

	public PropertyResourceBundle getResourceBundle() {
		try {
			return new PropertyResourceBundle(new FileInputStream(file));
		} catch (Exception e) {
			return null;
		}
	}
}
