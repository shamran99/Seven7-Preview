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

package net.sf.jmoney;

import javax.swing.tree.DefaultMutableTreeNode;

public class NavigationTreeModel extends SortedTreeModel {

	private static final long serialVersionUID = 1L;

	private DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(
			Constants.LANGUAGE.getString("NavigationTreeModel.myFinances"));

	private SortedTreeNode accountNode = new SortedTreeNode(
			Constants.LANGUAGE.getString("NavigationTreeModel.accounts"));

	private DefaultMutableTreeNode categoryNode = new DefaultMutableTreeNode(
			Constants.LANGUAGE.getString("NavigationTreeModel.categories"));

	private DefaultMutableTreeNode reportNode = new DefaultMutableTreeNode(
			Constants.LANGUAGE.getString("NavigationTreeModel.Reports"));

	private DefaultMutableTreeNode balancesReportNode = new DefaultMutableTreeNode(
			Constants.LANGUAGE.getString("Report.AccountBalances.Title"));

	private DefaultMutableTreeNode incomeExpenseReportNode = new DefaultMutableTreeNode(
			Constants.LANGUAGE.getString("Report.IncomeExpense.Title"));

	private DefaultMutableTreeNode accountDetailsReportNode = new DefaultMutableTreeNode(
			Constants.LANGUAGE.getString("Report.accountDetails.Title"));

	private DefaultMutableTreeNode graphGeneratorNode = new DefaultMutableTreeNode("Graph Generator");

	/*
	 * private DefaultMutableTreeNode accountReportNode = new
	 * DefaultMutableTreeNode("Account Entries");
	 */
	public NavigationTreeModel() {
		// initialize with dummy root node
		super(new DefaultMutableTreeNode());

		reportNode.add(balancesReportNode);
		reportNode.add(incomeExpenseReportNode);
		reportNode.add(accountDetailsReportNode);
		reportNode.add(graphGeneratorNode);

		rootNode.add(accountNode);
		rootNode.add(categoryNode);
		rootNode.add(reportNode);
		setRoot(rootNode);
	}

	public DefaultMutableTreeNode getRootNode() {
		return (DefaultMutableTreeNode) getRoot();
	}

	public SortedTreeNode getAccountNode() {
		return accountNode;
	}

	public DefaultMutableTreeNode getCategoryNode() {
		return categoryNode;
	}

	public DefaultMutableTreeNode getReportNode() {
		return reportNode;
	}

	/**
	 * Returns the accountReportNode.
	 * 
	 * @return DefaultMutableTreeNode
	 */
	/*
	 * public DefaultMutableTreeNode getAccountReportNode() { return
	 * accountReportNode; }
	 */
	/**
	 * Returns the balancesReportNode.
	 * 
	 * @return DefaultMutableTreeNode
	 */
	public DefaultMutableTreeNode getBalancesReportNode() {
		return balancesReportNode;
	}

	/**
	 * Returns the cashFlowReportNode.
	 * 
	 * @return DefaultMutableTreeNode
	 */
	public DefaultMutableTreeNode getIncomeExpenseReportNode() {
		return incomeExpenseReportNode;
	}

	/**
	 * Returns the Account Detail Report node
	 * 
	 * @return
	 */
	public DefaultMutableTreeNode getAccountDetailsReportNode() {
		return accountDetailsReportNode;
	}

	/**
	 * Returns the Graph Generator Node
	 *
	 * @return
	 */
	public DefaultMutableTreeNode getGraphGeneratorNode() {
		return graphGeneratorNode;
	}
}
