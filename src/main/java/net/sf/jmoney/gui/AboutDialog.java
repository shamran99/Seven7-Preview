/*
 *
 *  JMoney - A Personal Finance Manager
 *  Copyright (c) 2001-2003 Johann Gyger <jgyger@users.sf.net>
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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.InputStream;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.plaf.metal.MetalLookAndFeel;

import net.sf.jmoney.Constants;

/**
 * About dialog for JMoney.
 */
public class AboutDialog extends JDialog {
	JPanel centerPanel = new JPanel();
	JPanel northPanel = new JPanel();
	JTextArea licenseArea = new JTextArea();
	JLabel applicationLabel = new JLabel();
	JLabel copyrightLabel = new JLabel();
	JLabel versionTagLabel = new JLabel();
	JButton closeButton = new JButton();
	JFrame parent;
	JLabel versionLabel = new JLabel();

	public AboutDialog(JFrame parent) {
		super(parent, "", true);
		this.setTitle(Constants.LANGUAGE.getString("AboutDialog.title"));
		this.parent = parent;
		try {
			jbInit();
		} catch (Exception e) {
			e.printStackTrace();
		}
		pack();
	}

	public void showDialog() {
		licenseArea.setFont(MetalLookAndFeel.getSubTextFont()); // update font
		licenseArea.setOpaque(false); // doesn't work in jbInit
		pack();
		setLocationRelativeTo(parent);
		show();
	}

	private void jbInit() throws Exception {
		// north panel
		// -------------------------------------------------------------
		applicationLabel.setIcon(Constants.JMONEY_IMAGE);
		// applicationLabel.setBackground(Color.white);
		// northPanel.setBackground(Color.WHITE);
		northPanel.setLayout(new GridBagLayout());
		versionLabel.setText(" " + Constants.GENERAL.getString("Version"));
		closeButton.setToolTipText("");
		northPanel.add(applicationLabel, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.CENTER, new Insets(18, 18, 17, 17), 0, 0));

		// center panel
		// ------------------------------------------------------------
		versionTagLabel.setText(Constants.LANGUAGE.getString("AboutDialog.version"));
		copyrightLabel.setText("Copyright (C) 2017 Shamran Siddique <shamran100@gmail.com>");

		InputStream in = Constants.class.getResourceAsStream("resources/GPL.txt");
		byte[] bytes = new byte[in.available()];
		in.read(bytes);
		licenseArea.setText(new String(bytes));
		licenseArea.setEditable(false);

		closeButton.setText(Constants.LANGUAGE.getString("AboutDialog.close"));
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		centerPanel.setLayout(new GridBagLayout());
		centerPanel.add(closeButton, new GridBagConstraints(0, 3, 2, 1, 1.0, 1.0, GridBagConstraints.SOUTHEAST,
				GridBagConstraints.NONE, new Insets(17, 12, 11, 11), 0, 0));
		centerPanel.add(licenseArea, new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(0, 12, 0, 11), 0, 0));
		centerPanel.add(copyrightLabel, new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(0, 12, 11, 11), 0, 0));
		centerPanel.add(versionTagLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(11, 12, 0, 0), 0, 0));
		centerPanel.add(versionLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(11, 0, 0, 0), 0, 0));

		// this
		// --------------------------------------------------------------------
		getContentPane().add(northPanel, BorderLayout.NORTH);
		getContentPane().add(centerPanel, BorderLayout.CENTER);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				dispose();
			}
		});
	}

}
