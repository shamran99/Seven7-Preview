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

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JTextField;

import net.sf.jmoney.model.Entry;

/**
 * A component (panel) used by AccountPanel.entryList to show the entries.
 */
public class EntryListItemExtended extends EntryListItem {

	GridBagLayout thisGridBagLayout = new GridBagLayout();

	JTextField itemCode = new JTextField();
	JTextField categoryField = new JTextField();
	JTextField quantity = new JTextField();
	JTextField cost = new JTextField();
	JTextField fillerField4 = new JTextField();
	JTextField fillerField5 = new JTextField();
	JTextField fillerField6 = new JTextField();
	JTextField fillerField2 = new JTextField();

	/**
	 * Creates a new view based on an account.
	 */
	public EntryListItemExtended() {
		try {
			jbInit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Sets the model.
	 */
	public void setModel(Entry entry) {
		super.setModel(entry);

		itemCode.setText(entry.getItemCode());
		String quantityText = (entry.getQuantity() == 0) ? "" : String.valueOf(entry.getQuantity());
		String costText = (entry.getCost() == 0) ? "" : String.valueOf(entry.getCost());

		quantity.setText(quantityText);
		cost.setText(costText);
		categoryField.setText(entry.getFullCategoryName());
	}

	/**
	 * Build the GUI.
	 */
	private void jbInit() throws Exception {
		itemCode.setEnabled(false);
		itemCode.setBorder(rightLineBorder);
		itemCode.setOpaque(false);
		itemCode.setPreferredSize(itemCode.getMinimumSize());
		itemCode.setDisabledTextColor(Color.black);
		categoryField.setEnabled(false);
		categoryField.setBorder(rightLineBorder);
		categoryField.setOpaque(false);
		categoryField.setPreferredSize(categoryField.getMinimumSize());
		categoryField.setDisabledTextColor(Color.black);
		quantity.setEnabled(false);
		quantity.setBorder(rightLineBorder);
		quantity.setOpaque(false);
		quantity.setPreferredSize(quantity.getMinimumSize());
		quantity.setHorizontalAlignment(4);
		quantity.setDisabledTextColor(Color.blue);
		cost.setEnabled(false);
		cost.setBorder(rightLineBorder);
		cost.setOpaque(false);
		cost.setPreferredSize(cost.getMinimumSize());
		cost.setHorizontalAlignment(4);
		cost.setDisabledTextColor(Color.black);
		fillerField4.setBorder(rightLineBorder);
		fillerField4.setOpaque(false);
		fillerField5.setEnabled(false);
		fillerField5.setBorder(rightLineBorder);
		fillerField5.setOpaque(false);
		fillerField6.setEnabled(false);
		fillerField6.setBorder(leftLineBorder);
		fillerField6.setOpaque(false);
		fillerField2.setEnabled(false);
		fillerField2.setBorder(rightLineBorder);
		fillerField2.setOpaque(false);
		add(itemCode, new GridBagConstraints(1, 1, 1, 1, 10.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 2, 0, 0), 0, 0));
		add(categoryField, new GridBagConstraints(2, 1, 1, 1, 30.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 2, 0, 0), 0, 0));
		add(quantity, new GridBagConstraints(2, 2, 1, 1, 30.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0, 2, 0, 0), 0, 0));
		add(cost, new GridBagConstraints(1, 2, 1, 1, 10.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 2, 0, 0), 0, 0));
		add(fillerField2, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(0, 2, 0, 0), 0, 0));
		add(fillerField4, new GridBagConstraints(4, 1, 1, 2, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 2, 0, 0), 0, 0));
		add(fillerField5, new GridBagConstraints(5, 1, 1, 2, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 2, 0, 0), 0, 0));
		add(fillerField6, new GridBagConstraints(6, 1, 1, 2, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 1, 0, 0), 0, 0));
	}

}