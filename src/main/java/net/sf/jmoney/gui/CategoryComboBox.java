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

import java.awt.Component;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;

import net.sf.jmoney.model.Category;
import net.sf.jmoney.model.CategoryNode;
import net.sf.jmoney.model.RootCategory;

/**
 * A combo box to choose a category.
 */
public class CategoryComboBox extends SelectionComboBox {

	/**
	 * Creates a new CategoryComboBox.
	 */
	public CategoryComboBox() {
		setRenderer(new Renderer());
	}

	/**
	 * Sets the model.
	 */
	public void setModel(TreeModel treeModel) {
		setModel(new Model(treeModel));
	}

	/**
	 * Model class.
	 */
	class Model extends DefaultComboBoxModel {
		TreeModel treeModel;

		public Model(TreeModel treeModel) {
			this.treeModel = treeModel;
			if (treeModel == null)
				return;
			update();
			treeModel.addTreeModelListener(new TreeModelListener() {
				public void treeNodesChanged(TreeModelEvent e) {
					update();
				}

				public void treeNodesInserted(TreeModelEvent e) {
					update();
				}

				public void treeNodesRemoved(TreeModelEvent e) {
					update();
				}

				public void treeStructureChanged(TreeModelEvent e) {
					update();
				}
			});
		}

		private void update() {
			removeAllElements();
			addElement(((CategoryNode) treeModel.getRoot()).getUserObject());
			for (int i = 0; i < treeModel.getChildCount(treeModel.getRoot()); i++)
				buildModel(treeModel.getChild(treeModel.getRoot(), i));
		}

		private void buildModel(Object node) {
			Category cat = (Category) ((CategoryNode) node).getUserObject();
			addElement(cat);
			for (int i = 0; i < treeModel.getChildCount(node); i++)
				buildModel(treeModel.getChild(node, i));
		}
	}

	/**
	 * Renderer class.
	 */
	class Renderer extends JLabel implements ListCellRenderer {
		Border emptyBorder = new EmptyBorder(1, 1, 1, 1);

		public Renderer() {
			setOpaque(true);
		}

		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
				boolean cellHasFocus) {
			if (isSelected) {
				setBackground(UIManager.getColor("ComboBox.selectionBackground"));
				setForeground(UIManager.getColor("ComboBox.selectionForeground"));
			} else {
				setBackground(UIManager.getColor("ComboBox.background"));
				setForeground(UIManager.getColor("ComboBox.foreground"));
			}

			Category category = (Category) value;

			// avoid a null pointer exception
			if (category == null || category instanceof RootCategory) {
				setText(" ");
				setBorder(emptyBorder);
				return this;
			}

			// categories in list
			if (index != -1) {
				setText(category.getCategoryName());
				int level = category.getCategoryNode().getLevel() - 1;
				setBorder(new EmptyBorder(1, 10 * level + 1, 1, 1));
				return this;
			}

			// other
			setText(category.getCategoryName());
			setBorder(emptyBorder);
			return this;
		}
	}

}
