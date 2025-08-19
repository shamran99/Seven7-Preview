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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.swing.JComboBox;

public class SelectionComboBox extends JComboBox {

	/**
	 * Constructor SelectionComboBox.
	 */
	public SelectionComboBox() {
		super();
	}

	/**
	 * Constructor SelectionComboBox.
	 * 
	 * @param strings
	 */
	public SelectionComboBox(String[] strings) {
		super(strings);
	}

	protected PropertyChangeSupport listSelectionSupport = new PropertyChangeSupport(this);

	public void setSelectedIndexWithoutEvent(int index) {
		setSelectedItemWithoutEvent(dataModel.getElementAt(index));
	}

	public void setSelectedItem(Object obj) {
		setSelectedItemWithoutEvent(obj);
		listSelectionSupport.firePropertyChange("selectedItem", null, obj);
	}

	public void setSelectedItemWithoutEvent(Object obj) {
		super.setSelectedItem(obj);
	}

	public void addElementSelectionListener(PropertyChangeListener pcl) {
		listSelectionSupport.addPropertyChangeListener(pcl);
	}

	public void removeElementSelectionListener(PropertyChangeListener pcl) {
		listSelectionSupport.removePropertyChangeListener(pcl);
	}

}
