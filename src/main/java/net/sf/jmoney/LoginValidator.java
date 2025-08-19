package net.sf.jmoney;

import java.util.Arrays;

public class LoginValidator {
	private String username;
	private char[] password;

	public LoginValidator(String username, char[] password) {
		this.username = username;
		this.password = password;
	}

	public boolean validate() {
		if (username.equals("admin")) {
			if (Arrays.equals("admin".toCharArray(), password)) {
				return true;
			}
		}
		return false;
	}
}
