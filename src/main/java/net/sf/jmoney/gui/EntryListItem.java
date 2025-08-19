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

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import net.sf.jmoney.*;
import net.sf.jmoney.model.*;

/**
 * A component (panel) used by AccountPanel.entryList to show the entries.
 */
public class EntryListItem extends JPanel {
	static Color selectedEntryColor = new Color(153, 153, 204);
	static Color oddEntryColor = new Color(229, 229, 255);
	static Color evenEntryColor = Color.white;

	GridBagLayout thisGridBagLayout = new GridBagLayout();
	JTextField dateField = new JTextField();
	JTextField descriptionField = new JTextField();
	JTextField debitField = new JTextField();
	JTextField creditField = new JTextField();
	JTextField balanceField = new JTextField();
	Border rightLineBorder;
	Border leftLineBorder;
	VerySimpleDateFormat datef;
	Account account;

	/**
	 * Creates a new view based on an account.
	 */
	public EntryListItem() {
		try {
			jbInit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void setDateFormat(VerySimpleDateFormat df) {
		datef = df;
	}

	/**
	 * Sets the account.
	 */
	public void setAccount(Account acc) {
		this.account = acc;
	}

	/**
	 * Sets the model.
	 */
	public void setModel(Entry entry) {
		dateField.setText(datef.format(entry.getDate()));
		descriptionField.setText(entry.getDescription());
		formatAmount(entry.getAmount());
	}

	/**
	 * Sets the balance.
	 */
	public void setBalance(long balance) {
		balanceField.setText(account.formatAmount(balance));
		balanceField.setDisabledTextColor(balance < 0 ? Color.red : Color.black);
	}

	/**
	 * Sets the look.
	 */
	public void setLook(boolean isSelected, int index) {
		if (isSelected)
			setBackground(selectedEntryColor);
		else if (index % 2 == 1)
			setBackground(oddEntryColor);
		else
			setBackground(evenEntryColor);
	}

	/**
	 * Auxiliary method to format the amount.
	 */
	private void formatAmount(long amount) {
		if (amount < 0) {
			debitField.setText(account.formatAmount(-amount));
			creditField.setText(null);
		} else {
			debitField.setText(null);
			creditField.setText(account.formatAmount(amount));
		}
	}

	/**
	 * Build the GUI.
	 */
	private void jbInit() throws Exception {
		setLayout(thisGridBagLayout);
		rightLineBorder = BorderFactory.createMatteBorder(0, 0, 0, 1, Color.gray);
		leftLineBorder = BorderFactory.createMatteBorder(0, 1, 0, 0, Color.gray);
		dateField.setEnabled(false);
		dateField.setBorder(rightLineBorder);
		dateField.setOpaque(false);
		dateField.setPreferredSize(dateField.getMinimumSize());
		dateField.setDisabledTextColor(Color.black);
		descriptionField.setEnabled(false);
		descriptionField.setBorder(rightLineBorder);
		descriptionField.setOpaque(false);
		descriptionField.setPreferredSize(descriptionField.getMinimumSize());
		descriptionField.setDisabledTextColor(Color.black);
		debitField.setBorder(rightLineBorder);
		debitField.setOpaque(false);
		debitField.setEnabled(false);
		debitField.setPreferredSize(debitField.getMinimumSize());
		debitField.setDisabledTextColor(Color.black);
		debitField.setHorizontalAlignment(SwingConstants.RIGHT);
		creditField.setEnabled(false);
		creditField.setBorder(rightLineBorder);
		creditField.setOpaque(false);
		creditField.setPreferredSize(creditField.getMinimumSize());
		creditField.setDisabledTextColor(Color.black);
		creditField.setHorizontalAlignment(SwingConstants.RIGHT);
		balanceField.setEnabled(false);
		balanceField.setBorder(leftLineBorder);
		balanceField.setOpaque(false);
		balanceField.setPreferredSize(balanceField.getMinimumSize());
		balanceField.setDisabledTextColor(Color.black);
		balanceField.setHorizontalAlignment(SwingConstants.RIGHT);
		add(dateField, new GridBagConstraints(1, 0, 1, 1, 10.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0, 2, 0, 0), 1, 0));
		add(descriptionField, new GridBagConstraints(2, 0, 2, 1, 30.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.BOTH, new Insets(0, 2, 0, 0), 0, 0));
		add(debitField, new GridBagConstraints(4, 0, 1, 1, 10.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 2, 0, 0), 0, 0));
		add(creditField, new GridBagConstraints(5, 0, 1, 1, 10.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 2, 0, 0), 0, 0));
		add(balanceField, new GridBagConstraints(6, 0, 1, 1, 10.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 1, 0, 0), 0, 0));
	}

}
