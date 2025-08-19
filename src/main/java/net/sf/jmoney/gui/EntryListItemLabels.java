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
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import net.sf.jmoney.Constants;
import net.sf.jmoney.UserProperties;

public class EntryListItemLabels extends JPanel {
	JButton dateButton = new JButton();
	JButton descriptionButton = new JButton();
	JButton debitButton = new JButton();
	JButton creditButton = new JButton();
	JButton balanceButton = new JButton();
	JLabel dummyLabel = new JLabel(" ");
	Insets buttonInsets = new Insets(0, 1, 0, 1);

	UserProperties userProperties;
	JButton orderButton;
	boolean ascending;

	public EntryListItemLabels() {
		try {
			jbInit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public UserProperties getUserProperties() {
		return userProperties;
	}

	public void setUserProperties(UserProperties p) {
		userProperties = p;
	}

	public void setEntryOrder(String field, String order) {
		ascending = true;
		if (order.equals("Descending"))
			ascending = false;

		setSortButton(getButton(field));
	}

	protected JButton getButton(String field) {
		if (field.equals("Date"))
			return dateButton;
		else if (field.equals("Description"))
			return descriptionButton;
		else
			return null;
	}

	protected void initButton(JButton b) {
		b.setHorizontalTextPosition(SwingConstants.LEFT);
		b.setHorizontalAlignment(SwingConstants.LEFT);
		b.setBorderPainted(false);
		b.setFocusable(false);
		b.setMargin(buttonInsets);
		b.setContentAreaFilled(false);
		b.setMinimumSize(dummyLabel.getMinimumSize());
		b.setPreferredSize(dummyLabel.getPreferredSize());
	}

	protected void setSortButton(JButton button) {
		if (orderButton != null) {
			orderButton.setIcon(null);
			orderButton = null;
		}
		if (button != null) {
			orderButton = button;
			orderButton.setIcon(ascending ? Constants.ARROW_UP_ICON : Constants.ARROW_DOWN_ICON);
		}
	}

	protected void toggleEntrySort(JButton source, String field) {
		if (orderButton == source) {
			if (ascending)
				userProperties.setEntryOrder(field, "Descending");
			else
				userProperties.setEntryOrder("Creation", "Ascending");
		} else {
			userProperties.setEntryOrder(field, "Ascending");
		}
	}

	private void jbInit() throws Exception {
		dateButton.setText(Constants.LANGUAGE.getString("Entry.date"));
		dateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				toggleEntrySort(dateButton, "Date");
			}
		});
		initButton(dateButton);

		descriptionButton.setText(Constants.LANGUAGE.getString("Entry.description"));
		descriptionButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				toggleEntrySort(descriptionButton, "Description");
			}
		});
		initButton(descriptionButton);

		debitButton.setText(Constants.LANGUAGE.getString("Entry.debit"));
		initButton(debitButton);

		creditButton.setText(Constants.LANGUAGE.getString("Entry.credit"));
		initButton(creditButton);

		balanceButton.setText(Constants.LANGUAGE.getString("Entry.balance"));
		initButton(balanceButton);

		setLayout(new GridBagLayout());
		add(dateButton, new GridBagConstraints(1, 0, 1, 1, 10.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(5, 0, 0, 0), 0, 0));
		add(descriptionButton, new GridBagConstraints(2, 0, 1, 1, 30.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(5, 0, 0, 0), 0, 0));
		add(debitButton, new GridBagConstraints(4, 0, 1, 1, 10.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(5, 0, 0, 0), 0, 0));
		add(balanceButton, new GridBagConstraints(6, 0, 1, 1, 10.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(5, 0, 0, 12), 0, 0));
		add(creditButton, new GridBagConstraints(5, 0, 1, 1, 10.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.BOTH, new Insets(5, 0, 0, 0), 0, 0));
	}

}
