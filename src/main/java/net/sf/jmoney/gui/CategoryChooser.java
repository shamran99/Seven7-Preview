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
import net.sf.jmoney.model.Category;
import net.sf.jmoney.model.CategoryNode;
import net.sf.jmoney.model.CategoryTreeModel;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ResourceBundle;

public class CategoryChooser extends JDialog {
    ResourceBundle language = Constants.LANGUAGE;
    JPanel rootPanel = new JPanel();
    JPanel commandPanel = new JPanel();
    JLabel instructionLabel = new JLabel();
    JScrollPane categoryTreeScrollPane = new JScrollPane();
    JTree categoryList = new JTree();
    JButton okButton = new JButton();
    JButton cancelButton = new JButton();
    int status = Constants.CANCEL;
    JFrame parent;

    /**
     * Dialog to choose a category.
     */
    public CategoryChooser(JFrame parent) {
        super(parent, Constants.LANGUAGE.getString("CategoryChooser.title"), true);
        this.parent = parent;
        try {
            jbInit();
//			categoryList.setSelectionModel(TreeSelectionModel.CONTIGUOUS_TREE_SELECTION);
        } catch (Exception e) {
            e.printStackTrace();
        }
        pack();
    }

    public void setCategoryRootModel(CategoryTreeModel categoryTreeModel) {
        categoryList.setModel(categoryTreeModel);
    }

    public int showDialog() {
        okButton.setEnabled(false);
        pack();
        setLocationRelativeTo(parent);
        show();
        return status;
    }

    public Category getSelectedCategory() {
        CategoryNode categoryNode = (CategoryNode) categoryList.getLastSelectedPathComponent();
        if(categoryNode.isRoot()){
           return null;
        }
        return categoryNode.getCategory();
    }

    private void jbInit() throws Exception {
        // command panel
        // -----------------------------------------------------------
        // ok button
        okButton.setText(language.getString("Dialog.ok"));
        okButton.setEnabled(false);
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ok();
            }
        });
        // cancel button
        cancelButton.setText(language.getString("Dialog.cancel"));
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cancel();
            }
        });
        // add components to command panel
        commandPanel.setLayout(new GridLayout(1, 2, 5, 0));
        this.setModal(true);
        commandPanel.add(okButton, null);
        commandPanel.add(cancelButton, null);

        categoryList.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                okButton.setEnabled(true);
            }
        });
        categoryTreeScrollPane.setViewportView(categoryList);
        // add components to root panel
        rootPanel.setLayout(new GridBagLayout());
        rootPanel.add(instructionLabel, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, new Insets(12, 12, 11, 11), 0, 0));
        rootPanel.add(categoryTreeScrollPane, new GridBagConstraints(0, 1, 2, 1, 1.0, 1.0, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets(0, 12, 0, 12), 0, 0));
        rootPanel.add(commandPanel, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                GridBagConstraints.NONE, new Insets(17, 0, 11, 11), 0, 0));

        instructionLabel.setText(Constants.LANGUAGE.getString("CategoryChooser.Details"));
        // this
        // --------------------------------------------------------------------
        getContentPane().add(rootPanel, BorderLayout.CENTER);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                cancel();
            }
        });
    }

    void ok() {
        status = Constants.OK;
        dispose();
    }

    void cancel() {
        status = Constants.CANCEL;
        dispose();
    }

}
