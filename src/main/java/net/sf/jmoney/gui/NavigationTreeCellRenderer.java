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

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import net.sf.jmoney.Constants;
import net.sf.jmoney.NavigationTreeModel;
import net.sf.jmoney.model.Account;

public class NavigationTreeCellRenderer extends DefaultTreeCellRenderer {
	public NavigationTreeCellRenderer() {
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf,
			int row, boolean hasFocus) {

		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

		DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
		Object obj = node.getUserObject();
		if (tree.getModel() instanceof NavigationTreeModel) {
			NavigationTreeModel model = (NavigationTreeModel) tree.getModel();
			if (node == model.getAccountNode()) {
				setIcon(Constants.ACCOUNTS_ICON);
			} else if (node == model.getCategoryNode()) {
				setIcon(Constants.CATEGORY_ICON);
			} else if (obj instanceof Account) {
				setIcon(Constants.ACCOUNT_ICON);
			}
		}
		return this;
	}

}
