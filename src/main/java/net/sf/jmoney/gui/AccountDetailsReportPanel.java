package net.sf.jmoney.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.List;

import net.sf.jasperreports.view.JRSaveContributor;

import javax.swing.*;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JRViewer;
import net.sf.jmoney.*;
import net.sf.jmoney.Currency;
import net.sf.jmoney.model.Account;
import net.sf.jmoney.model.Category;
import net.sf.jmoney.model.Entry;
import net.sf.jmoney.model.Session;
import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilDateModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTimeComparator;

public class AccountDetailsReportPanel extends JPanel {

	public static final int THIS_MONTH = 0;

	public static final int THIS_YEAR = 1;

	public static final int LAST_MONTH = 2;

	public static final int LAST_YEAR = 3;
	
	public static final int ALL_RECORDS = 4;

	public static final int SELECT_MONTH = 5;

	public static final int CUSTOM = 6;

	public static final String ALL_ACCOUNTS = "All Accounts";

	private static final Logger logger = LogManager.getLogger(AccountDetailsReportPanel.class);

	public static final String[] periods = { Constants.LANGUAGE.getString("Panel.Report.IncomeExpense.thisMonth"),
			Constants.LANGUAGE.getString("Panel.Report.IncomeExpense.thisYear"),
			Constants.LANGUAGE.getString("Panel.Report.IncomeExpense.lastMonth"),
			Constants.LANGUAGE.getString("Panel.Report.IncomeExpense.lastYear"),
			Constants.LANGUAGE.getString("Panel.Report.IncomeExpense.allRecords"),
			"Select Month",
			Constants.LANGUAGE.getString("Panel.Report.IncomeExpense.custom") };
	public static final String[] currencies = { "ALL","LKR","GBP" };

	private JPanel reportPanel;
	private JPanel controlPanel = new JPanel();
	private JButton generateButton = new JButton();
	private JLabel periodLabel = new JLabel();
	private JComboBox periodBox = new JComboBox(periods);
	private JComboBox currencyBox = new JComboBox(currencies);

	/**
	 * From date field
	 */
	UtilDateModel fromDateModel = new UtilDateModel(new Date());
	JDatePanelImpl fromDatePanel = new JDatePanelImpl(fromDateModel);
	JDatePickerImpl fromDateField;

	/**
	 * To date field
	 */
	UtilDateModel toDateModel = new UtilDateModel(new Date());
	JDatePanelImpl toDatePanel = new JDatePanelImpl(toDateModel);
	JDatePickerImpl toDateField;

	private JLabel fromLabel = new JLabel();
	private JLabel toLabel = new JLabel();
	private JLabel categoryLabel = new JLabel();
	private CategoryComboBox categoryBox = new CategoryComboBox();
	private JTextField searchBox = new JTextField();
	private Category selectedCategoryType;
	private String keyword;
	private int selectedCategoryIndex;

	private Session session;
	private VerySimpleDateFormat dateFormat;
	private Date fromDate;
	private Date toDate;
	private final JLabel accountLabel = new JLabel();
	private JComboBox accountBox = new JComboBox(new Vector());
	private final JCheckBox includeTrans = new JCheckBox("Include Transaction");

	public AccountDetailsReportPanel() {
		try {
			fromDateField = new JDatePickerImpl(fromDatePanel, new DateLabelFormatter());
			toDateField = new JDatePickerImpl(toDatePanel, new DateLabelFormatter());
			jbInit();
		} catch (Exception e) {
			logger.error("Found exception when processing AccountDetailsReportPanel(). Details: {}",e.getStackTrace());
		}
	}

	/**
	 * The following method will set the data model to the account combo box.
	 */
	public void setAccountSelector() {
		Object selectedItem = accountBox.getSelectedItem();
		accountBox.removeAllItems();
		Iterator aIt = session.getAccounts().listIterator();

		accountBox.addItem(ALL_ACCOUNTS);
		while (aIt.hasNext()) {
			Account account = (Account) aIt.next();
			accountBox.addItem(account.getName());
		}
		if (selectedItem != null)
			accountBox.setSelectedItem(selectedItem);
	}

	/**
	 * The following method will set the data model to the category combo box.
	 */
	public void setCategorySelector() {
		Object selectedItem = categoryBox.getSelectedItem();
		categoryBox.setModel(session.getCategories());
		categoryBox.setSelectedItem(selectedItem);
	}

	public void setSession(Session aSession) {
		session = aSession;
	}

	public void setDateFormat(String pattern) {
		dateFormat = new VerySimpleDateFormat(pattern);
		updateFromAndTo();
	}

	private String safeString(Object value){
		if(value instanceof Long){
			// Step 1: Perform division and cast to double
			double result = (long)value / 100.0;

			// Step 2: Format to two decimal places
			value = String.format("%.2f", result);
		}
		return value == null ? "" : value.toString();
	}

	private void generateCSV(Collection items) {
		// Output path
		List<Item> items_ = new ArrayList<>(items);
		String outputPath = Constants.EXTERNAL_PROPERTY.getString("account.report.path");

		try (FileWriter writer = new FileWriter(outputPath)) {
			// Write CSV header
			writer.append("Date,Category,Description,Income,Expense,Order Number, Quantity, Cost\n");

			// Write data rows
			for (Item item : items_) {
				writer.append(item.getDate())
						.append(",")
						.append(safeString(item.getCategory()))
						.append(",")
						.append(safeString(item.getDescription()))
						.append(",")
						.append(safeString(item.getIncome()))
						.append(",")
						.append(safeString(item.getExpense()))
						.append(",")
						.append(safeString(item.getItemCode()))
						.append(",")
						.append(safeString(item.getQuantity()))
						.append(",")
						.append(safeString(item.getCost()))
						.append("\n");
			}

			logger.info("CSV File written to path: {}",outputPath);
		} catch (IOException e) {
                        throw new RuntimeException(e);
                    }
          }

	private void generateReport() {
		if (reportPanel != null) {
			remove(reportPanel);
			updateUI();
		}
		try {
			String reportFile = "resources/AccountDetails.jasper";

			URL url = Constants.class.getResource(reportFile);
			InputStream is = url.openStream();

			Map params = new HashMap();
			params.put("Balance", Constants.LANGUAGE.getString("Report.AccountReport.Balance"));
			params.put("Date", Constants.LANGUAGE.getString("Report.AccountReport.Date"));
			String categoryTitle = (categoryBox.getSelectedIndex() == 0 || categoryBox.getSelectedIndex() == -1) ? ""
					: " (" + categoryBox.getSelectedItem().toString() + ")";
			params.put("Title", String.format(Constants.LANGUAGE.getString("Report.AccountReport.Title"),
					accountBox.getSelectedItem().toString() + categoryTitle));

			keyword = searchBox.getText();
			String keywordTitle = "";
			if(!"".equals(keyword)){
				keywordTitle = " || key: ";
				keywordTitle += keyword.length() > 5 ? keyword.substring(0,5) + "..." : keyword;
			}
			params.put("Subtitle", dateFormat.format(fromDate) + " - " + dateFormat.format(toDate) + keywordTitle);

			params.put("Total", Constants.LANGUAGE.getString("Report.Total"));
			params.put("Category", Constants.LANGUAGE.getString("Report.AccountReport.Category"));
			params.put("Description", Constants.LANGUAGE.getString("Report.AccountReport.Description"));
			params.put("Income", Constants.LANGUAGE.getString("Report.AccountReport.Income"));
			params.put("Expense", Constants.LANGUAGE.getString("Report.AccountReport.Expense"));
			params.put("DateToday", dateFormat.format(new Date()));
			params.put("Page", Constants.LANGUAGE.getString("Report.Page"));
			params.put("AccountName", Constants.LANGUAGE.getString("Report.AccountName"));

			// Read report related user inputs
			selectedCategoryIndex = categoryBox.getSelectedIndex();
			selectedCategoryType = (Category) categoryBox.getSelectedItem();

			// Read entries
			Collection items = getItems();
			logger.info("Total number of records: {}",items.size());
			// Attempts to read the 'account.report.path' property from an external properties file.
			// A CSV file is created only if the property is defined.
			try {
				Constants.EXTERNAL_PROPERTY.getString("account.report.path");
				generateCSV(items);
			} catch (MissingResourceException e) {
				// CSV Download skipped.
			}



			if (items.isEmpty()) {
				JOptionPane.showMessageDialog(this, Constants.LANGUAGE.getString("Panel.Report.EmptyReport.Message"),
						Constants.LANGUAGE.getString("Panel.Report.EmptyReport.Title"), JOptionPane.ERROR_MESSAGE);
			} else {
				JRDataSource ds = new JRBeanCollectionDataSource(items);
				JasperPrint print = JasperFillManager.fillReport(is, params, ds);
				reportPanel = makeCsvDefault(new JRViewer(print));

				add(reportPanel, BorderLayout.CENTER);
			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this,
					"Exception occured during report generate. Details: " + ex.getMessage());
			logger.error("Found Exception: {}",ex.getStackTrace());
		}
		updateUI();
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

	private Collection getItems() {
		Vector allItems = new Vector();

		// This is to filter the Accounts
		Iterator aIt = session.getAccounts().listIterator();
		String selectedAccount = accountBox.getSelectedItem().toString();
		while (aIt.hasNext()) {
			Account a = (Account) aIt.next();
			String cc = a.getCurrencyCode();
			if (!selectedAccount.equals(ALL_ACCOUNTS) && !a.getName().equals(selectedAccount)) {
				continue;
			}

			// This is to filter based on currency selection
			if(validateCurrencies(cc)){
				continue;
			}

			addEntries(allItems, a.getCurrencyCode(), a.getEntries(), a.getName());
		}

		Collections.sort(allItems);
		return allItems;
	}

	/**
	 * Returns false if the account is with selected currency.
	 * @param cc - currency of the account
	 * @return true if not selected currency
	 */
	private boolean validateCurrencies(String cc) {
		return currencyBox.getSelectedIndex() == 0 ? false : !currencyBox.getSelectedItem().toString().equals(cc);
	}

	private void addEntries(Vector allItems, String currencyCode, Vector entries, String accountName) {
		Iterator eIt = entries.listIterator();
		while (eIt.hasNext()) {
			Entry e = (Entry) eIt.next();
			if (accept(e)) {
				Item i = new Item(e.getCategory(), currencyCode, e.getAmount(), e.getDescription(), e.getDate(),accountName, e.getItemCode(), e.getQuantity(), e.getCost());
				allItems.add(i);
			}
		}
	}

	private boolean acceptCategory(Entry e) {
		try {
			return (selectedCategoryIndex == 0 || selectedCategoryIndex == -1) ? filterTransaction(e.getCategory())
					: e.getCategory().equals(selectedCategoryType);
		} catch (NullPointerException ex) {
			// Exception caught when e.getCategory() returns null. In that case
			// we skip that particular entry.
			logger.warn("Found exception on getCategory(). Details: {}",e);
			return false;
		}
	}

	/*
	 * filterTransaction method is to filter the transactional entries only if
	 * requested not to include
	 */
	private boolean filterTransaction(Category category) {
		return (!includeTrans.isSelected() && category instanceof Account) ? false : true;
	}

	private boolean accept(Entry e) {
		return acceptFrom(e.getDate()) && acceptTo(e.getDate()) && acceptCategory(e) && acceptKeyword(e);
	}

	private boolean acceptKeyword(Entry e) {
		String text = e.getDescription();
		return "".equals(keyword) || (text != null && text.toLowerCase().contains(keyword.toLowerCase()));
	}

	private boolean acceptFrom(Date d) {
		if (fromDate == null)
			return false;
		if (d == null)
			return true;
		int compare = DateTimeComparator.getDateOnlyInstance().compare(fromDate, d);
		return (d.after(fromDate) || compare == 0);
	}

	private boolean acceptTo(Date d) {
		if (toDate == null)
			return true;
		if (d == null)
			return false;
		int compare = DateTimeComparator.getDateOnlyInstance().compare(toDate, d);
		return (d.before(toDate) || compare == 0);
	}

	private void updateFromAndTo() {
		int index = periodBox.getSelectedIndex();
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		switch (index) {
		case THIS_MONTH:
			cal.set(Calendar.DAY_OF_MONTH, 1);
			fromDate = cal.getTime();

			cal.add(Calendar.MONTH, 1);
			cal.add(Calendar.MILLISECOND, -1);
			toDate = cal.getTime();
			break;
		case THIS_YEAR:
			cal.set(Calendar.DAY_OF_MONTH, 1);
			cal.set(Calendar.MONTH, Calendar.JANUARY);
			fromDate = cal.getTime();

			cal.add(Calendar.YEAR, 1);
			cal.add(Calendar.MILLISECOND, -1);
			toDate = cal.getTime();
			break;
		case LAST_MONTH:
			cal.set(Calendar.DAY_OF_MONTH, 1);
			cal.add(Calendar.MONTH, -1);
			fromDate = cal.getTime();

			cal.add(Calendar.MONTH, 1);
			cal.add(Calendar.MILLISECOND, -1);
			toDate = cal.getTime();
			break;
		case LAST_YEAR:
			cal.set(Calendar.DAY_OF_MONTH, 1);
			cal.set(Calendar.MONTH, Calendar.JANUARY);
			cal.add(Calendar.YEAR, -1);
			fromDate = cal.getTime();

			cal.add(Calendar.YEAR, 1);
			cal.add(Calendar.MILLISECOND, -1);
			toDate = cal.getTime();
			break;
		case ALL_RECORDS:
			Date minDate = new Date();
			Date maxDate = new Date();
			try {
				minDate = (Date) session.getAccounts().stream().flatMap(x -> {
					Account a = (Account) x;
					return a.getEntries().stream();
				}).map(x -> {
					Entry e = (Entry) x;
					return e.getDate();
				}).min(Comparator.naturalOrder()).orElse(null);
				
				maxDate = (Date) session.getAccounts().stream().flatMap(x -> {
					Account a = (Account) x;
					return a.getEntries().stream();
				}).map(x -> {
					Entry e = (Entry) x;
					return e.getDate();
				}).max(Comparator.naturalOrder()).orElse(null);
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this,
						"Exception occuured when proesseing min date. Proceeding with current date. Details: "
								+ e.getMessage());
				logger.error("Found exception on updateFromAndTo(). Details: {}",e.getStackTrace());
			}

			cal.setTime(minDate);
			fromDate = cal.getTime();

			cal.setTime(maxDate);
			toDate = cal.getTime();
			break;
		default:
		}

		fromDateModel.setValue(fromDate);
		toDateModel.setValue(toDate);
		disableDateField(index == CUSTOM || index == SELECT_MONTH, toDateField);
	}

	private void updateFrom() {
		// Month selector code
		if(periodBox.getSelectedIndex() == SELECT_MONTH){
			fromDateModel.setDay(1);

			Calendar cal = Calendar.getInstance();
			cal.setTime(fromDateModel.getValue());
			cal.set(Calendar.DAY_OF_MONTH,cal.getActualMaximum(Calendar.DATE));

			toDateModel.setValue(cal.getTime());
			toDate = toDateModel.getValue();

		}
		fromDate = fromDateModel.getValue();
	}

	private void updateTo() {
		toDate = toDateModel.getValue();
	}

	private void jbInit() throws Exception {
		setLayout(new BorderLayout());
		periodLabel.setText(Constants.LANGUAGE.getString("Panel.Report.IncomeExpense.Period"));
		periodBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateFromAndTo();
			}
		});
		fromLabel.setText(Constants.LANGUAGE.getString("Panel.Report.IncomeExpense.From"));
		fromDateField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateFrom();
			}
		});
		fromDateField.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				updateFrom();
			}
		});
		toLabel.setText(Constants.LANGUAGE.getString("Panel.Report.IncomeExpense.To"));
		toDateField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateTo();
			}
		});
		fromDateField.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				updateTo();
			}
		});
		generateButton.setText(Constants.LANGUAGE.getString("Panel.Report.Generate"));
		generateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				generateReport();
			}
		});

		categoryLabel.setText(Constants.LANGUAGE.getString("Panel.Report.IncomeExpense.Category"));

		controlPanel.setBorder(BorderFactory.createEtchedBorder());
		GridBagLayout gbl_controlPanel = new GridBagLayout();
		gbl_controlPanel.columnWidths = new int[] { 0, 78, 0, 0, 0, 0, 0, 0, 0 };
		gbl_controlPanel.columnWeights = new double[] { 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
		controlPanel.setLayout(gbl_controlPanel);

		searchBox.setToolTipText("This will search only against the description. Enter keyword and press enter.");
		searchBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				generateReport();
			}
		});

		GridBagConstraints gbc_searchBox = new GridBagConstraints();
		gbc_searchBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_searchBox.anchor = GridBagConstraints.WEST;
		gbc_searchBox.insets = new Insets(6, 6, 5, 5);
		gbc_searchBox.gridx = 0;
		gbc_searchBox.gridy = 0;
		gbc_searchBox.gridwidth = 4;
		controlPanel.add(searchBox, gbc_searchBox);

		GridBagConstraints gbc_accountLabel = new GridBagConstraints();
		gbc_accountLabel.anchor = GridBagConstraints.EAST;
		gbc_accountLabel.insets = new Insets(6, 6, 5, 5);
		gbc_accountLabel.gridx = 0;
		gbc_accountLabel.gridy = 1;
		accountLabel.setText(Constants.LANGUAGE.getString("Panel.Report.Account"));
		controlPanel.add(accountLabel, gbc_accountLabel);

		GridBagConstraints gbc_accountBox = new GridBagConstraints();
		gbc_accountBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_accountBox.anchor = GridBagConstraints.WEST;
		gbc_accountBox.insets = new Insets(6, 6, 5, 5);
		gbc_accountBox.gridx = 1;
		gbc_accountBox.gridy = 1;
		controlPanel.add(accountBox, gbc_accountBox);
		// (Ax,Ay,1,1,0.0,0.0,..,(6,6,0,5),0,0) - Ax - x axis in the
		// controlPanel; Ay - y axis in the control panel
		controlPanel.add(categoryLabel, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(6, 6, 5, 5), 0, 0));
		categoryBox.addElementSelectionListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				int selectedIndex = categoryBox.getSelectedIndex();
				if (selectedIndex == 0 || selectedIndex == -1) {
					includeTrans.setVisible(true);
				} else {
					includeTrans.setVisible(false);
				}
			}
		});
		controlPanel.add(categoryBox, new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL, new Insets(6, 6, 5, 5), 0, 0));
		controlPanel.add(periodLabel, new GridBagConstraints(4, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(6, 6, 5, 5), 0, 0));
		controlPanel.add(periodBox, new GridBagConstraints(5, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(6, 6, 5, 5), 0, 0));

		GridBagConstraints gbc_includeTrans = new GridBagConstraints();
		gbc_includeTrans.insets = new Insets(0, 0, 5, 5);
		gbc_includeTrans.gridx = 6;
		gbc_includeTrans.gridy = 1;
		controlPanel.add(includeTrans, gbc_includeTrans);

		controlPanel.add(fromLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(6, 6, 0, 5), 0, 0));
		controlPanel.add(fromDateField, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(6, 6, 0, 5), 0, 0));
		controlPanel.add(toLabel, new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(6, 6, 0, 5), 0, 0));
		controlPanel.add(toDateField, new GridBagConstraints(3, 2, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(6, 6, 0, 5), 0, 0));
		controlPanel.add(currencyBox, new GridBagConstraints(4, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.NONE, new Insets(6, 12, 0, 4), 0, 0));
		controlPanel.add(generateButton, new GridBagConstraints(5, 2, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(6, 6, 0, 5), 1, 0));
		add(controlPanel, BorderLayout.SOUTH);
	}

	public class Item implements Comparable {

		private Category category;
		private String currencyCode;
		private long sum;
		private String description;
		private Date date;
		private String accountName;
		private String itemCode;
		private int quantity;
		private float cost;

		public Item(Category aCategory, String aCurrencyCode, long aSum, String description, Date date, String accountName, String itemCode, int quantity, float cost) {
			category = aCategory;
			currencyCode = aCurrencyCode;
			sum = aSum;
			this.description = description;
			this.date = date;
			this.accountName = accountName;
			this.itemCode = itemCode;
			this.quantity = quantity;
			this.cost = cost;
		}

		public String getDescription() {
			if (description == null || description.equals("")) {
				return "";
			}
			return description.length() > 45 ? description.substring(0, 43) + ".." : description;
		}

		public String getDate() {
			return dateFormat.format(date);
		}

		public String getCurrencyCode() {
			return currencyCode;
		}

		public String getBaseCategory() {
			if (category == null)
				return Constants.LANGUAGE.getString("Report.IncomeExpense.NoCategory");
			Object[] path = category.getCategoryNode().getUserObjectPath();
			return path.length > 1 ? path[1].toString() : category.getCategoryName();
		}

		public String getCategory() {
			return category == null ? Constants.LANGUAGE.getString("Report.IncomeExpense.NoCategory")
					: category.getFullCategoryName();
		}

		public Long getIncome() {
			return sum >= 0 ? new Long(sum) : null;
		}

		public String getAccountName(){
			return accountName;
		}

		public String getIncomeString() {
			return Currency.getCurrencyForCode(currencyCode).format(getIncome());
		}

		public Long getExpense() {
			return sum < 0 ? new Long(-sum) : null;
		}

		public String getExpenseString() {
			return Currency.getCurrencyForCode(currencyCode).format(getExpense());
		}

		public boolean noCategory() {
			return category == null;
		}

		public String getItemCode(){
			return itemCode;
		}

		public int getQuantity(){
			return quantity;
		}

		public float getCost(){
			return cost;
		}

		public int compareTo(Object o) {
			return Comparator.comparing((Item i) -> i.getCurrencyCode()).thenComparing((Item i) -> i.date).compare(this,
					(Item) o);
		}
	}

	private void disableDateField(boolean status, JDatePickerImpl field) {
		Arrays.stream(controlPanel.getComponents()).filter(x -> x.getClass() == field.getClass())
				.map(x -> (JDatePickerImpl)x).map(x -> x.getComponents()).flatMap(x -> Arrays.stream(x))
				.forEach(x -> x.setEnabled(status));
	}
}