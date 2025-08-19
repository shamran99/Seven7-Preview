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
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sf.jmoney.Constants;
import net.sf.jmoney.model.Account;

public class AccountChooser extends JDialog {
	ResourceBundle language = Constants.LANGUAGE;
	JPanel rootPanel = new JPanel();
	JPanel commandPanel = new JPanel();
	JLabel instructionLabel = new JLabel();
	JScrollPane accountListScrollPane = new JScrollPane();
	JList accountList = new JList();
	JButton okButton = new JButton();
	JButton newAccountButton = new JButton();
	JButton cancelButton = new JButton();
	int status = Constants.CANCEL;
	JFrame parent;

	/**
	 * Dialog to choose an account.
	 */
	public AccountChooser(JFrame parent) {
		super(parent, Constants.LANGUAGE.getString("AccountChooser.title"), true);
		this.parent = parent;
		try {
			jbInit();
		} catch (Exception e) {
			e.printStackTrace();
		}
		pack();
	}

	public int showDialog(Vector accounts, String instruction) {
		return showDialog(accounts, instruction, false);
	}

	public int showDialog(Vector accounts, String instruction, boolean showNewButton) {
		accountList.setModel(new DefaultComboBoxModel(accounts));
		instructionLabel.setText(instruction);
		newAccountButton.setVisible(showNewButton);
		okButton.setEnabled(false);
		pack();
		setLocationRelativeTo(parent);
		show();
		return status;
	}

	public Account getSelectedAccount() {
		return (Account) accountList.getSelectedValue();
	}

	private void jbInit() throws Exception {
		// command panel
		// -----------------------------------------------------------
		// ok button
		okButton.setText(language.getString("Dialog.ok"));
		okButton.setEnabled(false);
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ok();
			}
		});
		// cancel button
		cancelButton.setText(language.getString("Dialog.cancel"));
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancel();
			}
		});
		// add components to command panel
		commandPanel.setLayout(new GridLayout(1, 2, 5, 0));
		this.setModal(true);
		commandPanel.add(okButton, null);
		commandPanel.add(cancelButton, null);

		// root panel
		// --------------------------------------------------------------
		// account list
		accountList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent event) {
				if (!event.getValueIsAdjusting() && !accountList.isSelectionEmpty()) {
					okButton.setEnabled(true);
				}
			}
		});
		accountListScrollPane.getViewport().add(accountList, null);
		// new account button
		newAccountButton.setText(language.getString("AccountChooser.newAccount"));
		newAccountButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				newAccount();
			}
		});
		// add components to root panel
		rootPanel.setLayout(new GridBagLayout());
		rootPanel.add(instructionLabel, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(12, 12, 11, 11), 0, 0));
		rootPanel.add(accountListScrollPane, new GridBagConstraints(0, 1, 2, 1, 1.0, 1.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 12, 0, 12), 0, 0));
		rootPanel.add(newAccountButton, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTHWEST,
				GridBagConstraints.NONE, new Insets(17, 12, 11, 11), 0, 0));
		rootPanel.add(commandPanel, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.NONE, new Insets(17, 0, 11, 11), 0, 0));

		// this
		// --------------------------------------------------------------------
		getContentPane().add(rootPanel, BorderLayout.CENTER);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				cancel();
			}
		});
	}

	void newAccount() {
		status = Constants.NEW;
		dispose();
	}

	void ok() {
		status = Constants.OK;
		dispose();
	}

	void cancel() {
		status = Constants.CANCEL;
		dispose();
	}

}
