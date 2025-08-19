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

import javax.swing.*;
import java.awt.*;
import javax.swing.border.*;

class AccountOverviewPanel extends JPanel {
	private GridLayout gridLayout1 = new GridLayout();
	private JPanel accountsPanel = new JPanel();
	private JPanel jPanel2 = new JPanel();
	private JPanel jPanel3 = new JPanel();
	private JPanel chartPanel = new JPanel();
	private JList jList1 = new JList();
	private BorderLayout borderLayout1 = new BorderLayout();
	private JPanel jPanel1 = new JPanel();
	private JLabel jLabel1 = new JLabel();
	private GridLayout gridLayout2 = new GridLayout();
	private JLabel jLabel2 = new JLabel();
	private TitledBorder titledBorder1;
	private Border border1;
	private JLabel jLabel3 = new JLabel();
	private BorderLayout borderLayout2 = new BorderLayout();

	public AccountOverviewPanel() {
		try {
			jbInit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void jbInit() throws Exception {
		titledBorder1 = new TitledBorder("");
		border1 = BorderFactory.createEmptyBorder(5, 5, 5, 5);
		this.setLayout(gridLayout1);
		gridLayout1.setColumns(2);
		gridLayout1.setRows(2);
		accountsPanel.setLayout(borderLayout1);
		jLabel1.setText("Sum");
		jPanel1.setLayout(gridLayout2);
		jLabel2.setHorizontalAlignment(SwingConstants.RIGHT);
		jLabel2.setText("jLabel2");
		jPanel1.setBorder(border1);
		jLabel3.setHorizontalAlignment(SwingConstants.CENTER);
		jLabel3.setText("Here comes a chart");
		chartPanel.setLayout(borderLayout2);
		this.add(accountsPanel, null);
		this.add(chartPanel, null);
		this.add(jPanel3, null);
		this.add(jPanel2, null);
		accountsPanel.add(jList1, BorderLayout.CENTER);
		accountsPanel.add(jPanel1, BorderLayout.SOUTH);
		jPanel1.add(jLabel1, null);
		jPanel1.add(jLabel2, null);
		chartPanel.add(jLabel3, BorderLayout.CENTER);
	}
}
