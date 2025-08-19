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
import java.util.List;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import net.sf.jmoney.*;
import net.sf.jmoney.model.*;

/**
 * A dialog to modify the categories.
 */
public class CategoryPanel extends JPanel {
	ResourceBundle language = Constants.LANGUAGE;
	JTree categoryTree = new JTree();
	JScrollPane categoryTreeScrollPane = new JScrollPane();
	JLabel nameLabel = new JLabel();
	JTextField nameField = new JTextField();
	CategoryTreeModel categories;
	Category selectedCategory = null;
	Session session;
	JPopupMenu categoryPopup = new JPopupMenu();
	JMenuItem newCategoryItem = new JMenuItem();
	JMenuItem newSubcategoryItem = new JMenuItem();
	JMenuItem deleteCategoryItem = new JMenuItem();

	/**
	 * Creates a new account properties dialog
	 */
	public CategoryPanel() {
		try {
			jbInit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setSession(Session session) {
		this.session = session;
		categories = session.getCategories();
		categoryTree.setModel(categories);
		this.updateUI();
	}

	public void updateUI() {
		super.updateUI();
		if (categoryTree != null)
			categoryTree.setCellRenderer(new CategoryTreeCellRenderer());
	}

	/**
	 * Builds the GUI.
	 */
	private void jbInit() throws Exception {
		categoryTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		categoryTree.setCellRenderer(new CategoryTreeCellRenderer());
		categoryTree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				categorySelection((CategoryNode) e.getPath().getLastPathComponent());
			}
		});
		categoryTree.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				maybeShowPopup(e);
			}

			public void mouseReleased(MouseEvent e) {
				maybeShowPopup(e);
			}

			private void maybeShowPopup(MouseEvent e) {
				if (e.isPopupTrigger())
					categoryPopup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
		categoryTreeScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		nameField.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateCategory();
			}
		});
		nameField.addFocusListener(new java.awt.event.FocusAdapter() {
			public void focusLost(FocusEvent e) {
				updateCategory();
			}
		});
		categoryTreeScrollPane.setViewportView(categoryTree);

		newCategoryItem.setText(language.getString("CategoryPanel.newCategory"));
		newCategoryItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				newCategory();
			}
		});

		newSubcategoryItem.setText(language.getString("CategoryPanel.newSubcategory"));
		newSubcategoryItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				newSubcategory();
			}
		});

		deleteCategoryItem.setText(language.getString("CategoryPanel.deleteCategory"));
		deleteCategoryItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteCategory();
			}
		});

		nameLabel.setText(language.getString("CategoryPanel.name"));
		nameField.setEnabled(false);
		nameField.setColumns(30);

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWeights = new double[] { 0.0, 1.0, 0.0, 0.0 };
		gridBagLayout.columnWidths = new int[] { 0, 276, 0, 0 };
		setLayout(gridBagLayout);
		add(categoryTreeScrollPane, new GridBagConstraints(0, 0, 4, 1, 1.0, 1.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(12, 12, 11, 11), 0, 0));
		add(nameField, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(0, 0, 11, 11), 0, 0));
		add(nameLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
				new Insets(0, 12, 11, 12), 0, 0));

		GridBagConstraints gbc_toggleBtn = new GridBagConstraints();
		gbc_toggleBtn.insets = new Insets(0, 12, 11, 12);
		gbc_toggleBtn.gridx = 3;
		gbc_toggleBtn.gridy = 1;

		categoryPopup.add(newCategoryItem);
		categoryPopup.add(newSubcategoryItem);
		categoryPopup.add(deleteCategoryItem);
	}

	private void categorySelection(CategoryNode node) {
		if (node == null)
			return;
		selectedCategory = (Category) node.getUserObject();
		nameField.setText(selectedCategory.getCategoryName());
		enableComponents();
	}

	private void enableComponents() {
		boolean editable = selectedCategory instanceof SimpleCategory;
		boolean allowsChildren = editable || selectedCategory instanceof RootCategory;
		nameField.setEnabled(editable);
		newCategoryItem.setEnabled(editable);
		deleteCategoryItem.setEnabled(editable);
		newSubcategoryItem.setEnabled(allowsChildren);
	}

	private void selectCategory(Category c) {
		Object path[] = categories.getPathToRoot(c.getCategoryNode());
		categoryTree.setSelectionPath(new TreePath(path));
	}

	private void newCategory() {
		String name = language.getString("CategoryPanel.newCategory");
		SimpleCategory category = new SimpleCategory(name);
		CategoryNode parent = (CategoryNode) selectedCategory.getCategoryNode().getParent();
		categories.insertNodeInto(category.getCategoryNode(), parent, 0);
		selectCategory(category);
		session.modified();
	}

	private void newSubcategory() {
		String name = language.getString("CategoryPanel.newCategory");
		SimpleCategory category = new SimpleCategory(name);
		CategoryNode parent = selectedCategory.getCategoryNode();
		categories.insertNodeInto(category.getCategoryNode(), parent, 0);
		selectCategory(category);
		session.modified();
	}

	private void deleteCategory() {
		CategoryNode node = selectedCategory.getCategoryNode();
		categories.removeNodeFromParent(node);
		session.modified();
	}

	private void updateCategory() {
		SimpleCategory cat = (SimpleCategory) selectedCategory;
		cat.setCategoryName(nameField.getText());
		categories.sortChildren((CategoryNode) cat.getCategoryNode().getParent());
		selectCategory(cat);
		session.modified();
	}

	public class CategoryTreeCellRenderer extends DefaultTreeCellRenderer {

		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
				boolean leaf, int row, boolean hasFocus) {
			super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
			Object obj = node.getUserObject();
			if (tree.getModel() instanceof CategoryTreeModel) {
				CategoryTreeModel model = (CategoryTreeModel) tree.getModel();
				if (obj instanceof Account) {
					setIcon(Constants.ACCOUNT_ICON);
				} else if (obj instanceof Category) {
					setIcon(Constants.CATEGORY_ICON);
				}
			}
			return this;
		}

	}

}
