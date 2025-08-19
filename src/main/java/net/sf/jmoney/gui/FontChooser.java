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

import javax.swing.plaf.FontUIResource;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;
import java.util.ResourceBundle;
import net.sf.jmoney.*;

public class FontChooser extends JDialog {
	static String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
	static String[] fontSizes = { "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "20", "22", "24",
			"30" };
	static String[] fontStyles = { "Plain", "Bold", "Italic", "Bold Italic" };

	public static String formatFont(Font font) {
		return formatFont(font, " ");
	}

	public static String formatFont(Font font, String delimiter) {
		return font.getName() + delimiter + fontStyles[getFontStyleIndex(font)] + delimiter + font.getSize();
	}

	public static int getFontStyle(String style) {
		int i;
		for (i = 0; i < fontStyles.length; i++)
			if (fontStyles[i].equalsIgnoreCase(style))
				break;
		return getFontStyle(i);
	}

	static int getFontStyle(int index) {
		if (index == 1)
			return Font.BOLD;
		else if (index == 2)
			return Font.ITALIC;
		else if (index == 3)
			return Font.BOLD | Font.ITALIC;
		else
			return Font.PLAIN;
	}

	static int getFontStyleIndex(Font font) {
		if (font.isBold())
			if (font.isItalic())
				return 3;
			else
				return 1;
		else if (font.isItalic())
			return 2;
		else
			return 0;
	}

	ResourceBundle language = Constants.LANGUAGE;
	JPanel rootPanel = new JPanel();
	JLabel fontLabel = new JLabel();
	JPanel commandPanel = new JPanel();
	JComboBox fontComboBox = new JComboBox(fonts);
	JLabel styleLabel = new JLabel();
	JComboBox styleComboBox = new JComboBox(fontStyles);
	JLabel sizeLabel = new JLabel();
	JComboBox sizeComboBox = new JComboBox(fontSizes);
	JButton okButton = new JButton();
	JButton cancelButton = new JButton();
	JPanel previewPanel = new JPanel();
	JLabel previewLabel = new JLabel();

	JFrame parent;
	Font font;
	String fontName;
	int style;
	int size;
	int status = Constants.CANCEL;

	public FontChooser(JFrame parent) {
		super(parent, "JMoney: Font Chooser", true);
		this.parent = parent;
		try {
			jbInit();
		} catch (Exception e) {
			e.printStackTrace();
		}
		pack();
	}

	public int showDialog(Font font) {
		fontComboBox.setSelectedItem(font.getName());
		styleComboBox.setSelectedIndex(getFontStyleIndex(font));
		sizeComboBox.setSelectedItem("" + font.getSize());
		updatePreview();
		setLocationRelativeTo(parent);
		show();
		return status;
	}

	public Font getFont() {
		return font;
	}

	public FontUIResource getFontUIResource() {
		return new FontUIResource(font);
	}

	private void jbInit() throws Exception {
		// preview panel
		// -----------------------------------------------------------
		previewPanel.setBorder(new TitledBorder(language.getString("FontChooser.preview")));
		previewPanel.setPreferredSize(new Dimension(200, 100));
		previewPanel.setLayout(new BorderLayout());
		previewPanel.add(previewLabel, BorderLayout.CENTER);

		// command panel
		// -----------------------------------------------------------
		okButton.setText(language.getString("Dialog.ok"));
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ok();
			}
		});
		cancelButton.setText(language.getString("Dialog.cancel"));
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancel();
			}
		});
		commandPanel.setLayout(new GridLayout(1, 2, 5, 0));
		commandPanel.add(okButton, null);
		commandPanel.add(cancelButton, null);

		// root panel
		// --------------------------------------------------------------
		fontLabel.setText(language.getString("FontChooser.font"));
		fontComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				changeFont();
			}
		});
		styleLabel.setText(language.getString("FontChooser.style"));
		styleComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				changeStyle();
			}
		});
		sizeLabel.setText(language.getString("FontChooser.size"));
		sizeComboBox.setEditable(true);
		sizeComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				changeSize();
			}
		});
		// add components to root panel
		rootPanel.setLayout(new GridBagLayout());
		rootPanel.add(fontLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(12, 12, 11, 12), 0, 0));
		rootPanel.add(styleLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(0, 12, 11, 12), 0, 0));
		rootPanel.add(fontComboBox, new GridBagConstraints(1, 0, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(12, 0, 11, 11), 0, 0));
		rootPanel.add(styleComboBox, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(0, 0, 11, 0), 0, 0));
		rootPanel.add(sizeLabel, new GridBagConstraints(2, 1, 1, 1, 1.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.NONE, new Insets(0, 12, 11, 12), 0, 0));
		rootPanel.add(sizeComboBox, new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(0, 0, 11, 11), 0, 0));
		rootPanel.add(previewPanel, new GridBagConstraints(0, 2, 5, 1, 1.0, 1.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 12, 0, 11), 0, 0));
		rootPanel.add(commandPanel, new GridBagConstraints(0, 3, 5, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.NONE, new Insets(17, 12, 11, 11), 0, 0));

		// this
		// --------------------------------------------------------------------
		getContentPane().add(rootPanel, BorderLayout.CENTER);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				cancel();
			}
		});

	}

	void updatePreview() {
		previewLabel.setFont(font);
		previewLabel.setText(formatFont(font));
	}

	void changeFont() {
		fontName = (String) fontComboBox.getSelectedItem();
		font = new Font(fontName, style, size);
		updatePreview();
	}

	void changeStyle() {
		style = getFontStyle(styleComboBox.getSelectedIndex());
		font = font.deriveFont(style);
		updatePreview();
	}

	void changeSize() {
		try {
			size = Integer.parseInt((String) sizeComboBox.getSelectedItem());
			font = font.deriveFont((float) size);
			updatePreview();
		} catch (NumberFormatException nfe) {
		}
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
