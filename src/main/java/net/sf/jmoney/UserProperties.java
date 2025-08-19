/*
 *
 *  JMoney - A Personal Finance Manager
 *  Copyright (c) 2000-2003 Johann Gyger <johann.gyger@switzerland.org>
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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Properties;
import javax.swing.UIManager;

public class UserProperties {

	private Properties properties;

	public void setProperties(Properties p) {
		properties = p;
	}

	public void setLookAndFeel(String lnf) {
		String lookAndFeel = getLookAndFeel();
		if (lookAndFeel != null && lookAndFeel.equals(lnf))
			return;
		properties.setProperty("lookAndFeel", lnf);
		changeSupport.firePropertyChange("lookAndFeel", lookAndFeel, lnf);
	}

	public void setDateFormat(String df) {
		String dateFormat = getDateFormat();
		if (dateFormat != null && dateFormat.equals(df))
			return;
		properties.setProperty("dateFormat", df);
		changeSupport.firePropertyChange("dateFormat", dateFormat, df);
	}

	public void setDefaultCurrency(String dc) {
		String defaultCurrency = getDefaultCurrency();
		if (defaultCurrency != null && defaultCurrency.equals(dc))
			return;
		properties.setProperty("defaultCurrency", dc);
		changeSupport.firePropertyChange("defaultCurrency", defaultCurrency, dc);
	}

	public void setEntryStyle(String es) {
		String entryStyle = getEntryStyle();
		if (entryStyle != null && entryStyle.equals(es))
			return;
		properties.setProperty("entryStyle", es);
		changeSupport.firePropertyChange("entryStyle", entryStyle, es);
	}

	public void setEntryOrder(String field, String order) {
		properties.setProperty("entryOrderField", field);
		properties.setProperty("entryOrder", order);
		changeSupport.firePropertyChange("entryOrder", field, order);
	}

	public String getLookAndFeel() {
		return properties.getProperty("lookAndFeel", UIManager.getSystemLookAndFeelClassName());
	}

	public String getDateFormat() {
		return properties.getProperty("dateFormat", "yyyy-MM-dd");
	}

	public String getDefaultCurrency() {
		return properties.getProperty("defaultCurrency", "LKR");
	}

	public String getEntryStyle() {
		return properties.getProperty("entryStyle", "Extended");
	}

	public String getEntryOrderField() {
		return properties.getProperty("entryOrderField", "Date");
	}

	public String getEntryOrder() {
		return properties.getProperty("entryOrder", "Ascending");
	}

	private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

	public void addPropertyChangeListener(PropertyChangeListener pcl) {
		changeSupport.addPropertyChangeListener(pcl);
	}

	public void removePropertyChangeListener(PropertyChangeListener pcl) {
		changeSupport.removePropertyChangeListener(pcl);
	}

}
