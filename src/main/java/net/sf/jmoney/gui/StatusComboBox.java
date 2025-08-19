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
import javax.swing.*;
import javax.swing.border.*;

public class StatusComboBox extends JComboBox {

	public StatusComboBox(Object[] items) {
		super(items);
		setRenderer(new Renderer());
	}

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

			setBorder(emptyBorder);

			String text = (String) value;
			if (index >= 0)
				setText(text);
			else
				setText(text.substring(0, 1)); // ...

			return this;
		}
	}
}
