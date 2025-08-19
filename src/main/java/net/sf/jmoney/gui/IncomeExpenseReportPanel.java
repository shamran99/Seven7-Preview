package net.sf.jmoney.gui;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JRSaveContributor;
import net.sf.jasperreports.view.JRViewer;
import net.sf.jmoney.Currency;
import net.sf.jmoney.*;
import net.sf.jmoney.model.*;
import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilDateModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTimeComparator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class IncomeExpenseReportPanel extends JPanel {

    public static final int THIS_MONTH = 0;

    public static final int THIS_YEAR = 1;

    public static final int LAST_MONTH = 2;

    public static final int LAST_YEAR = 3;

    public static final int ALL_RECORDS = 4;

    public static final int SELECT_MONTH = 5;

    public static final int CUSTOM = 6;

    private static final Logger logger = LogManager.getLogger(IncomeExpenseReportPanel.class);

    public static final String[] periods = {Constants.LANGUAGE.getString("Panel.Report.IncomeExpense.thisMonth"),
            Constants.LANGUAGE.getString("Panel.Report.IncomeExpense.thisYear"),
            Constants.LANGUAGE.getString("Panel.Report.IncomeExpense.lastMonth"),
            Constants.LANGUAGE.getString("Panel.Report.IncomeExpense.lastYear"),
            Constants.LANGUAGE.getString("Panel.Report.IncomeExpense.allRecords"),
            "Select Month",
            Constants.LANGUAGE.getString("Panel.Report.IncomeExpense.custom")};

    public static final String[] currencies = {"ALL", "LKR", "GBP"};

    private JPanel reportPanel;
    private final JPanel controlPanel = new JPanel();
    private final JButton generateButton = new JButton();
    private final JLabel periodLabel = new JLabel();
    private final JComboBox periodBox = new JComboBox(periods);
    private final JComboBox currencyBox = new JComboBox(currencies);
    private final JButton categorySelector = new JButton("Category Filter");

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

    /**
     * Category Filter
     */
    CategoryFilter categoryFilter = new CategoryFilter(getSelectGroupCats());


    private final JLabel fromLabel = new JLabel();
    private final JLabel toLabel = new JLabel();

    private Session session;
    private VerySimpleDateFormat dateFormat;
    private Date fromDate;
    private Date toDate;

    public IncomeExpenseReportPanel() {
        try {
            fromDateField = new JDatePickerImpl(fromDatePanel, new DateLabelFormatter());
            toDateField = new JDatePickerImpl(toDatePanel, new DateLabelFormatter());
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setSession(Session aSession) {
        session = aSession;
        categoryFilter.setSession(session);
    }

    public void setDateFormat(String pattern) {
        dateFormat = new VerySimpleDateFormat(pattern);
        updateFromAndTo();
    }

    private void generateReport() {
        if (reportPanel != null) {
            remove(reportPanel);
            updateUI();
        }
        try {
            String reportFile = false ? "resources/IncomeExpenseSubtotals.jasper" : "resources/IncomeExpense.jasper";
            URL url = Constants.class.getResource(reportFile);
            InputStream is = url.openStream();

            Map params = new HashMap();
            params.put("Dashline", Constants.LANGUAGE.getString("Report.IncomeExpense.Signature.Dashline"));
            params.put("President", Constants.LANGUAGE.getString("Report.IncomeExpense.Signature.President"));
            params.put("Auditor", Constants.LANGUAGE.getString("Report.IncomeExpense.Signature.Auditor"));
            params.put("Treasurer", Constants.LANGUAGE.getString("Report.IncomeExpense.Signature.Treasurer"));
            params.put("Title", Constants.LANGUAGE.getString("Report.IncomeExpense.Title"));
            params.put("Subtitle", dateFormat.format(fromDate) + " - " + dateFormat.format(toDate));
            params.put("Total", Constants.LANGUAGE.getString("Report.Total"));
            params.put("Category", Constants.LANGUAGE.getString("Entry.category"));
            params.put("Income", Constants.LANGUAGE.getString("Report.IncomeExpense.Income"));
            params.put("Expense", Constants.LANGUAGE.getString("Report.IncomeExpense.Expense"));
            params.put("DateToday", dateFormat.format(new Date()));
            params.put("Page", Constants.LANGUAGE.getString("Report.Page"));

            Collection items = getItems();
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
            ex.printStackTrace();
        }
        updateUI();
    }

    /**
     * This is to set the default save extension to csv
     *
     * @param jrViewer
     * @return
     */
    private JRViewer makeCsvDefault(JRViewer jrViewer) {
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
        HashMap byCurrency = new HashMap();
        Map<String, Boolean> categoryStatus = session.getCategoryStatus();
        List<String> allowedCategoryList = categoryStatus.keySet().stream().filter(categoryStatus::get).collect(Collectors.toList());

        Iterator aIt = session.getAccounts().listIterator();
        while (aIt.hasNext()) {
            Account a = (Account) aIt.next();
            String cc = a.getCurrencyCode();

            // This is to filter based on currency selection
            if (validateCurrencies(cc)) {
                continue;
            }

            HashMap items = (HashMap) byCurrency.get(cc);
            if (items == null) {
                items = new HashMap();
                byCurrency.put(cc, items);
            }
            addEntries(allItems, items, cc, a.getEntries(), allowedCategoryList);
        }

        Collections.sort(allItems);
        return allItems;
    }

    /**
     * Returns false if the account is with selected currency.
     *
     * @param cc - currency of the account
     * @return true if not selected currency
     */
    private boolean validateCurrencies(String cc) {
        return currencyBox.getSelectedIndex() != 0 && !currencyBox.getSelectedItem().toString().equals(cc);
    }

    private void addEntries(Vector allItems, HashMap items, String currencyCode, Vector entries, List<String> allowedCategoryList) {
        Iterator eIt = entries.listIterator();
        while (eIt.hasNext()) {
            Entry e = (Entry) eIt.next();
            if (!accept(e))
                continue;

            if (e instanceof SplittedEntry) {
                addEntries(allItems, items, currencyCode, ((SplittedEntry) e).getEntries(), allowedCategoryList);
            } else {
                Category c = e.getCategory();

                // Filtering selected categories
                boolean categoryCheck = (c == null && !allowedCategoryList.contains("No Category")) || (c != null && !allowedCategoryList.contains(c.getCategoryName()));
                if (categoryCheck) {
                    logger.info("The entry with description {} is skipped as not selected.", e.getDescription());
                    continue;
                }

                Item i = (Item) items.get(e.getCategory());
                if (i == null) {
                    i = new Item(e.getCategory(), currencyCode, e.getAmount());
                    items.put(e.getCategory(), i);
                    allItems.add(i);

                } else {
                    i.addToSum(e.getAmount());
                }
            }
        }
    }

    private boolean accept(Entry e) {
        if (e instanceof DoubleEntry)
            return false;
        return acceptFrom(e.getDate()) && acceptTo(e.getDate());
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
        if (periodBox.getSelectedIndex() == SELECT_MONTH) {
            fromDateModel.setDay(1);

            Calendar cal = Calendar.getInstance();
            cal.setTime(fromDateModel.getValue());
            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DATE));

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

        categorySelector.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                categoryFilter.setUpPopupbox();
            }
        });

        controlPanel.setBorder(BorderFactory.createEtchedBorder());
        controlPanel.setLayout(new GridBagLayout());
        controlPanel.add(periodLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, new Insets(6, 6, 0, 0), 0, 0));
        controlPanel.add(periodBox, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, new Insets(6, 6, 0, 5), 0, 0));
        controlPanel.add(fromLabel, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, new Insets(6, 6, 0, 0), 0, 0));
        controlPanel.add(fromDateField, new GridBagConstraints(3, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, new Insets(6, 6, 0, 5), 0, 0));
        controlPanel.add(toLabel, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, new Insets(6, 6, 0, 0), 0, 0));
        controlPanel.add(toDateField, new GridBagConstraints(5, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, new Insets(6, 6, 0, 5), 0, 0));
        controlPanel.add(currencyBox, new GridBagConstraints(6, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                GridBagConstraints.NONE, new Insets(6, 12, 0, 4), 0, 0));


        controlPanel.add(categorySelector, new GridBagConstraints(7, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                GridBagConstraints.NONE, new Insets(6, 12, 0, 4), 0, 0));


        controlPanel.add(generateButton, new GridBagConstraints(8, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                GridBagConstraints.NONE, new Insets(6, 12, 0, 4), 0, 0));

        add(controlPanel, BorderLayout.SOUTH);
    }

    public class Item implements Comparable {

        private final Category category;
        private final String currencyCode;
        private long sum;

        public Item(Category aCategory, String aCurrencyCode, long aSum) {
            category = aCategory;
            currencyCode = aCurrencyCode;
            sum = aSum;
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

        public String getIncomeString() {
            return Currency.getCurrencyForCode(currencyCode).format(getIncome());
        }

        public Long getExpense() {
            return sum < 0 ? new Long(-sum) : null;
        }

        public String getExpenseString() {
            return Currency.getCurrencyForCode(currencyCode).format(getExpense());
        }

        public void addToSum(long amount) {
            sum += amount;
        }

        public boolean noCategory() {
            return category == null;
        }

        public int compareTo(Object o) {
            Function<Item,Long> getAmount  =  (Item i)  -> i.getExpense() == null ? i.getIncome() : i.getExpense();
            return Comparator.comparing(Item::getCurrencyCode).thenComparing(getAmount,Comparator.reverseOrder()).compare(this, (Item) o);
        }

    }

    private void disableDateField(boolean status, JDatePickerImpl field) {
        Arrays.stream(controlPanel.getComponents()).filter(x -> x.getClass() == field.getClass())
                .map(x -> (JDatePickerImpl) x).map(x -> x.getComponents()).flatMap(x -> Arrays.stream(x))
                .forEach(x -> x.setEnabled(status));
    }

    private List<String> getSelectGroupCats(){
        String selectGroupCats = Constants.EXTERNAL_PROPERTY.getString("report.ie.categoryfilter.select-group.categories");
        selectGroupCats = selectGroupCats.toLowerCase();
        return !"".equals(selectGroupCats) ? new ArrayList(Arrays.asList(selectGroupCats.split(","))) : new ArrayList<>();
    }
}