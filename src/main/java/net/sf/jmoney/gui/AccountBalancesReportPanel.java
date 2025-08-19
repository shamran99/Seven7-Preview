package net.sf.jmoney.gui;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JRSaveContributor;
import net.sf.jasperreports.view.JRViewer;
import net.sf.jmoney.AccountBalanceFilter;
import net.sf.jmoney.Constants;
import net.sf.jmoney.DateLabelFormatter;
import net.sf.jmoney.VerySimpleDateFormat;
import net.sf.jmoney.model.Account;
import net.sf.jmoney.model.Entry;
import net.sf.jmoney.model.Session;
import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilDateModel;
import org.joda.time.DateTimeComparator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.function.Function;

/**
 * @author ssiddique
 *
 */
public class AccountBalancesReportPanel extends JPanel {

	public static final int ALL_ENTRIES = 0;

	public static final int DATE = 1;

	public static final String[] filters = { Constants.LANGUAGE.getString("Report.AccountBalances.AllEntries"),
			Constants.LANGUAGE.getString("Entry.date") };

	private JPanel reportPanel;
	private JPanel controlPanel = new JPanel();
	private JButton generateButton = new JButton();
	private JLabel filterLabel = new JLabel();
	private JComboBox filterBox = new JComboBox(filters);
	private JLabel dateLabel = new JLabel();
	private Date allEntriesDate = new Date();
	/*
	 * AccountBalanceFilter is for filtering the accounts that are need to be
	 * accumulated
	 */
	AccountBalanceFilter accountBalanceFilter = new AccountBalanceFilter();

	/**
	 * DateField added
	 */
	UtilDateModel dateModel = new UtilDateModel(new Date());
	JDatePanelImpl datePanel = new JDatePanelImpl(dateModel);
	JDatePickerImpl datePicker;

	private Session session;
	private VerySimpleDateFormat dateFormat;
	private Date date;
	private final JButton editAccounts = new JButton("Edit Accounts");
	private final JTextField exRate_1 = new JTextField();

	public AccountBalancesReportPanel() {
		try {
			datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());
			jbInit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setSession(Session aSession) {
		session = aSession;
		accountBalanceFilter.setSession(aSession);
		exRate_1.setText(aSession.getExRate());
	}

	public void setDateFormat(String pattern) {
		dateFormat = new VerySimpleDateFormat(pattern);
	}

	private void generateReport() {
		if (reportPanel != null) {
			remove(reportPanel);
			updateUI();
		}
		try {
			URL url = Constants.class.getResource("resources/AccountBalances.jasper");
			InputStream is = url.openStream();
			double exRate = readExRate(exRate_1.getText());

			Map params = new HashMap();
			params.put("Title", Constants.LANGUAGE.getString("Report.AccountBalances.Title"));

			params.put("Total", Constants.LANGUAGE.getString("Report.Total"));
			params.put("Account", Constants.LANGUAGE.getString("Report.AccountBalances.Account"));
			params.put("Balance", Constants.LANGUAGE.getString("Report.AccountBalances.Balance"));
			params.put("DateToday", dateFormat.format(new Date()));
			params.put("Page", Constants.LANGUAGE.getString("Report.Page"));
			params.put("ExRate", exRate);
			params.put("TotalLKR", String.format("Grand Total LKR (Rate: %s)",exRate));

			Collection items = getItems();
			if (items.isEmpty()) {
				JOptionPane.showMessageDialog(this, Constants.LANGUAGE.getString("Panel.Report.EmptyReport.Message"),
						Constants.LANGUAGE.getString("Panel.Report.EmptyReport.Title"), JOptionPane.ERROR_MESSAGE);
			} else {
				JRDataSource ds = new JRBeanCollectionDataSource(items);
				params.put("Subtitle", getSubtitle());
				JasperPrint print = JasperFillManager.fillReport(is, params, ds);
				reportPanel = makeCsvDefault(new JRViewer(print));

				add(reportPanel, BorderLayout.CENTER);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		updateUI();
	}

	private double readExRate(String text) {
		double amount = 0.0;
		try {
			amount = Double.parseDouble(text);
			session.setExRate(text);
		} catch (NumberFormatException pex) {
		}
		return amount;
	}

	/**
	 * This is to set the default save extension to csv
	 * @param jrViewer
	 * @return
	 */
	private JRViewer makeCsvDefault(JRViewer jrViewer){
		JRSaveContributor[] saveContributors = jrViewer.getSaveContributors();
		List<JRSaveContributor> jrSaveContributors = Arrays.asList(saveContributors);
		JRSaveContributor csvSave = jrSaveContributors.stream().map(JRSaveContributor.class::cast).filter(x -> x.getDescription().contains(Constants.DEFAULT_SAVE_OPTION)).findFirst().get();
		saveContributors[jrSaveContributors.indexOf(csvSave)] = saveContributors[0];
		saveContributors[0] = csvSave;

		jrViewer.setSaveContributors(saveContributors);

		return jrViewer;
	}

	private String getSubtitle() {
		switch (filterBox.getSelectedIndex()) {
		case ALL_ENTRIES:
			return Constants.LANGUAGE.getString("Report.AccountBalances.AllEntries")+" "+dateFormat.format(allEntriesDate);
		case DATE:
			return dateFormat.format(date);
		default:
			return "";
		}
	}

	private Collection getItems() {
		Vector items = new Vector();
		Iterator aIt = session.getAccounts().listIterator();

		while (aIt.hasNext()) {
			Account account = (Account) aIt.next();

			Boolean checkBoxStatus = accountBalanceFilter.checkboxesStatus.get(account.getName());
			if (checkBoxStatus == null) {
				accountBalanceFilter.updateCheckboxesStatus();
				checkBoxStatus = accountBalanceFilter.checkboxesStatus.get(account.getName());
			}
			/*
			 * Following is to list the summation based on the edit accounts
			 * option
			 */
			if (!checkBoxStatus) {
				continue;
			}

			long bal = account.getStartBalance();

			Iterator eIt = account.getEntries().listIterator();
			while (eIt.hasNext()) {
				Entry e = (Entry) eIt.next();
				if (accept(e))
					bal += e.getAmount();
			}

			items.add(new Item(account, bal));
		}

		Collections.sort(items);
		return items;
	}

	private boolean accept(Entry entry) {
		switch (filterBox.getSelectedIndex()) {
		case ALL_ENTRIES:
			return setLatestDate(entry.getDate());
		case DATE:
			return acceptTo(entry.getDate());
		}
		return true;
	}

	private boolean setLatestDate(Date d){
		if(allEntriesDate.before(d)){
			allEntriesDate = d;
		}
		return true;
	}

	private boolean acceptTo(Date d) {
		if (date == null)
			return true;
		if (d == null)
			return false;
		int compare = DateTimeComparator.getDateOnlyInstance().compare(date, d);
		return (d.before(date) || compare == 0);
	}

	private void updateFilter() {
		disableDateField(filterBox.getSelectedIndex() == DATE, datePicker);
	}

	private void updateDate() {
		date = dateModel.getValue();
	}

	private void jbInit() throws Exception {
		exRate_1.setColumns(7);
		setLayout(new BorderLayout());

		filterLabel.setText(Constants.LANGUAGE.getString("EntryFilterPanel.filter"));
		filterBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateFilter();
			}
		});

		dateLabel.setText(Constants.LANGUAGE.getString("Entry.date"));

		datePicker.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateDate();
			}
		});
		datePicker.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				updateDate();
			}
		});

		generateButton.setText(Constants.LANGUAGE.getString("Panel.Report.Generate"));
		generateButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				generateReport();
			}
		});

		controlPanel.setBorder(BorderFactory.createEtchedBorder());
		controlPanel.setLayout(new GridBagLayout());
		add(controlPanel, BorderLayout.SOUTH);

		controlPanel.add(filterLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(6, 6, 3, 5), 0, 0));
		controlPanel.add(filterBox, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(6, 6, 3, 5), 0, 0));
		controlPanel.add(dateLabel, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(6, 6, 3, 5), 0, 0));
		controlPanel.add(datePicker, new GridBagConstraints(3, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(6, 6, 3, 5), 0, 0));
		controlPanel.add(exRate_1, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.NONE, new Insets(6, 12, 3, 5), 0, 0));
		controlPanel.add(generateButton, new GridBagConstraints(5, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.NONE, new Insets(6, 12, 3, 5), 0, 0));
		controlPanel.add(editAccounts, new GridBagConstraints(6, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.NONE, new Insets(6, 12, 3, 5), 0, 0));

		editAccounts.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Edit accounts btn pressed
				accountBalanceFilter.setUpPopupbox();
			}
		});
		disableDateField(false, datePicker);
	}

	public class Item implements Comparable {

		private Account account;

		private long balance;

		public Item(Account anAccount, long aBalance) {
			account = anAccount;
			balance = aBalance;
		}

		public Account getAccount() {
			return account;
		}

		public String getAccountName() {
			return account.getName();
		}

		public Long getBalance() {
			return new Long(balance);
		}

		public String getBalanceString() {
			return account.formatAmount(balance);
		}

		public void addToBalance(long amount) {
		}

		public String getCurrencyCode() {
			return account.getCurrencyCode();
		}

		public int compareTo(Object o) {
			return Comparator.comparing(Item::getCurrencyCode).thenComparing(Item::getBalance,Comparator.reverseOrder()).compare(this, (Item) o);
		}
	}

	private void disableDateField(boolean status, JDatePickerImpl field) {
		Component[] components = controlPanel.getComponents();
		for (Component c : components) {
			if (c.getClass() == field.getClass()) {
				JDatePickerImpl datePick = (JDatePickerImpl) c;
				Component[] components2 = datePick.getComponents();
				for (Component c2 : components2) {
					c2.setEnabled(status);
				}
			}

		}
	}
}