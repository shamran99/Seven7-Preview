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

import com.sun.java.swing.plaf.motif.*;
import com.sun.java.swing.plaf.windows.*;
import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import javax.swing.plaf.metal.*;
import net.sf.jmoney.*;

public class DateComboBox extends SelectionComboBox {

	protected VerySimpleDateFormat dateFormat;

	public void setDateFormat(VerySimpleDateFormat dateFormat) {
		this.dateFormat = dateFormat;
	}

	public void setSelectedItem(Object item) {
		removeAllItems(); // hides the popup if visible
		addItem(item);
		super.setSelectedItem(item);
	}

	public void updateUI() {
		ComboBoxUI cui = (ComboBoxUI) UIManager.getUI(this);
		if (cui instanceof MetalComboBoxUI) {
			cui = new MetalDateComboBoxUI();
		} else if (cui instanceof MotifComboBoxUI) {
			cui = new MotifDateComboBoxUI();
		} else if (cui instanceof WindowsComboBoxUI) {
			cui = new WindowsDateComboBoxUI();
		}
		setUI(cui);
	}

	class MetalDateComboBoxUI extends MetalComboBoxUI {
		protected ComboPopup createPopup() {
			return new DatePopup(comboBox);
		}
	}

	class WindowsDateComboBoxUI extends WindowsComboBoxUI {
		protected ComboPopup createPopup() {
			return new DatePopup(comboBox);
		}
	}

	class MotifDateComboBoxUI extends MotifComboBoxUI {
		protected ComboPopup createPopup() {
			return new DatePopup(comboBox);
		}
	}

	class DatePopup implements ComboPopup, MouseMotionListener, MouseListener, KeyListener, PopupMenuListener {

		protected JComboBox comboBox;
		protected Calendar calendar;
		protected JPopupMenu popup;
		protected JLabel monthLabel;
		protected JPanel days = null;
		protected SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM yyyy");

		protected Color selectedBackground;
		protected Color selectedForeground;
		protected Color background;
		protected Color foreground;

		protected JList list = new JList();

		protected boolean mouseInside = false;
		protected boolean hideNext = false;

		public DatePopup(JComboBox comboBox) {
			this.comboBox = comboBox;
			calendar = Calendar.getInstance();

			background = UIManager.getColor("ComboBox.background");
			foreground = UIManager.getColor("ComboBox.foreground");
			selectedBackground = UIManager.getColor("ComboBox.selectionBackground");
			selectedForeground = UIManager.getColor("ComboBox.selectionForeground");

			initializePopup();
		}

		/**
		 * ComboPopup implementation
		 */
		public KeyListener getKeyListener() {
			return this;
		}

		/**
		 * ComboPopup implementation
		 */
		public JList getList() {
			return list;
		}

		/**
		 * ComboPopup implementation
		 */
		public MouseListener getMouseListener() {
			return this;
		}

		/**
		 * ComboPopup implementation
		 */
		public MouseMotionListener getMouseMotionListener() {
			return this;
		}

		/**
		 * ComboPopup implementation
		 */
		public void hide() {
			popup.setVisible(false);
		}

		/**
		 * ComboPopup implementation
		 */
		public boolean isVisible() {
			return popup.isVisible();
		}

		/**
		 * ComboPopup implementation
		 */
		public void show() {
			Object item = comboBox.getSelectedItem();
			if (item != null) {
				Date d = dateFormat.parse(item.toString());
				if (d != null)
					calendar.setTime(d);
			}
			updatePopup();
			popup.show(comboBox, 0, comboBox.getHeight());
		}

		/**
		 * ComboPopup implementation
		 */
		public void uninstallingUI() {
			popup.removePopupMenuListener(this);
		}

		/**
		 * KeyListener implementation
		 */
		public void keyPressed(KeyEvent e) {
		}

		/**
		 * KeyListener implementation
		 */
		public void keyTyped(KeyEvent e) {
		}

		/**
		 * KeyListener implementation
		 */
		public void keyReleased(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_ENTER) {
				togglePopup();
			}
		}

		/**
		 * MouseListener implementation
		 */
		public void mouseClicked(MouseEvent e) {
			if (!SwingUtilities.isLeftMouseButton(e))
				return;
			if (!comboBox.isEnabled())
				return;
			if (comboBox.isEditable()) {
				comboBox.getEditor().getEditorComponent().requestFocus();
			} else {
				comboBox.requestFocus();
			}
			togglePopup();
		}

		/**
		 * MouseListener implementation
		 */
		public void mouseEntered(MouseEvent e) {
			mouseInside = true;
		}

		/**
		 * MouseListener implementation
		 */
		public void mouseExited(MouseEvent e) {
			mouseInside = false;
		}

		/**
		 * MouseListener implementation
		 */
		public void mousePressed(MouseEvent e) {
		}

		/**
		 * MouseListener implementation
		 */
		public void mouseReleased(MouseEvent e) {
		}

		/**
		 * MouseMotionListener implementation
		 */
		public void mouseDragged(MouseEvent e) {
		}

		/**
		 * MouseMotionListener implementation
		 */
		public void mouseMoved(MouseEvent e) {
		}

		public void popupMenuCanceled(PopupMenuEvent e) {
		}

		public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
			hideNext = mouseInside;
		}

		public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
		}

		protected void togglePopup() {
			if (isVisible() || hideNext)
				hide();
			else
				show();
			hideNext = false;
		}

		protected JLabel createUpdateButton(final int field, final int amount) {
			final JLabel label = new JLabel();
			final Border selectedBorder = new EtchedBorder();
			final Border unselectedBorder = new EmptyBorder(selectedBorder.getBorderInsets(new JLabel()));
			label.setBorder(unselectedBorder);
			label.setForeground(foreground);
			label.addMouseListener(new MouseAdapter() {
				public void mouseReleased(MouseEvent e) {
					calendar.add(field, amount);
					updatePopup();
				}

				public void mouseEntered(MouseEvent e) {
					label.setBorder(selectedBorder);
				}

				public void mouseExited(MouseEvent e) {
					label.setBorder(unselectedBorder);
				}
			});
			return label;
		}

		protected void initializePopup() {
			JPanel header = new JPanel(); // used Box, but it wasn't Opaque
			header.setLayout(new BoxLayout(header, BoxLayout.X_AXIS));
			header.setBackground(background);
			header.setOpaque(true);

			JLabel label;
			label = createUpdateButton(Calendar.YEAR, -1);
			label.setText("<<");
			label.setToolTipText("Previous Year");

			header.add(Box.createHorizontalStrut(12));
			header.add(label);
			header.add(Box.createHorizontalStrut(12));

			label = createUpdateButton(Calendar.MONTH, -1);
			label.setText("<");
			label.setToolTipText("Previous Month");
			header.add(label);

			monthLabel = new JLabel("", JLabel.CENTER);
			monthLabel.setForeground(foreground);
			header.add(Box.createHorizontalGlue());
			header.add(monthLabel);
			header.add(Box.createHorizontalGlue());

			label = createUpdateButton(Calendar.MONTH, 1);
			label.setText(">");
			label.setToolTipText("Next Month");
			header.add(label);

			label = createUpdateButton(Calendar.YEAR, 1);
			label.setText(">>");
			label.setToolTipText("Next Year");

			header.add(Box.createHorizontalStrut(12));
			header.add(label);
			header.add(Box.createHorizontalStrut(12));

			popup = new JPopupMenu();
			popup.setBorder(BorderFactory.createLineBorder(Color.black));
			popup.setLayout(new BorderLayout());
			popup.setBackground(background);
			popup.addPopupMenuListener(this);
			popup.add(BorderLayout.NORTH, header);
		}

		protected void updatePopup() {
			monthLabel.setText(monthFormat.format(calendar.getTime()));
			if (days != null)
				popup.remove(days);

			days = new JPanel(new GridLayout(0, 7));
			days.setBackground(background);
			days.setOpaque(true);

			Calendar setupCalendar = (Calendar) calendar.clone();
			setupCalendar.set(Calendar.DAY_OF_WEEK, setupCalendar.getFirstDayOfWeek());
			for (int i = 0; i < 7; i++) {
				int dayInt = setupCalendar.get(Calendar.DAY_OF_WEEK);
				JLabel label = new JLabel();
				label.setHorizontalAlignment(JLabel.CENTER);
				label.setForeground(foreground);
				if (dayInt == Calendar.SUNDAY) {
					label.setText("Sun");
				} else if (dayInt == Calendar.MONDAY) {
					label.setText("Mon");
				} else if (dayInt == Calendar.TUESDAY) {
					label.setText("Tue");
				} else if (dayInt == Calendar.WEDNESDAY) {
					label.setText("Wed");
				} else if (dayInt == Calendar.THURSDAY) {
					label.setText("Thu");
				} else if (dayInt == Calendar.FRIDAY) {
					label.setText("Fri");
				} else if (dayInt == Calendar.SATURDAY) {
					label.setText("Sat");
				}
				days.add(label);
				setupCalendar.roll(Calendar.DAY_OF_WEEK, true);
			}

			setupCalendar = (Calendar) calendar.clone();
			setupCalendar.set(Calendar.DAY_OF_MONTH, 1);
			int first = setupCalendar.get(Calendar.DAY_OF_WEEK);
			for (int i = 0; i < (first - 1); i++)
				days.add(new JLabel(""));
			for (int i = 1; i <= setupCalendar.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
				final int day = i;
				final JLabel label = new JLabel(String.valueOf(day));
				label.setHorizontalAlignment(JLabel.CENTER);
				label.setForeground(foreground);
				label.addMouseListener(new MouseListener() {
					public void mousePressed(MouseEvent e) {
					}

					public void mouseClicked(MouseEvent e) {
					}

					public void mouseReleased(MouseEvent e) {
						label.setOpaque(false);
						label.setBackground(background);
						label.setForeground(foreground);
						calendar.set(Calendar.DAY_OF_MONTH, day);
						comboBox.setSelectedItem(dateFormat.format(calendar.getTime()));
						comboBox.requestFocus();
					}

					public void mouseEntered(MouseEvent e) {
						label.setOpaque(true);
						label.setBackground(selectedBackground);
						label.setForeground(selectedForeground);
					}

					public void mouseExited(MouseEvent e) {
						label.setOpaque(false);
						label.setBackground(background);
						label.setForeground(foreground);
					}
				});
				days.add(label);
			}
			popup.add(BorderLayout.CENTER, days);
			popup.pack();
		}
	}

}
