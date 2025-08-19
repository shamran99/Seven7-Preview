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
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See thew
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 */

package net.sf.jmoney.gui;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import net.sf.jmoney.*;
import net.sf.jmoney.model.*;

public class AccountPanel extends JPanel {
	ResourceBundle language = Constants.LANGUAGE;
	JTabbedPane tabbedPane = new JTabbedPane();
	AccountEntriesPanel entriesPanel = new AccountEntriesPanel();
	AccountPropertiesPanel propertiesPanel = new AccountPropertiesPanel();
	BorderLayout borderLayout1 = new BorderLayout();

	public AccountPanel() {
		try {
			jbInit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	void setSession(Session session) {
		entriesPanel.setSession(session);
		propertiesPanel.setSession(session);
	}

	void setModel(Account account) {
		entriesPanel.setModel(account);
		propertiesPanel.setModel(account);
	}

	void jbInit() throws Exception {
		this.setLayout(borderLayout1);
		this.add(tabbedPane, BorderLayout.CENTER);
		tabbedPane.add(entriesPanel, language.getString("AccountPanel.entries"));
		tabbedPane.add(propertiesPanel, language.getString("AccountPanel.properties"));
	}

	/**
	 * Returns the entriesPanel.
	 * 
	 * @return AccountEntriesPanel
	 */
	public AccountEntriesPanel getEntriesPanel() {
		return entriesPanel;
	}

}
