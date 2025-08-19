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
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import net.sf.jmoney.Constants;

public class EntryListItemLabelsExtended extends EntryListItemLabels {
	JButton categoryButton = new JButton();

	public EntryListItemLabelsExtended() {
		try {
			jbInit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected JButton getButton(String field) {
		if (field.equals("Category"))
			return categoryButton;
		else
			return super.getButton(field);
	}

	private void jbInit() throws Exception {

		categoryButton.setText(Constants.LANGUAGE.getString("Entry.category"));
		categoryButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				toggleEntrySort(categoryButton, "Category");
			}
		});
		initButton(categoryButton);
		add(categoryButton, new GridBagConstraints(2, 1, 1, 1, 30.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
	}
}
