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

import java.util.Comparator;
import java.util.Date;

import net.sf.jmoney.model.Entry;

public class EntryComparator implements Comparator<Object> {

	private boolean ascending = true;

	private void setOrder(String order) {
		if (order.equals("Ascending"))
			ascending = true;
		else if (order.equals("Descending"))
			ascending = false;
		else
			System.err.println("preferences/entrySortOrder - invalid value: " + order);
	}

	public EntryComparator getInstance(String field, String order) {
		EntryComparator result;

		if (field.equals("Creation"))
			result = this;
		else if (field.equals("Check"))
			result = new CheckComparator();
		else if (field.equals("Date"))
			result = new DateComparator();
		else if (field.equals("Valuta"))
			result = new ValutaComparator();
		else if (field.equals("Description"))
			result = new DescriptionComparator();
		else if (field.equals("Category"))
			result = new CategoryComparator();
		else if (field.equals("Amount"))
			result = new AmountComparator();
		else if (field.equals("Status"))
			result = new StatusComparator();
		else if (field.equals("Memo"))
			result = new MemoComparator();
		else {
			System.err.println("preferences/entrySortField - invalid value: " + field);
			result = this;
		}
		result.setOrder(order);
		return result;
	}

	public int compare(Object o1, Object o2) {
		Entry e1 = (Entry) o1;
		Entry e2 = (Entry) o2;
		int result = compareEntries(e1, e2);
		if (result == 0)
			return longCompare(e1.getCreation(), e2.getCreation());
		else
			return ascending ? result : -result;
	}

	protected int compareEntries(Entry e1, Entry e2) {
		return 0;
	}

	private int longCompare(long n1, long n2) {
		return n1 < n2 ? -1 : (n1 == n2 ? 0 : 1);
	}

	private int stringCompare(String s1, String s2) {
		if (s1 == null && s2 == null)
			return 0;
		if (s1 == null)
			return -1;
		if (s2 == null)
			return 1;
		return s1.compareTo(s2);
	}

	private int dateCompare(Date d1, Date d2) {
		if (d1 == null && d2 == null)
			return 0;
		if (d1 == null)
			return 1;
		if (d2 == null)
			return -1;
		return d1.compareTo(d2);
	}

	public class CheckComparator extends EntryComparator {
		protected int compareEntries(Entry e1, Entry e2) {
			return stringCompare(e1.getCheck(), e2.getCheck());
		}
	}

	public class DateComparator extends EntryComparator {
		protected int compareEntries(Entry e1, Entry e2) {
			return dateCompare(e1.getDate(), e2.getDate());
		}
	}

	public class ValutaComparator extends EntryComparator {
		protected int compareEntries(Entry e1, Entry e2) {
			return dateCompare(e1.getValuta(), e2.getValuta());
		}
	}

	public class DescriptionComparator extends EntryComparator {
		protected int compareEntries(Entry e1, Entry e2) {
			return stringCompare(e1.getDescription(), e2.getDescription());
		}
	}

	public class CategoryComparator extends EntryComparator {
		protected int compareEntries(Entry e1, Entry e2) {
			return stringCompare(e1.getFullCategoryName(), e2.getFullCategoryName());
		}
	}

	public class AmountComparator extends EntryComparator {
		protected int compareEntries(Entry e1, Entry e2) {
			return longCompare(e1.getAmount(), e2.getAmount());
		}
	}

	public class StatusComparator extends EntryComparator {
		protected int compareEntries(Entry e1, Entry e2) {
			return longCompare(e2.getStatus(), e1.getStatus());
		}
	}

	public class MemoComparator extends EntryComparator {
		protected int compareEntries(Entry e1, Entry e2) {
			return stringCompare(e1.getMemo(), e2.getMemo());
		}
	}

}
