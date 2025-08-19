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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import net.sf.jmoney.model.Account;
import net.sf.jmoney.model.Entry;

public class EntryFilter {

	public static String[] filterTypes() {
		String[] filterType = { Constants.LANGUAGE.getString("EntryFilter.entryContains"),
				Constants.LANGUAGE.getString("EntryFilter.amountContains"),
				Constants.LANGUAGE.getString("EntryFilter.categoryContains"),"Item Code Contains" };
		return filterType;
	}

	private String pattern = "";

	private String searchPattern = "";

	private int type = 0;

	protected transient PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

	public EntryFilter() {
	}

	/**
	 * Returns the pattern.
	 * 
	 * @return String
	 */
	public String getPattern() {
		return pattern;
	}

	/**
	 * Sets the pattern.
	 * 
	 * @param pattern
	 *            The pattern to set
	 */
	public void setPattern(String aPattern) {
		if (aPattern == null)
			aPattern = "";
		if (!aPattern.equals(pattern)) {
			pattern = aPattern;
			changeSupport.firePropertyChange("pattern", null, null);
		}
	}


	public void setSearchPattern(String searchPattern) {
		if (searchPattern == null)
			searchPattern ="";
		if (!searchPattern.equals(this.searchPattern)) {
			this.searchPattern = searchPattern;
			changeSupport.firePropertyChange("searchPattern", null, null);
		}

	}

	public String getSearchPattern() {
		return searchPattern;
	}

	/**
	 * Returns the type.
	 * 
	 * @return int
	 */
	public int getType() {
		return type;
	}

	/**
	 * Sets the type.
	 * 
	 * @param type
	 *            The type to set
	 */
	public void setType(int aType) {
		if (type == aType)
			return;
		type = aType;
		changeSupport.firePropertyChange("type", null, null);
	}

	public boolean isEmpty() {
		return pattern.length() == 0;
	}

	public boolean filterEntry(Entry entry, Account account, VerySimpleDateFormat dateFormat, int filterType) {
		switch (filterType) {
		case 0:
			return checkEntry(entry, account, dateFormat);
		case 1:
			return checkAmount(entry, account);
		case 2:
			return checkCategory(entry);
		case 3:
			return checkItemCode(entry);
		case 4:
			return checkDate(entry, dateFormat);
		case 5:
			return checkDescription(entry);
		case 6:
			return checkMemo(entry);
		case 7:
			return checkValuta(entry, dateFormat);
		default:
			return true;
		}
	}

	public boolean checkEntry(Entry entry, Account account, VerySimpleDateFormat df) {
		return pattern.equals("") || checkAmount(entry, account) || checkCategory(entry)
				|| checkDate(entry, df) || checkDescription(entry);
	}

	public boolean checkAmount(Entry e, Account account) {
		return contains(account.formatAmount(e.getAmount()));
	}

	public boolean checkCategory(Entry e) {
		String s = e.getCategory() != null ? e.getCategory().getFullCategoryName() : null;
		boolean isCategoryMatch = categoryContains(s);
		return (isCategoryMatch && searchPattern.length() != 0) ? checkSearchPattern(e) : isCategoryMatch;
	}

	private boolean checkSearchPattern(Entry e){
		String description = e.getDescription();
		String itemCode = e.getItemCode();

		// to be implemented later
		/*Date date = e.getDate();
		long amount = e.getAmount();*/
		boolean isDescription = description != null && description.toLowerCase().indexOf(searchPattern.toLowerCase()) >= 0;
		if(!isDescription){
			return itemCode != null && itemCode.toLowerCase().indexOf(searchPattern.toLowerCase()) >= 0;
		} else{
			return isDescription;
		}
	}

	public boolean checkCheck(Entry e) {
		return contains(e.getCheck());
	}

	public boolean checkItemCode(Entry e) {
		return contains(e.getItemCode());
	}

	public boolean checkDate(Entry e, VerySimpleDateFormat df) {
		return contains(df.format(e.getDate()));
	}

	public boolean checkDescription(Entry e) {
		return contains(e.getDescription());
	}

	public boolean checkMemo(Entry e) {
		return contains(e.getMemo());
	}

	public boolean checkValuta(Entry e, VerySimpleDateFormat df) {
		return contains(df.format(e.getValuta()));
	}

	private boolean contains(String s) {
		if (isEmpty())
			return true;
		else
			return (s != null) && (s.toLowerCase().indexOf(pattern.toLowerCase()) >= 0);
	}
	
	/**
	 * Category search only
	 * @param s
	 * @return
	 */
	private boolean categoryContains(String s) {
		if (isEmpty())
			return s == null;
		else
			return (s != null) && s.equals(pattern);
	}

	/**
	 * Adds a PropertyChangeListener.
	 */
	public void addPropertyChangeListener(PropertyChangeListener pcl) {
		changeSupport.addPropertyChangeListener(pcl);
	}

	/**
	 * Removes a PropertyChangeListener.
	 */
	public void removePropertyChangeListener(PropertyChangeListener pcl) {
		changeSupport.removePropertyChangeListener(pcl);
	}

}
