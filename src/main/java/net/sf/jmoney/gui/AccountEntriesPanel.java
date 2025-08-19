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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sf.jmoney.Constants;
import net.sf.jmoney.DateLabelFormatter;
import net.sf.jmoney.EntryComparator;
import net.sf.jmoney.EntryFilter;
import net.sf.jmoney.UserProperties;
import net.sf.jmoney.VerySimpleDateFormat;
import net.sf.jmoney.model.Account;
import net.sf.jmoney.model.Category;
import net.sf.jmoney.model.DoubleEntry;
import net.sf.jmoney.model.Entry;
import net.sf.jmoney.model.RootCategory;
import net.sf.jmoney.model.Session;
import net.sf.jmoney.model.SimpleCategory;
import net.sf.jmoney.model.SplitCategory;
import net.sf.jmoney.model.SplittedEntry;
import net.sf.jmoney.model.TransferCategory;
import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilDateModel;

/**
 * The delegate of an account.
 */
public class AccountEntriesPanel extends JPanel implements PropertyChangeListener {

	// gui components
	CategoryComboBox categoryBox = new CategoryComboBox();
	EntryCellRenderer entryCellRenderer = new EntryCellRenderer();
	EntryFilterPanel filterPanel = new EntryFilterPanel();
	EntryListItem entryRenderer = new EntryListItem();
	EntryListItemLabels entryListItemLabels = new EntryListItemLabels();
	JButton deleteButton = new JButton();
	JButton newButton = new JButton();
	JButton duplicateEntryBtn = new JButton(Constants.LANGUAGE.getString("DuplicateEntry.Button.Text"));
	JLabel totalLabel = new JLabel();
	JList entryList = new SelectionList();
	JPanel controlPanel = new JPanel();
	JPanel northPanel = new JPanel();
	JPanel totalPanel = new JPanel();
	JScrollPane entryListScrollPane = new JScrollPane();
	JTextField creditField = new JTextField();
	JTextField itemCode = new JTextField();
	JTextField quantity = new JTextField();
	JTextField cost = new JTextField();
	AccountChooser accountChooser;

	public void setAccountChooser(AccountChooser accountChooser) {
		this.accountChooser = accountChooser;
	}

	/**
	 * Adding calender
	 */
	UtilDateModel dateModel = new UtilDateModel(new Date());
	JDatePanelImpl datePanel = new JDatePanelImpl(dateModel);
	JDatePickerImpl dateField;

	JTextField debitField = new JTextField();
	JTextField descriptionField = new JTextField();
	JTextField emptyField = new JTextField();
	SplittedEntryDialog splittedEntryDialog;
	TransferDialog transferDialog;

	DateFormat nativeDate = DateFormat.getDateInstance();
	VerySimpleDateFormat userDate;
	SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");

	EntryFilter entryFilter = new EntryFilter();
	DefaultListModel entryListModel;
	EntryComparator entryComparator;

	long balances[];

	Entry selectedEntry;
	Entry newEntry = null;
	Entry foundEntry = null;

	Date last_date = new Date();

	Session session;
	Account account;

	/**
	 * Creates a new AccountPanel.
	 */
	public AccountEntriesPanel() {
		transferDialog = new TransferDialog(null);
		splittedEntryDialog = new SplittedEntryDialog(null);
		entryList.setPrototypeCellValue(Entry.PROTOTYPE);
		entryList.setCellRenderer(entryCellRenderer);
		try {
			dateField = new JDatePickerImpl(datePanel, new DateLabelFormatter());
			jbInit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public EntryListItemLabels getEntryListItemLabels() {
		return entryListItemLabels;
	}

	public void setDateFormat(String pattern) {
		userDate = new VerySimpleDateFormat(pattern);
		updateUI();
	}

	public void setEntryStyle(String es) {
		UserProperties p = entryListItemLabels.getUserProperties();

		if (es.equals("Simple")) {
			entryRenderer = new EntryListItem();
			northPanel.remove(entryListItemLabels);
			entryListItemLabels = new EntryListItemLabels();
		} else if (es.equals("Extended")) {
			entryRenderer = new EntryListItemExtended();
			northPanel.remove(entryListItemLabels);
			entryListItemLabels = new EntryListItemLabelsExtended();
		} else {
			System.err.println("preferences/entryStyle - invalid value " + es);
			return;
		}

		entryListItemLabels.setUserProperties(p);
		entryListItemLabels.setEntryOrder(p.getEntryOrderField(), p.getEntryOrder());
		northPanel.add(entryListItemLabels, BorderLayout.SOUTH);
		updateUI();
	}

	public void setEntryOrder(String field, String order) {
		entryComparator = (new EntryComparator()).getInstance(field, order);
		entryListItemLabels.setEntryOrder(field, order);
		if (account != null)
			updateEntryList();
	}

	/**
	 * Sets the session.
	 */
	public void setSession(Session session) {
		this.session = session;
		categoryBox.setModel(session.getCategories());
		EntryFilterPanel.setCategorySelector(session);
	}

	/**
	 * Sets the model of this account view.
	 */
	public void setModel(Account model) {
		if (account != null)
			model.removePropertyChangeListener(this);
		account = model;
		account.addPropertyChangeListener(this);
		clearSelection();
		updateEntryList();
	}

	public void propertyChange(PropertyChangeEvent event) {
		String propertyName = event.getPropertyName();
		if (propertyName.equals("locale")) {
			if (selectedEntry != null)
				updateDebitAndCreditField(selectedEntry.getAmount());
			entryList.updateUI();
		}
		if (propertyName.equals("startBalance") || propertyName.equals("entries") || event.getSource() == entryFilter) {
			updateEntryList();
		}
	}

	/**
	 * Overridden.
	 */
	public void updateUI() {
		super.updateUI();
		if (transferDialog != null)
			SwingUtilities.updateComponentTreeUI(transferDialog);
		if (entryList != null)
			entryList.setCellRenderer(new EntryCellRenderer());
	}

	/**
	 * Builds the GUI.
	 */
	private void jbInit() throws Exception {
		enableComponents(false);

		dateField.setPreferredSize(new Dimension(8, 22));
		dateField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateDate();
			}
		});

		descriptionField.setPreferredSize(new Dimension(4, 22));
		descriptionField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateDescription();
			}
		});
		descriptionField.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				updateDescription();
			}
		});
		descriptionField.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				completeEntry(e.getKeyChar());
			}
		});

		categoryBox.setPreferredSize(new Dimension(4, 22));
		categoryBox.addElementSelectionListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				updateCategory();
			}
		});

		debitField.setPreferredSize(new Dimension(4, 22));
		debitField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateDebit();
			}
		});
		debitField.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				updateDebit();
			}
		});

		creditField.setPreferredSize(new Dimension(4, 22));
		creditField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateCredit();
			}
		});
		creditField.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				updateCredit();
			}
		});

		newButton.setText(Constants.LANGUAGE.getString("AccountEntriesPanel.new"));
		newButton.setPreferredSize(new Dimension(4, 22));
		newButton.setMargin(new Insets(2, 2, 2, 2));
		newButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				newEntry();
			}
		});

		deleteButton.setText(Constants.LANGUAGE.getString("AccountEntriesPanel.delete"));
		deleteButton.setPreferredSize(new Dimension(4, 22));
		deleteButton.setMargin(new Insets(2, 3, 2, 3));
		deleteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteEntry();
			}
		});

		entryFilter.addPropertyChangeListener(this);
		filterPanel.setEntryFilter(entryFilter);

		entryList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		entryList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting())
					multipleEntrySelected();
			}
		});

		northPanel.setLayout(new BorderLayout());
		northPanel.add(filterPanel, BorderLayout.NORTH);
		northPanel.add(entryListItemLabels, BorderLayout.SOUTH);

		entryListScrollPane.setBorder(BorderFactory.createEtchedBorder());
		entryListScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		entryListScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		entryListScrollPane.setViewportView(entryList);

		GridBagLayout gbl_controlPanel = new GridBagLayout();
		gbl_controlPanel.rowHeights = new int[] { 36, 0 };
		controlPanel.setLayout(gbl_controlPanel);
		controlPanel.add(dateField, new GridBagConstraints(1, 0, 1, 1, 10.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(5, 2, 5, 5), 0, 0));
		controlPanel.add(descriptionField, new GridBagConstraints(2, 0, 1, 1, 30.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL, new Insets(5, 2, 5, 5), 0, 0));
		controlPanel.add(debitField, new GridBagConstraints(3, 0, 1, 1, 10.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(5, 2, 5, 5), 0, 0));

		// item Code
		itemCode.setPreferredSize(new Dimension(4, 22));

		itemCode.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateItemCode();
			}
		});
		itemCode.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				updateItemCode();
			}
		});

		// Quantity
		quantity.setPreferredSize(new Dimension(4, 22));
		quantity.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateQuantity();
			}
		});
		quantity.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				updateQuantity();
			}
		});

		// cost
		cost.setPreferredSize(new Dimension(4, 22));
		cost.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateCost();
			}
		});
		cost.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				updateCost();
			}
		});

		duplicateEntryBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				executeDuplicateBtn();
			}
		});

		duplicateEntryBtn.setPreferredSize(new Dimension(4, 22));

		controlPanel.add(itemCode, new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(2, 2, 5, 5), 0, 0));

		controlPanel.add(quantity, new GridBagConstraints(3, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(2, 2, 5, 5), 0, 0));

		controlPanel.add(cost, new GridBagConstraints(4, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(2, 2, 5, 5), 0, 0));


		controlPanel.add(duplicateEntryBtn, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(2, 2, 5, 5), 0, 0));

		totalLabel.setAlignmentX(0.5f);
		totalLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		totalPanel.setPreferredSize(new Dimension(0, 0));
		totalPanel.setLayout(new GridBagLayout());
		totalPanel.add(totalLabel, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		controlPanel.add(totalPanel, new GridBagConstraints(3, 1, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 0, 5, 5), 0, 0));
		controlPanel.add(creditField, new GridBagConstraints(4, 0, 1, 1, 10.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(5, 2, 5, 5), 0, 0));
		controlPanel.add(categoryBox, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(2, 2, 5, 5), 0, 0));
		controlPanel.add(newButton, new GridBagConstraints(5, 0, 1, 1, 10.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(5, 4, 5, 3), 0, 0));
		controlPanel.add(deleteButton, new GridBagConstraints(5, 1, 1, 1, 10.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(2, 4, 5, 3), 0, 0));

		setLayout(new BorderLayout());
		add(northPanel, BorderLayout.NORTH);
		add(entryListScrollPane, BorderLayout.CENTER);
		add(controlPanel, BorderLayout.SOUTH);
	}

	private void updateEntryList() {
		account.sortEntries(entryComparator);
		Vector entries = account.getEntries();

		Iterator it = entries.listIterator();
		entryListModel = new DefaultListModel();
		while (it.hasNext()) {
			Entry e = (Entry) it.next();
			if (filterPanel.filterEntry(e, account, userDate))
				entryListModel.addElement(e);
		}

		computeBalances();
		newButton.setEnabled(entryFilter.isEmpty());
		Entry selected = selectedEntry;
		entryList.setModel(entryListModel);
		entryList.setSelectedValue(selected, true);
	}

	/**
	 * Computes the balance for each entry.
	 */
	private void computeBalances() {
		balances = new long[entryListModel.size() + 1];
		balances[0] = entryFilter.isEmpty() ? account.getStartBalance() : 0;
		for (int i = 1; i < balances.length; i++) {
			Entry e = (Entry) entryListModel.elementAt(i - 1);
			balances[i] = balances[i - 1] + e.getAmount();
		}
		totalLabel.setText(entryFilter.isEmpty() ? account.formatAmount(balances[balances.length - 1]) : null);
	}

	private void completeTransfer() {
		if (selectedEntry instanceof DoubleEntry) {
			DoubleEntry de = (DoubleEntry) selectedEntry;
			Account account = (Account) de.getOther().getCategory();
			Account otherAccount = (Account) de.getCategory();
			if (account.getCurrencyCode().equals(otherAccount.getCurrencyCode())) {
				de.getOther().setAmount(-de.getAmount());
			} else {
				int status = transferDialog.showDialog(account, otherAccount, de.getAmount(),
						de.getOther().getAmount());
				if (status == Constants.OK)
					de.getOther().setAmount(transferDialog.getOtherAmount());
			}
		}
	}

	/**
	 * Enables/disables the corresponding components.
	 */
	private void enableComponents(boolean status) {
		disableDateField(status);
		dateField.setEnabled(status);
		descriptionField.setEnabled(status);
		categoryBox.setEnabled(status);
		debitField.setEnabled(status);
		creditField.setEnabled(status);
		deleteButton.setEnabled(status);
		if (selectedEntry == null)
			duplicateEntryBtn.setEnabled(false);
		itemCode.setEnabled(status);
		quantity.setEnabled(status);
		cost.setEnabled(status);
	}

	/**
	 * A new entry has been selected.
	 */
	private void multipleEntrySelected(){
		List<Entry> selectedValuesList = (List<Entry>) entryList.getSelectedValuesList();
		if(selectedValuesList.size() == 1){
			selectedEntry = selectedValuesList.get(0);
			descriptionField.setText(selectedEntry.getDescription());
			categoryBox.setSelectedItemWithoutEvent(selectedEntry.getCategory());
			dateModel.setValue(selectedEntry.getDate());
			updateDebitAndCreditField(selectedEntry.getAmount());
			enableComponents(true);
			itemCode.setText(selectedEntry.getItemCode());
			quantity.setText(String.valueOf(selectedEntry.getQuantity()));
			cost.setText(String.valueOf(selectedEntry.getCost()));
			enableDuplicateEntryBtn(selectedEntry);
			// top dropdown
                              filterPanel.setActionComboStatus(true);
                              filterPanel.setSelectedItems(selectedValuesList);
                              filterPanel.setAccountEntriesPanel(this);
		} else if(selectedValuesList.size() > 1){
			//activate the dropdownbox
			filterPanel.setActionComboStatus(true);
			filterPanel.setSelectedItems(selectedValuesList);
			filterPanel.setAccountEntriesPanel(this);

			//disable after completion.
			//filterPanel.setActionComboStatus(false);
			clearSelection();
		} else {
			clearSelection();
			filterPanel.setActionComboStatus(false);
		}
	}

	/**
	 * Clears the entry list selection.
	 */
	private void clearSelection() {
		selectedEntry = null;
		descriptionField.setText("");
		categoryBox.setSelectedItemWithoutEvent(null);

		/**
		 * date set
		 */
		JDatePanelImpl datePanel = new JDatePanelImpl(new UtilDateModel(new Date()));
		dateField = new JDatePickerImpl(datePanel);

		debitField.setText("");
		creditField.setText("");
		itemCode.setText("");
		quantity.setText("");
		cost.setText("");
		enableComponents(false);
	}

	/**
	 * Following method is to enable the Duplicate Entry button
	 */
	private void enableDuplicateEntryBtn(Entry entry) {
		if (entry != null) {
			duplicateEntryBtn.setEnabled(true);
		} else {
			duplicateEntryBtn.setEnabled(false);
		}
	}

	/**
	 * The following method is for executing the duplicate entry
	 */
	private void executeDuplicateBtn() {
		if (account == null || selectedEntry == null) {
			JOptionPane.showMessageDialog(this, Constants.LANGUAGE.getString("DuplicateEntry.Error.Content"),
					Constants.LANGUAGE.getString("DuplicateEntry.Error.Title"), JOptionPane.ERROR_MESSAGE);
			return;
		}

		String revDescription = descriptionField.getText();
		Category revCategory = selectedEntry.getCategory();
		Date revDate = selectedEntry.getDate();
		String revAmountLabel, revAmountValue = "";
		String itemCode = selectedEntry.getItemCode();
		int quantity = selectedEntry.getQuantity();
		float cost = selectedEntry.getCost();
		if (debitField.getText().toString().equals("")) {
			revAmountLabel = "Income";
			revAmountValue = creditField.getText();
		} else {
			revAmountLabel = "Expense";
			revAmountValue = debitField.getText();
		}
		// Are you sure, you want to duplicate the entry with following details?\nDate: %s\nDescription: %s\nCategory: %s\nOrder Number: %s\n%s: %s
		int confirmStatus = JOptionPane.showConfirmDialog(this,
				String.format(Constants.LANGUAGE.getString("DuplicateEntry.Confirm.Message"), sf.format(revDate),
						revDescription, revCategory, itemCode, quantity, cost, revAmountLabel, revAmountValue),
				Constants.LANGUAGE.getString("DuplicateEntry.Confirm.Title"), JOptionPane.YES_NO_OPTION);
		if (confirmStatus == 0) {
			Entry revEntry = new Entry();
			revEntry.setDate(revDate);
			revEntry.setDescription(revDescription);
			revEntry.setItemCode(itemCode);
			revEntry.setQuantity(quantity);
			revEntry.setCost(cost);
			account.addEntry(revEntry);
			updateEntryList();
			entryList.setSelectedValue(revEntry, true);
			session.modified();
			categoryBox.setSelectedItem(revCategory);
			if (revAmountLabel.equals("Expense")) {
				updateAmount(-account.parseAmount(revAmountValue));
			} else {
				updateAmount(account.parseAmount(revAmountValue));
			}
		}
	}

	/**
	 * Create a new entry.
	 */
	private void newEntry() {
		newEntry = new Entry();
		newEntry.setDate(last_date);
		account.addEntry(newEntry);
		updateEntryList();
		entryList.setSelectedValue(newEntry, true);
		session.modified();
	}

	/**
	 * Delete selected entry.
	 */
	private void deleteEntry() {
		int answer = JOptionPane.showConfirmDialog(this, Constants.LANGUAGE.getString("Entry.ConfirmDelete"),
				Constants.LANGUAGE.getString("Dialog.ConfirmDelete"), JOptionPane.YES_NO_OPTION);
		if (answer != JOptionPane.YES_OPTION)
			return;

		int index = entryList.getSelectedIndex();

		account.removeEntry(selectedEntry);
		updateEntryList();

		if (index < account.getEntries().size())
			entryList.setSelectedIndex(index);
		else if (index > 0)
			entryList.setSelectedIndex(index - 1);
		else
			clearSelection();

		session.modified();
	}

	/*
	This is to move the entries from current account to any choosen accounts
	 */
	public void moveEntries(List<Entry> entries){
		if(entries.stream().anyMatch(e -> e instanceof DoubleEntry)){
			// Reject moving if there is any Double entry
			JOptionPane.showMessageDialog(this, "Some double entries are included. De-select to continue!",
					"Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		// Remove current account from account list
		Vector accountToMove = new Vector(session.getAccounts());
		accountToMove.remove(account);

		if (accountChooser.showDialog(accountToMove, "Please choose an account to move entries to", true) == Constants.OK) {
			Account selectedAccount = accountChooser.getSelectedAccount();
			int answer = JOptionPane.showConfirmDialog(this, String.format("Are you sure? you want to move %s entries from %s to %s account?",entries.size(),account.getName(),selectedAccount.getName()),
					"Confirm Moving..", JOptionPane.YES_NO_OPTION);
			if (answer != JOptionPane.YES_OPTION)
				return;

			entries.stream().forEach(e -> {
				selectedAccount.addEntry(e);
				account.removeEntry(e);
			});

			updateEntryList();
			clearSelection();
			session.modified();

			JOptionPane.showMessageDialog(this, String.format("%s entries are moved to %s successfully",entries.size(),selectedAccount.getName()),
					"Action Success", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	public void deleteSelectedEntries(List<Entry> entries){
		int answer = JOptionPane.showConfirmDialog(this, String.format(Constants.LANGUAGE.getString("Entries.ConfirmDelete"),entries.size()),
				Constants.LANGUAGE.getString("Dialog.ConfirmDelete"), JOptionPane.YES_NO_OPTION);
		if (answer != JOptionPane.YES_OPTION)
			return;
		//Remove all
		entries.stream().forEach(account::removeEntry);
		updateEntryList();
		clearSelection();
		session.modified();
		JOptionPane.showMessageDialog(this, String.format("%s entries are removed successfully",entries.size()),
				"Action Success", JOptionPane.INFORMATION_MESSAGE);
	}

	private void setCategory() {
		Category newCategory = (Category) categoryBox.getSelectedItem();

		if (newCategory == account || newCategory instanceof TransferCategory) {
			categoryBox.setSelectedItemWithoutEvent(selectedEntry.getCategory());
			return;
		} else if (newCategory instanceof RootCategory) {
			newCategory = null;
		}

		Entry oldEntry = selectedEntry;
		if (newCategory == null || newCategory instanceof SimpleCategory)
			selectedEntry = selectedEntry.toEntry();
		else if (newCategory instanceof Account)
			selectedEntry = selectedEntry.toDoubleEntry();
		else if (newCategory instanceof SplitCategory)
			selectedEntry = selectedEntry.toSplittedEntry();
		selectedEntry.setCategory(newCategory);
		account.replaceEntry(oldEntry, selectedEntry);
	}

	private void updateAmount(long a) {
		updateDebitAndCreditField(a);
		selectedEntry.setAmount(a);
		completeTransfer();
		updateEntryList();
	}

	private void updateCategory() {
		setCategory();
		completeTransfer();
		updateEntryList();
	}

	private void updateDate() {
		if (selectedEntry != null) {
			Date originalDate = (Date) dateModel.getValue();
			selectedEntry.setDate(originalDate);
			last_date = originalDate;
			updateEntryList();
		}
	}

	private void updateDescription() {
		if (newEntry == selectedEntry && foundEntry != null) {
			if (debitField.getText().length() > 0)
				selectedEntry.setAmount(-account.parseAmount(debitField.getText()));
			else
				selectedEntry.setAmount(account.parseAmount(creditField.getText()));
			setCategory();
			completeTransfer();
			// selectedEntry.setDate(userDate.parse(dateField.getText()));
		}
		newEntry = null;
		selectedEntry.setDescription(descriptionField.getText());
		updateEntryList();
	}

	private void updateItemCode(){
		selectedEntry.setItemCode(itemCode.getText());
		updateEntryList();
	}

	private void updateQuantity(){
		selectedEntry.setQuantity(Integer.parseInt(quantity.getText()));
		updateEntryList();
	}

	private void updateCost(){
		selectedEntry.setCost(Float.parseFloat(cost.getText()));
		updateEntryList();
	}

	private void updateStatus() {
		updateEntryList();
	}

	private void updateDebit() {
		if (debitField.getText().equals("")) {
			return;
		} else {
			updateAmount(-account.parseAmount(debitField.getText()));
		}
	}

	private void updateCredit() {
		if (creditField.getText().equals("")) {
			return;
		} else {
			updateAmount(account.parseAmount(creditField.getText()));
		}
	}

	private void updateDebitAndCreditField(long amount) {
		if (amount == 0) {
			debitField.setText("");
			creditField.setText("");
		} else if (amount > 0) {
			debitField.setText("");
			creditField.setText(account.formatAmount(amount));
		} else {
			debitField.setText(account.formatAmount(-amount));
			creditField.setText("");
		}
	}

	private void completeEntry(char c) {
		if (newEntry != selectedEntry || !Character.isLetterOrDigit(c))
			return;

		newEntry = null;
		String desc = descriptionField.getText().toLowerCase();
		foundEntry = searchEntryBackwards(desc);
		if (foundEntry != null) {
			if (selectedEntry.getAmount() == 0)
				updateDebitAndCreditField(foundEntry.getAmount());
			if (selectedEntry.getCategory() == null) {
				categoryBox.setSelectedItemWithoutEvent(foundEntry.getCategory());
			}

			descriptionField.setText(foundEntry.getDescription());
			descriptionField.setCaretPosition(foundEntry.getDescription().length());
			descriptionField.moveCaretPosition(desc.length());
		}
		newEntry = selectedEntry;
	}

	private Entry searchEntryBackwards(String description) {
		Vector entries = account.getEntries();
		for (int i = entryList.getSelectedIndex() - 1; i >= 0; i--) {
			Entry e = (Entry) entries.elementAt(i);
			if (e.getDescription() != null && e.getDescription().toLowerCase().startsWith(description))
				return e;
		}
		return null;
	}

	void split() {
		splittedEntryDialog.showDialog((SplittedEntry) selectedEntry, session, account);
	}

	public void setCategoryChooser(CategoryChooser categoryChooser) {
		filterPanel.setCategoryChooser(categoryChooser);
	}

	public void setAppendTextBox(AppendTextBox appendTextBox) {
		filterPanel.setAppendTextBox(appendTextBox);
	}

	public void setQuantityTextBox(QuantityTextBox quantityTextBox) {
		filterPanel.setQuantityTextBox(quantityTextBox);
	}

	public void setCostTextBox(CostTextBox costTextBox) {
		filterPanel.setCostTextBox(costTextBox);
	}

	/**
	 * Inner class to render entries. Used to set the cell renderer of
	 * entryList.
	 */
	class EntryCellRenderer implements ListCellRenderer {

		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
				boolean cellHasFocus) {
			Entry entry = (Entry) value;

			entryRenderer.setLook(isSelected, index);
			if (entry != Entry.PROTOTYPE) {
				entryRenderer.setDateFormat(AccountEntriesPanel.this.userDate);
				entryRenderer.setAccount(AccountEntriesPanel.this.account);
				entryRenderer.setModel(entry);
				entryRenderer.setBalance(balances[index + 1]);
			}
			return entryRenderer;
		}
	}

	private void disableDateField(boolean status) {
		Component[] components = controlPanel.getComponents();
		for (Component c : components) {
			if (c.getClass() == dateField.getClass()) {
				JDatePickerImpl datePick = (JDatePickerImpl) c;
				Component[] components2 = datePick.getComponents();
				for (Component c2 : components2) {
					c2.setEnabled(status);
				}
			}

		}
	}
}
