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
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import net.sf.jmoney.*;
import net.sf.jmoney.model.*;

public class SplittedEntryDialog extends JDialog {
	ResourceBundle language = Constants.LANGUAGE;
	JPanel rootPanel = new JPanel();
	GridBagLayout gridBagLayout1 = new GridBagLayout();
	JPanel commandPanel = new JPanel();
	JButton closeButton = new JButton();
	JButton deleteButton = new JButton();
	CategoryComboBox categoryBox = new CategoryComboBox();
	DefaultCellEditor categoryCellEditor = new DefaultCellEditor(categoryBox);
	JPanel mainPanel = new JPanel();
	JScrollPane entryScrollPane = new JScrollPane();
	JTable entryTable = new JTable();
	GridBagLayout gridBagLayout3 = new GridBagLayout();
	JButton newButton = new JButton();
	GridLayout gridLayout1 = new GridLayout();
	SplittedEntry splittedEntry;
	EntryTableModel entryTableModel;
	JLabel totalLabel = new JLabel();
	JTextField totalField = new JTextField();
	Account account;
	long total;

	public SplittedEntryDialog(JFrame parent) {
		super(parent, "Splitted Entry", true);
		try {
			jbInit();
			pack();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public double showDialog(SplittedEntry entry, Session session, Account account) {
		splittedEntry = entry;
		this.account = account;
		entryTableModel = new EntryTableModel(splittedEntry, account);
		entryTable.setModel(entryTableModel);
		entryTable.getColumnModel().getColumn(1).setCellEditor(categoryCellEditor);

		categoryBox.setModel(session.getCategories());

		updateTotal();
		pack();
		setLocationRelativeTo(getParent());
		show();
		return total;
	}

	private void jbInit() throws Exception {
		rootPanel.setLayout(gridBagLayout1);
		commandPanel.setLayout(gridLayout1);
		closeButton.setText("Close");
		closeButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				close();
			}
		});
		deleteButton.setText("Delete");
		deleteButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				removeEntry();
			}
		});
		entryTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		mainPanel.setLayout(gridBagLayout3);
		mainPanel.setBorder(BorderFactory.createRaisedBevelBorder());
		newButton.setText("New");
		newButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				newEntry();
			}
		});
		gridLayout1.setHgap(5);
		totalLabel.setText("Total");
		totalField.setBorder(null);
		totalField.setOpaque(false);
		totalField.setEditable(false);
		totalField.setHorizontalAlignment(SwingConstants.RIGHT);
		commandPanel.add(newButton, null);
		commandPanel.add(deleteButton, null);
		commandPanel.add(closeButton, null);
		rootPanel.add(mainPanel, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		mainPanel.add(entryScrollPane, new GridBagConstraints(0, 0, 3, 1, 1.0, 1.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(12, 12, 11, 11), 0, 0));
		entryScrollPane.getViewport().add(entryTable, null);
		rootPanel.add(commandPanel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.NONE, new Insets(17, 0, 11, 11), 0, 0));
		this.getContentPane().add(rootPanel, BorderLayout.CENTER);
		mainPanel.add(totalLabel, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.NONE, new Insets(0, 12, 11, 11), 0, 0));
		mainPanel.add(totalField, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.NONE, new Insets(0, 0, 11, 11), 100, 0));
	}

	class EntryTableModel extends AbstractTableModel {
		SplittedEntry splittedEntry;
		Vector entries;
		Account account;
		String[] columnNames = { "Description", "Category", "Amount" };

		public EntryTableModel(SplittedEntry se, Account account) {
			splittedEntry = se;
			entries = se.getEntries();
			this.account = account;
		}

		public String getColumnName(int col) {
			return columnNames[col];
		}

		public int getRowCount() {
			return entries.size();
		}

		public int getColumnCount() {
			return 3;
		}

		public Object getValueAt(int row, int col) {
			Entry e = (Entry) entries.elementAt(row);
			switch (col) {
			case 0:
				return e.getDescription();
			case 1:
				return e.getCategory();
			case 2:
				return account.formatAmount(e.getAmount());
			default:
				return null;
			}
		}

		public boolean isCellEditable(int row, int col) {
			return true;
		}

		public void setValueAt(Object value, int row, int col) {
			Entry e = (Entry) entries.elementAt(row);
			switch (col) {
			case 0:
				e.setDescription((String) value);
				break;
			case 1:
				Category newCategory = (Category) value;
				if (newCategory == account)
					break;
				if (newCategory instanceof SimpleCategory) {
					e = e.toEntry();
					e.setCategory(newCategory);
					splittedEntry.setEntryAt(e, row);
				} else if (newCategory instanceof Account) {
					e = e.toDoubleEntry();
					e.setCategory(newCategory);
					splittedEntry.setEntryAt(e, row);
					((DoubleEntry) e).getOther().setCategory(account);
				}
				break;
			case 2:
				long a = account.parseAmount((String) value);
				e.setAmount(a);
				if (e instanceof DoubleEntry) {
					DoubleEntry de = (DoubleEntry) e;
					de.getOther().setAmount(-a);
				}
				updateTotal();
				break;
			default:
				break;
			}
			this.fireTableCellUpdated(row, col);
		}

		public void addEntry(Entry e) {
			splittedEntry.addEntry(e);
			this.fireTableRowsInserted(entries.size() - 1, entries.size() - 1);
		}

		public void removeEntryAt(int index) {
			splittedEntry.removeEntryAt(index);
			this.fireTableRowsDeleted(index, index);
		}

	} // EntryTableModel

	private void updateTotal() {
		total = 0;
		for (Enumeration e = entryTableModel.entries.elements(); e.hasMoreElements();) {
			total += ((Entry) e.nextElement()).getAmount();
		}
		;
		totalField.setText(account.formatAmount(total));
	}

	private void newEntry() {
		entryTableModel.addEntry(new Entry());
		updateTotal();
	}

	private void removeEntry() {
		entryTableModel.removeEntryAt(entryTable.getSelectedRow());
		updateTotal();
	}

	private void close() {
		dispose();
	}

}
