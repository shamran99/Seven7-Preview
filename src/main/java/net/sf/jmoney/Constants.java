/*
 *
 *  JMoney - A Personal Finance Manager
 *  Copyright (c) 2002 Johann Gyger <johann.gyger@switzerland.org>
 *
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 */

package net.sf.jmoney;

import java.util.ResourceBundle;

import javax.swing.ImageIcon;

/**
 * A class that holds the global constants.
 */
public final class Constants {

	private Constants() {
		// To restrict creation of class objects
	}

	/**
	 * The name of the author.
	 */
	public static final String AUTHOR = "Shamran Siddique";

	/**
	 * The email address of the author.
	 */
	public static final String EMAIL = "shamran100@gmail.com";

	/**
	 * The reports default save format
	 */
	public static final String DEFAULT_SAVE_OPTION = "CSV";

	/**
	 * Copyright string.
	 */
	public static final String COPYRIGHT = "Copyright (C) 2002 " + AUTHOR + " <" + EMAIL + ">";

	/**
	 * Corresponding int for CANCEL.
	 */
	public static final int CANCEL = 0;

	/**
	 * Corresponding int for OK.
	 */
	public static final int OK = 1;

	/**
	 * Corresponding int for HELP.
	 */
	public static final int HELP = 2;

	/**
	 * Corresponding int for NEW.
	 */
	public static final int NEW = 3;

	public static final ImageIcon JMONEY_IMAGE = new ImageIcon(Constants.class.getResource("images/seven7_logo.png"));

	public static final ImageIcon ACCOUNTS_ICON = new ImageIcon(Constants.class.getResource("images/Accounts.gif"));

	public static final ImageIcon ACCOUNT_ICON = new ImageIcon(Constants.class.getResource("images/Account.gif"));

	public static final ImageIcon CATEGORY_ICON = new ImageIcon(Constants.class.getResource("images/Category.gif"));

	public static final ImageIcon ARROW_UP_ICON = new ImageIcon(Constants.class.getResource("images/ArrowUp.gif"));

	public static final ImageIcon ARROW_DOWN_ICON = new ImageIcon(Constants.class.getResource("images/ArrowDown.gif"));

	public static final ImageIcon NEW_ICON = new ImageIcon(Constants.class.getResource("images/jlfgr/New16.gif"));

	public static final ImageIcon OPEN_ICON = new ImageIcon(Constants.class.getResource("images/jlfgr/Open16.gif"));

	public static final ImageIcon SAVE_ICON = new ImageIcon(Constants.class.getResource("images/jlfgr/Save16.gif"));

	public static final ImageIcon SAVE_AS_ICON = new ImageIcon(
			Constants.class.getResource("images/jlfgr/SaveAs16.gif"));

	public static final ImageIcon PRINT_ICON = new ImageIcon(Constants.class.getResource("images/jlfgr/Print16.gif"));

	public static final ImageIcon IMPORT_ICON = new ImageIcon(Constants.class.getResource("images/jlfgr/Import16.gif"));

	public static final ImageIcon EXPORT_ICON = new ImageIcon(Constants.class.getResource("images/jlfgr/Export16.gif"));

	public static final ImageIcon UNDO_ICON = new ImageIcon(Constants.class.getResource("images/jlfgr/Undo16.gif"));

	public static final ImageIcon REDO_ICON = new ImageIcon(Constants.class.getResource("images/jlfgr/Redo16.gif"));

	public static final ImageIcon CUT_ICON = new ImageIcon(Constants.class.getResource("images/jlfgr/Cut16.gif"));

	public static final ImageIcon COPY_ICON = new ImageIcon(Constants.class.getResource("images/jlfgr/Copy16.gif"));

	public static final ImageIcon PASTE_ICON = new ImageIcon(Constants.class.getResource("images/jlfgr/Paste16.gif"));

	public static final ImageIcon FIND_ICON = new ImageIcon(Constants.class.getResource("images/jlfgr/Find16.gif"));

	public static final ImageIcon FIND_AGAIN_ICON = new ImageIcon(
			Constants.class.getResource("images/jlfgr/FindAgain16.gif"));

	public static final ImageIcon PREFERENCES_ICON = new ImageIcon(
			Constants.class.getResource("images/jlfgr/Preferences16.gif"));

	public static final ImageIcon ABOUT_ICON = new ImageIcon(Constants.class.getResource("images/jlfgr/About16.gif"));

	public static final ImageIcon SYNC_ICON = new ImageIcon(Constants.class.getResource("images/jlfgr/Sync16.png"));

	/**
	 * The language resource bundle.
	 */
	public static ResourceBundle LANGUAGE = ResourceBundle.getBundle("net.sf.jmoney.resources.Language");

	/**
	 * The external property resource bundle.
	 */
	public static ResourceBundle EXTERNAL_PROPERTY = new GetPropertyFile(LANGUAGE.getString("externalData.file.name"))
			.getResourceBundle();

	/**
	 * The general resource bundle.
	 */
	public static ResourceBundle GENERAL = ResourceBundle.getBundle("net.sf.jmoney.resources.General");

	/**
	 * Filename extension
	 */
	public static final String FILE_EXTENSION = ".jmx";

	/**
	 * File filter name
	 */
	public static final String FILE_FILTER_NAME = "JMoney Files (*.jmx)";

	/**
	 * The FilePath of Data File
	 */
	public static final String FILE_PATH = LANGUAGE.getString("dataFile.location");

}
