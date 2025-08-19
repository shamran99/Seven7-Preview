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

package net.sf.jmoney.gui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;

import net.sf.jmoney.Constants;
import net.sf.jmoney.UserProperties;
import net.sf.jmoney.VerySimpleDateFormat;

public class PreferencesDialog extends JDialog {

	/* entry style names in english (for preferences file) */
	static final String[] ENTRY_STYLES_PREF = { "Simple", "Extended" };

	/* entry style names depending on current language */
	static final String[] ENTRY_STYLES_LANG = { Constants.LANGUAGE.getString("PreferencesDialog.entryStyle.simple"),
			Constants.LANGUAGE.getString("PreferencesDialog.entryStyle.extended") };

	MainFrame parent;
	UIManager.LookAndFeelInfo[] lafInfos;
	int status;
	JPanel rootPanel = new JPanel();
	JPanel commonPanel = new JPanel();
	JComboBox lafBox = new JComboBox();
	JTabbedPane tabbedPane = new JTabbedPane();
	GridBagLayout gridBagLayout1 = new GridBagLayout();
	JPanel controlPanel = new JPanel();
	JButton applyButton = new JButton();
	JButton cancelButton = new JButton();
	JButton okButton = new JButton();
	GridLayout gridLayout1 = new GridLayout();
	JLabel lafLabel = new JLabel();
	GridBagLayout gridBagLayout2 = new GridBagLayout();
	JLabel dateFormatLabel = new JLabel();
	JComboBox dateFormatBox = new JComboBox(VerySimpleDateFormat.DATE_PATTERNS);
	JLabel defaultCurrencyLabel = new JLabel();
	JComboBox defaultCurrencyBox = new JComboBox(net.sf.jmoney.Currency.getAvailableCurrencies());
	UserProperties userProperties;
	private JLabel entryStyleLabel = new JLabel();
	private JComboBox entryStyleBox = new JComboBox(ENTRY_STYLES_LANG);

	public PreferencesDialog(MainFrame parent) {
		super(parent, "", true);
		// Register New Look and Feels
		registerNewLookAndFeels();
		setTitle(Constants.LANGUAGE.getString("PreferencesDialog.title"));
		this.parent = parent;
		try {
			jbInit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * This is to register new look and feels. Make sure, you have installed
	 * jars for the look and feel.
	 */
	private void registerNewLookAndFeels() {
		UIManager.installLookAndFeel("Jtattoo", "com.jtattoo.plaf.smart.SmartLookAndFeel");
		UIManager.installLookAndFeel("Acryl", "com.jtattoo.plaf.acryl.AcrylLookAndFeel");
		UIManager.installLookAndFeel("Fast", "com.jtattoo.plaf.fast.FastLookAndFeel");
	}

	public int showDialog(UserProperties p) {
		userProperties = p;

		lafInfos = UIManager.getInstalledLookAndFeels();
		String[] lafNames = new String[lafInfos.length];
		for (int i = 0; i < lafInfos.length; i++)
			lafNames[i] = lafInfos[i].getName();
		lafBox.setModel(new DefaultComboBoxModel(lafNames));

		lafBox.setSelectedItem(UIManager.getLookAndFeel().getName());
		dateFormatBox.setSelectedItem(userProperties.getDateFormat());
		defaultCurrencyBox
				.setSelectedItem(net.sf.jmoney.Currency.getCurrencyForCode(userProperties.getDefaultCurrency()));
		String style = userProperties.getEntryStyle();
		if (style.equals(ENTRY_STYLES_PREF[0]))
			entryStyleBox.setSelectedIndex(0);
		else
			entryStyleBox.setSelectedIndex(1);

		pack();
		setLocationRelativeTo(parent);
		show();
		return status;
	}

	private void jbInit() throws Exception {
		rootPanel.setLayout(gridBagLayout1);
		applyButton.setText(Constants.LANGUAGE.getString("Dialog.apply"));
		applyButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				apply();
			}
		});
		cancelButton.setText(Constants.LANGUAGE.getString("Dialog.cancel"));
		cancelButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancel();
			}
		});
		okButton.setText(Constants.LANGUAGE.getString("Dialog.ok"));
		okButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ok();
			}
		});
		controlPanel.setLayout(gridLayout1);
		gridLayout1.setHgap(5);
		lafLabel.setText(Constants.LANGUAGE.getString("PreferencesDialog.lookAndFeel"));
		commonPanel.setLayout(gridBagLayout2);
		dateFormatLabel.setText(Constants.LANGUAGE.getString("PreferencesDialog.dateFormat"));
		defaultCurrencyLabel.setText(Constants.LANGUAGE.getString("PreferencesDialog.defaultCurrency"));
		this.setModal(true);
		entryStyleLabel.setText(Constants.LANGUAGE.getString("PreferencesDialog.entryStyle"));
		commonPanel.add(lafLabel, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(12, 12, 0, 0), 0, 0));
		this.getContentPane().add(rootPanel, BorderLayout.CENTER);
		rootPanel.add(tabbedPane, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		tabbedPane.add(commonPanel, Constants.LANGUAGE.getString("PreferencesDialog.common"));
		commonPanel.add(lafBox, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(12, 12, 0, 11), 0, 0));
		commonPanel.add(dateFormatLabel, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(11, 12, 0, 11), 0, 0));
		commonPanel.add(dateFormatBox, new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(11, 12, 0, 11), 0, 0));
		rootPanel.add(controlPanel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.NONE, new Insets(17, 12, 11, 11), 0, 0));
		controlPanel.add(okButton, null);
		controlPanel.add(cancelButton, null);
		controlPanel.add(applyButton, null);
		commonPanel.add(defaultCurrencyLabel, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(11, 12, 0, 0), 0, 0));
		commonPanel.add(defaultCurrencyBox, new GridBagConstraints(2, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(12, 12, 0, 11), 0, 0));
		commonPanel.add(entryStyleLabel, new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(11, 12, 11, 0), 0, 0));
		commonPanel.add(entryStyleBox, new GridBagConstraints(2, 4, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(12, 12, 11, 11), 0, 0));
	}

	void apply() {
		userProperties.setLookAndFeel(lafInfos[lafBox.getSelectedIndex()].getClassName());
		userProperties.setDateFormat((String) dateFormatBox.getSelectedItem());
		userProperties.setDefaultCurrency(((net.sf.jmoney.Currency) defaultCurrencyBox.getSelectedItem()).getCode());
		userProperties.setEntryStyle(ENTRY_STYLES_PREF[entryStyleBox.getSelectedIndex()]);
	}

	void ok() {
		apply();
		status = Constants.OK;
		dispose();
	}

	void cancel() {
		status = Constants.CANCEL;
		dispose();
	}

}
