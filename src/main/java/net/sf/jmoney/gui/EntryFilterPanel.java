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

import net.sf.jmoney.Constants;
import net.sf.jmoney.EntryFilter;
import net.sf.jmoney.VerySimpleDateFormat;
import net.sf.jmoney.model.Account;
import net.sf.jmoney.model.Category;
import net.sf.jmoney.model.Entry;
import net.sf.jmoney.model.Session;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.List;

public class EntryFilterPanel extends JPanel {

	private List<Entry> selectedValuesList;

	static String[] filterActions() {
		String[] filterActions = { Constants.LANGUAGE.getString("EntryFilterPanel.filter"),
				Constants.LANGUAGE.getString("EntryFilterPanel.clear") };
		return filterActions;
	}

	private JComboBox filterTypeBox = new JComboBox(EntryFilter.filterTypes());
	private JTextField filterField = new JTextField("");
	private JTextField searchFilterField = new JTextField("");
	private static CategoryComboBox categoryComboBox = new CategoryComboBox();
	private EntryFilter entryFilter = new EntryFilter();
	private final JButton btnClear = new JButton(Constants.LANGUAGE.getString("EntryFilterPanel.clear"));
	private final DecimalFormat formatter = new DecimalFormat("#,###,###,##0.00");
	private final JComboBox actions = new JComboBox(actionTypes());
	private CategoryChooser categoryChooser;
	private AppendTextBox appendTextBox;
	private QuantityTextBox quantityTextBox;
	private CostTextBox costTextBox;
	private AccountEntriesPanel accountEntriesPanel;

	public EntryFilterPanel() {
		try {
			jbInit();
			categoryComboBox.setVisible(false);
			setActionComboStatus(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setAccountEntriesPanel(AccountEntriesPanel accountEntriesPanel){
		this.accountEntriesPanel = accountEntriesPanel;
	}

	public String[] actionTypes() {
		String[] filterType = { Constants.LANGUAGE.getString("ActionType.ChangeCategory"),
				Constants.LANGUAGE.getString("ActionType.AppendText"),
				Constants.LANGUAGE.getString("ActionType.MoveAccount"),
				Constants.LANGUAGE.getString("ActionType.Delete"),
				Constants.LANGUAGE.getString("ActionType.SetQuantity"),
				Constants.LANGUAGE.getString("ActionType.SetCost")};
		return filterType;
	}
	public void setActionComboStatus(boolean enabled){
		actions.setEnabled(enabled);
	}

	public void actionEventSelected(){
		switch(actions.getSelectedIndex()){
			case 0: changeCategory(); break;
			case 1: appendText(); break;
			case 2: accountEntriesPanel.moveEntries(selectedValuesList); break;
			case 3: accountEntriesPanel.deleteSelectedEntries(selectedValuesList); break;
			case 4: setQuantity(); break;
			case 5: setCost(); break;
			default: //no options
		}
	}

	private void setQuantity() {
		int status = quantityTextBox.showDialog();
		if (status == Constants.OK) {
			String text = quantityTextBox.getText();
			int i = JOptionPane.showConfirmDialog(this, String.format("Are you sure? Do you want to change the quantity to %s?",text), "Confirm ?", JOptionPane.YES_NO_OPTION);
			if(i == 1){
				return;
			}

			try{
				int quantity = Integer.parseInt(text);
				selectedValuesList.stream().forEach(e -> e.setQuantity(quantity));
				JOptionPane.showMessageDialog(this, String.format("%s entries quantity set to %s",selectedValuesList.size(),text),
						"Action Success", JOptionPane.INFORMATION_MESSAGE);


			}
			catch(NumberFormatException exp){
				JOptionPane.showMessageDialog(this, "Only Numbers are allowed:"+exp.getMessage(),
						"Action Failure", JOptionPane.ERROR_MESSAGE);
			}
			catch(Exception exp) {
				JOptionPane.showMessageDialog(this, "Failed:"+exp.getMessage(),
						"Action Failure", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void setCost() {
		int status = costTextBox.showDialog();
		if (status == Constants.OK) {
			String text = costTextBox.getText();
			int i = JOptionPane.showConfirmDialog(this, String.format("Are you sure? Do you want to change the cost to %s?",text), "Confirm ?", JOptionPane.YES_NO_OPTION);
			if(i == 1){
				return;
			}

			try{
				float cost = Float.parseFloat(text);
				selectedValuesList.stream().forEach(e -> e.setCost(cost));
				JOptionPane.showMessageDialog(this, String.format("%s entries cost set to %s",selectedValuesList.size(),text),
						"Action Success", JOptionPane.INFORMATION_MESSAGE);


			}
			catch(NumberFormatException exp){
				JOptionPane.showMessageDialog(this, "Only Numbers are allowed:"+exp.getMessage(),
						"Action Failure", JOptionPane.ERROR_MESSAGE);
			}
			catch(Exception exp) {
				JOptionPane.showMessageDialog(this, "Failed:"+exp.getMessage(),
						"Action Failure", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void appendText() {
		// append text selected
		int status = appendTextBox.showDialog();
		if (status == Constants.OK) {
			String text = appendTextBox.getText();
			if(text.contains("%s") && text.length() > 2){
				int i = JOptionPane.showConfirmDialog(this, String.format("Are you sure? Do you want to change the description text to the format %s?",text), "Confirm ?", JOptionPane.YES_NO_OPTION);
				if(i == 1){
					return;
				}

				selectedValuesList.stream().forEach(e -> {
					String description = e.getDescription();
					e.setDescription(String.format(text,description));
				});

				JOptionPane.showMessageDialog(this, String.format("%s entries description changed to the format %s",selectedValuesList.size(),text),
						"Action Success", JOptionPane.INFORMATION_MESSAGE);

			}
			else {
				JOptionPane.showMessageDialog(this, "Incorrect text. Please enter a valid text",
						"Action Failure", JOptionPane.ERROR_MESSAGE);
			}

		}
	}

	private void changeCategory(){
		// Category change selected
		int status = categoryChooser.showDialog();
		if (status == Constants.OK) {
			Category selectedCategory = categoryChooser.getSelectedCategory();
			// an existing account has been selected

			if(selectedCategory != null){
				//Process the entries and chack weather the change is allowed.
				int i = JOptionPane.showConfirmDialog(this, String.format("Are you sure? Do you want to move entries to %s category?",selectedCategory.getCategoryName()), "Confirm ?", JOptionPane.YES_NO_OPTION);
				if(i == 1){
					return;
				}

				//change the category
				selectedValuesList.stream().forEach(x -> {
					x.setCategory(selectedCategory);
				});

				JOptionPane.showMessageDialog(this, String.format("%s number of entries changed to the %s category",selectedValuesList.size(),selectedCategory.getCategoryName()),
						"Action Success", JOptionPane.INFORMATION_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(this, "Incorrect selection. Please select a category to change",
						"Action Success", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	 * Returns the entryFilter.
	 * 
	 * @return EntryFilter
	 */
	public EntryFilter getEntryFilter() {
		return entryFilter;
	}

	/**
	 * Sets the entryFilter.
	 * 
	 * @param entryFilter
	 *            The entryFilter to set
	 * @param session
	 */
	public void setEntryFilter(EntryFilter entryFilter) {
		this.entryFilter = entryFilter;
		filterField.setText(entryFilter.getPattern());
		filterTypeBox.setSelectedIndex(entryFilter.getType());
	}

	/**
	 * The following method will set the data model to the category combo box.
	 * 
	 * @param session
	 */
	public static void setCategorySelector(Session session) {
		Object selectedItem = categoryComboBox.getSelectedItem();
		categoryComboBox.setModel(session.getCategories());
		categoryComboBox.setSelectedItem(selectedItem);
	}

	public boolean filterEntry(Entry entry, Account account, VerySimpleDateFormat dateFormat) {
		return entryFilter.filterEntry(entry, account, dateFormat, filterTypeBox.getSelectedIndex());
	}

	private void setFilterPattern() {
		int type = entryFilter.getType();
		String text = filterField.getText();
		if (type == 1 && !text.equals("")) {
			try {
				String format = formatter.format(Double.parseDouble(text));
				text = format;
				filterField.setText(text);
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(this, Constants.LANGUAGE.getString("EntryFilter.OnlyNumber.Error"),
						Constants.LANGUAGE.getString("MainFrame.FileError"), JOptionPane.ERROR_MESSAGE);
				filterField.setText("");
			}
		} else if (type == 2) {
			int index = categoryComboBox.getSelectedIndex();
			text = (index == 0 || index == -1) ? "" : categoryComboBox.getSelectedItem().toString();
		}
		entryFilter.setPattern(text);
	}

	private void setSearchPattern() {
		String text = searchFilterField.getText();
		entryFilter.setSearchPattern(text);
	}

	private void jbInit() throws Exception {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{32, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0};
		setLayout(gridBagLayout);
		setBorder(BorderFactory.createEtchedBorder());

		filterTypeBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				filterItemSelected();
			}
		});

		actions.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionEventSelected();
			}
		});

		categoryComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setFilterPattern();
			}
		});

		filterField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setFilterPattern();
			}
		});

		searchFilterField.setToolTipText("Only works with Category search!! Searches only Description and item code");
		searchFilterField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setSearchPattern();
			}
		});

		GridBagConstraints gbc_btnClear = new GridBagConstraints();
		gbc_btnClear.insets = new Insets(0, 0, 0, 5);
		gbc_btnClear.gridx = 1;
		gbc_btnClear.gridy = 1;
		btnClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clearSearch();
			}
		});
		
		GridBagConstraints gbc_actions = new GridBagConstraints();
		gbc_actions.fill = GridBagConstraints.HORIZONTAL;
		gbc_actions.insets = new Insets(0, 0, 0, 5);
		gbc_actions.gridx = 0;
		gbc_actions.gridy = 1;
		add(actions, gbc_actions);
		add(btnClear, gbc_btnClear);
		add(filterTypeBox, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.NONE, new Insets(5, 4, 4, 5), 0, 0));

		add(filterField, new GridBagConstraints(3, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(5, 4, 4, 4), 0, 0));

		add(categoryComboBox, new GridBagConstraints(3, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(5, 4, 4, 4), 0, 0));
		add(searchFilterField, new GridBagConstraints(4, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(5, 4, 4, 4), 0, 0));
	}

	private void filterItemSelected() {
		int index = filterTypeBox.getSelectedIndex();
		if (index == 2) {
			categoryComboBox.setVisible(true);
			filterField.setVisible(false);
		} else {
			categoryComboBox.setVisible(false);
			filterField.setVisible(true);
		}
		entryFilter.setType(index);
	}

	private void clearSearch() {
		filterField.setText("");
		if (categoryComboBox.getSelectedIndex() != -1)
			categoryComboBox.setSelectedIndex(0);
		setFilterPattern();
		setSearchPattern();
	}

	public void setCategoryChooser(CategoryChooser categoryChooser) {
		this.categoryChooser = categoryChooser;
	}

	public void setSelectedItems(List<Entry> selectedValuesList) {
		this.selectedValuesList = selectedValuesList;
	}

	public void setAppendTextBox(AppendTextBox appendTextBox) {
		this.appendTextBox = appendTextBox;
	}

	public void setQuantityTextBox(QuantityTextBox quantityTextBox) {
		this.quantityTextBox = quantityTextBox;
	}

	public void setCostTextBox(CostTextBox costTextBox) {
		this.costTextBox = costTextBox;
	}

}
