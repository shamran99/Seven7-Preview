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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ResourceBundle;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import net.sf.jmoney.Constants;
import net.sf.jmoney.model.Account;
import net.sf.jmoney.model.Session;

/**
 * A dialog to modify the account properties.
 */
public class AccountPropertiesPanel extends JPanel {
	ResourceBundle language = Constants.LANGUAGE;
	JFrame parent;
	Account account;
	net.sf.jmoney.Currency currency;
	long startBalance = 0;
	Long minBalance;
	JLabel commentLabel = new JLabel();
	JLabel startBalanceLabel = new JLabel();
	JLabel minBalanceLabel = new JLabel();
	JLabel currencyLabel = new JLabel();
	JTextField abbrevationField = new JTextField();
	JLabel nameLabel = new JLabel();
	JComboBox currencyBox = new JComboBox(net.sf.jmoney.Currency.getAvailableCurrencies());
	JLabel abbrevationLabel = new JLabel();
	JScrollPane commentAreaScrollPane = new JScrollPane();
	JTextField accountNumberField = new JTextField();
	JTextField startBalanceField = new JTextField();
	JTextField minBalanceField = new JTextField();
	JLabel accountNumberLabel = new JLabel();
	JTextField nameField = new JTextField();
	JPanel fillPanel = new JPanel();
	JTextArea commentArea = new JTextArea();
	boolean currencyBoxEventsEnabled = true;
	Session session;

	/**
	 * Creates a new account properties dialog
	 */
	public AccountPropertiesPanel() {
		try {
			jbInit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public void setModel(Account account) {
		this.account = account;

		currency = account.getCurrency();
		startBalance = account.getStartBalance();
		minBalance = account.getMinBalance();

		nameField.setText(account.getName());
		setBalanceFields();
		currencyBox.setSelectedItem(currency);
		currencyBoxEventsEnabled = true;
		currencyBox.setEnabled(account.getEntries().size() == 0);
		accountNumberField.setText(account.getAccountNumber());
		abbrevationField.setText(account.getAbbrevation());
		commentArea.setText(account.getComment());
	}

	private void jbInit() throws Exception {
		startBalanceLabel.setText(language.getString("AccountPropertiesPanel.startBalance"));
		commentLabel.setText("Details");

		minBalanceLabel.setText(language.getString("AccountPropertiesPanel.minBalance"));
		currencyLabel.setText(language.getString("AccountPropertiesPanel.currency"));
		abbrevationField.setColumns(15);
		abbrevationField.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateAbbrevation();
			}
		});
		abbrevationField.addFocusListener(new java.awt.event.FocusAdapter() {
			public void focusLost(FocusEvent e) {
				updateAbbrevation();
			}
		});
		nameLabel.setText(language.getString("AccountPropertiesPanel.name"));
		currencyBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateCurrency();
			}
		});
		abbrevationLabel.setText("Subject");
		commentAreaScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		setLayout(new GridBagLayout());
		accountNumberField.setColumns(15);
		accountNumberField.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateAccountNumber();
			}
		});
		accountNumberField.addFocusListener(new java.awt.event.FocusAdapter() {
			public void focusLost(FocusEvent e) {
				updateAccountNumber();
			}
		});
		startBalanceField.setColumns(15);
		startBalanceField.addFocusListener(new java.awt.event.FocusAdapter() {
			public void focusLost(FocusEvent e) {
				updateStartBalance();
			}
		});
		startBalanceField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateStartBalance();
			}
		});
		minBalanceField.setColumns(15);
		minBalanceField.addFocusListener(new java.awt.event.FocusAdapter() {
			public void focusLost(FocusEvent e) {
				updateMinBalance();
			}
		});
		minBalanceField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateMinBalance();
			}
		});
		accountNumberLabel.setText(language.getString("AccountPropertiesPanel.accountNumber"));
		nameField.setColumns(30);
		nameField.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateName();
			}
		});
		nameField.addFocusListener(new java.awt.event.FocusAdapter() {
			public void focusLost(FocusEvent e) {
				updateName();
			}
		});
		commentArea.setLineWrap(true);
		commentArea.setColumns(25);
		commentArea.setRows(5);
		commentArea.addFocusListener(new java.awt.event.FocusAdapter() {
			public void focusLost(FocusEvent e) {
				updateComment();
			}
		});
		currencyBox.setMinimumSize(new Dimension(21, 21));
		add(nameLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
				new Insets(12, 12, 11, 12), 0, 0));
		add(accountNumberLabel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(0, 12, 11, 12), 0, 0));
		add(startBalanceLabel, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(0, 12, 11, 12), 0, 0));
		add(minBalanceLabel, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(0, 12, 11, 12), 0, 0));
		add(abbrevationLabel, new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(0, 12, 11, 12), 0, 0));
		add(currencyLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(0, 12, 11, 12), 0, 0));
		add(nameField, new GridBagConstraints(1, 0, 3, 1, 1.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(12, 0, 11, 11), 0, 0));
		add(currencyBox, new GridBagConstraints(1, 2, 1, 1, 0.5, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(0, 0, 11, 11), 0, 0));
		add(accountNumberField, new GridBagConstraints(1, 3, 1, 1, 0.5, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(0, 0, 11, 11), 0, 0));
		add(startBalanceField, new GridBagConstraints(1, 4, 1, 1, 0.5, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(0, 0, 11, 11), 0, 0));
		add(minBalanceField, new GridBagConstraints(1, 5, 1, 1, 0.5, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(0, 0, 11, 11), 0, 0));
		add(abbrevationField, new GridBagConstraints(1, 6, 1, 1, 0.5, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(0, 0, 11, 11), 0, 0));
		add(fillPanel, new GridBagConstraints(2, 2, 1, 5, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0, 0, 0, 0), 0, 0));
		add(commentLabel, new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(2, 12, 0, 12), 0, 0));
		add(commentAreaScrollPane, new GridBagConstraints(1, 7, 2, 1, 0.0, 1.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 0, 11, 11), 0, 0));
		commentAreaScrollPane.setViewportView(commentArea);

	}

	private void setBalanceFields() {
		startBalanceField.setText(account.formatAmount(startBalance));
		minBalanceField.setText(minBalance == null ? "" : account.formatAmount(minBalance.longValue()));
	}

	void updateName() {
		account.setName(nameField.getText());
	}

	void updateCurrency() {
		if (currencyBoxEventsEnabled) {
			currency = (net.sf.jmoney.Currency) currencyBox.getSelectedItem();
			account.setCurrencyCode(currency.getCode());
			setBalanceFields();
		}
	}

	void updateAccountNumber() {
		account.setAccountNumber(accountNumberField.getText());
	}

	void updateStartBalance() {
		startBalance = account.parseAmount(startBalanceField.getText());
		setBalanceFields();
		account.setStartBalance(startBalance);
	}

	void updateMinBalance() {
		try {
			NumberFormat.getNumberInstance().parse(minBalanceField.getText());
			minBalance = new Long(account.parseAmount(minBalanceField.getText()));
		} catch (ParseException pex) {
			minBalance = null;
		}
		setBalanceFields();
	}

	void updateAbbrevation() {
		account.setAbbrevation(abbrevationField.getText());
	}

	void updateComment() {
		account.setComment(commentArea.getText());
	}

}
