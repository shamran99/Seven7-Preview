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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.text.DecimalFormat;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.sf.jmoney.Constants;
import net.sf.jmoney.model.Account;

/**
 * A dialog to modify the account properties.
 */
public class TransferDialog extends JDialog {
	ResourceBundle language = Constants.LANGUAGE;
	JPanel rootPanel = new JPanel();
	JPanel propertiesPanel = new JPanel();
	JButton okButton = new JButton();
	JButton cancelButton = new JButton();
	JPanel commandPanel = new JPanel();
	JLabel fromAmountLabel = new JLabel();
	JLabel rateLabel = new JLabel();
	JLabel toAmountLabel = new JLabel();
	JTextField fromAmountField = new JTextField();
	JTextField rateField = new JTextField();
	JTextField toAmountField = new JTextField();
	Account fromAccount;
	Account toAccount;
	long fromAmount;
	long toAmount;
	long originalFromAmount;
	long originalToAmount;
	int status = Constants.CANCEL;

	/**
	 * Creates a new account properties dialog
	 */
	public TransferDialog(JFrame parent) {
		super(parent, Constants.LANGUAGE.getString("TransferDialog.title"));
		try {
			jbInit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int showDialog(Account from, Account to, long fromA, long toA) {
		if (fromA == 0) {
			toAmount = 0;
			return Constants.OK;
		}

		fromAccount = from;
		toAccount = to;
		fromAmount = Math.abs(fromA);
		toAmount = Math.abs(toA);
		originalFromAmount = fromA;
		originalToAmount = toA;

		fromAmountLabel.setText(from.getName());
		fromAmountField.setText(from.formatAmount(fromAmount));

		toAmountLabel.setText(to.getName());
		toAmountField.setText(to.formatAmount(toAmount));

		updateRateField();

		pack();
		setLocationRelativeTo(super.getParent());
		show();

		return status;
	}

	/**
	 * Returns the other amount.
	 */
	public long getOtherAmount() {
		return (originalFromAmount > 0) ? -toAmount : toAmount;
	}

	/**
	 * Returns the rate string.
	 */
	private void updateRateField() {
		if (fromAmount == 0) {
			rateField.setText("");
		} else {
			rateField.setText("" + getRateValue());
			rateChanged();
		}
	}

	/**
	 * @return
	 */
	private double getRateValue() {
		double rate;
		if (fromAccount.getCurrencyCode().equals("GBP") && toAccount.getCurrencyCode().equals("LKR")) {
			rate = getGBPtoLKRRate();
		} else if (fromAccount.getCurrencyCode().equals("LKR") && toAccount.getCurrencyCode().equals("GBP")) {
			rate = Double.valueOf(new DecimalFormat("#.#####").format(1.0 / getGBPtoLKRRate()));
		} else {
			rate = ((double) toAmount * fromAccount.getCurrency().getScaleFactor())
					/ (toAccount.getCurrency().getScaleFactor() * fromAmount);
		}
		return rate;

	}

	private double getGBPtoLKRRate() {
		double rate = 1.0;
		try {
			rate = Double.parseDouble(Constants.EXTERNAL_PROPERTY.getString("gbp.to.lkr"));
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this,
					"Error occured during GBP to LKR rate retrival. Details:- \n" + e.getMessage());
		}
		return rate;
	}

	/**
	 * Builds the GUI.
	 */
	private void jbInit() throws Exception {
		fromAmountField.setEnabled(false);
		fromAmountField.setColumns(15);
		// rate
		rateLabel.setText(language.getString("TransferDialog.exchangeRate"));
		rateField.setColumns(15);
		rateField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rateChanged();
			}
		});
		// to amount
		toAmountField.setColumns(15);
		toAmountField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				toAmountChanged();
			}
		});
		// add components to properties panel
		propertiesPanel.setLayout(new GridBagLayout());
		this.setModal(true);
		propertiesPanel.add(fromAmountLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(12, 12, 11, 12), 0, 0));
		propertiesPanel.add(rateLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(0, 12, 11, 12), 0, 0));
		propertiesPanel.add(toAmountLabel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(0, 12, 0, 12), 0, 0));
		propertiesPanel.add(fromAmountField, new GridBagConstraints(1, 1, 1, 1, 0.5, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(12, 0, 11, 11), 0, 0));
		propertiesPanel.add(rateField, new GridBagConstraints(1, 2, 1, 1, 0.5, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(2, 0, 11, 11), 0, 0));
		propertiesPanel.add(toAmountField, new GridBagConstraints(1, 3, 1, 1, 0.5, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 11), 0, 0));

		// command panel
		// -----------------------------------------------------------
		// ok button
		okButton.setText(language.getString("Dialog.ok"));
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
		commandPanel.add(okButton, null);
		commandPanel.add(cancelButton, null);

		// root panel
		// --------------------------------------------------------------
		rootPanel.setLayout(new GridBagLayout());
		rootPanel.add(propertiesPanel, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		rootPanel.add(commandPanel, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.SOUTHEAST,
				GridBagConstraints.NONE, new Insets(17, 12, 11, 11), 0, 0));

		// this
		// --------------------------------------------------------------------
		getContentPane().add(rootPanel);
		addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				cancel();
			}
		});
	}

	private void rateChanged() {
		try {
			double rate;
			rate = Double.parseDouble(rateField.getText());
			toAmount = Math.round(rate * fromAmount * fromAccount.getCurrency().getScaleFactor()
					/ toAccount.getCurrency().getScaleFactor());
			toAmountField.setText(toAccount.formatAmount(toAmount));
		} catch (Exception ex) {
			updateRateField();
		}
	}

	private void toAmountChanged() {
		toAmount = toAccount.parseAmount(toAmountField.getText());
		toAmountField.setText(toAccount.formatAmount(toAmount));
		updateRateField();
	}

	private void ok() {
		status = Constants.OK;
		dispose();
	}

	private void cancel() {
		toAmount = originalToAmount;
		status = Constants.CANCEL;
		dispose();
	}

}
