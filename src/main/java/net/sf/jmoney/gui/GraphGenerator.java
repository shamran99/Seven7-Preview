package net.sf.jmoney.gui;

import net.sf.jmoney.*;
import net.sf.jmoney.model.Account;
import net.sf.jmoney.model.Category;
import net.sf.jmoney.model.Entry;
import net.sf.jmoney.model.Session;
import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilDateModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTimeComparator;

public class GraphGenerator extends JPanel {

    private static final Logger logger = LogManager.getLogger(GraphGenerator.class);
    public static final int THIS_MONTH = 0;

    public static final int THIS_YEAR = 1;

    public static final int LAST_MONTH = 2;

    public static final int LAST_YEAR = 3;

    public static final int ALL_RECORDS = 4;

    public static final int SELECT_MONTH = 5;

    public static final int CUSTOM = 6;

    AccountBalanceFilter accountBalanceFilter = new AccountBalanceFilter();

    public static final String[] periods = {Constants.LANGUAGE.getString("Panel.Report.IncomeExpense.thisMonth"),
            Constants.LANGUAGE.getString("Panel.Report.IncomeExpense.thisYear"),
            Constants.LANGUAGE.getString("Panel.Report.IncomeExpense.lastMonth"),
            Constants.LANGUAGE.getString("Panel.Report.IncomeExpense.lastYear"),
            Constants.LANGUAGE.getString("Panel.Report.IncomeExpense.allRecords"),
            "Select Month",
            Constants.LANGUAGE.getString("Panel.Report.IncomeExpense.custom")};

    private JPanel controlPanel = new JPanel();
    private JButton generateButton = new JButton();
    private JLabel periodLabel = new JLabel();
    private JComboBox periodBox = new JComboBox(periods);

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
    private Category selectedCategoryType;
    private int selectedCategoryIndex;

    private Session session;
    private VerySimpleDateFormat dateFormat;
    private Date fromDate;
    private Date toDate;
    private final JLabel accountLabel = new JLabel();
    private final JButton editAccounts = new JButton("Edit Accounts");
    private final JCheckBox cumulative = new JCheckBox("Cumulative");

    public GraphGenerator() {
        try {
            fromDateField = new JDatePickerImpl(fromDatePanel, new DateLabelFormatter());
            toDateField = new JDatePickerImpl(toDatePanel, new DateLabelFormatter());
            jbInit();
        } catch (Exception e) {
            logger.error("Caught exception on GraphGenerator constructor. Details: {}", e);
        }
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
        accountBalanceFilter.setSession(aSession);
    }

    public void setDateFormat(String pattern) {
        dateFormat = new VerySimpleDateFormat(pattern);
        updateFromAndTo();
    }


    private void generateChart() {
        // Read report related user inputs
        logger.info("Generate chart process started");
        selectedCategoryIndex = categoryBox.getSelectedIndex();
        selectedCategoryType = (Category) categoryBox.getSelectedItem();
        logger.info("Category box selected index is {} and category type is {}", selectedCategoryIndex, selectedCategoryType);
        try {
            getItems();
        } catch (NullPointerException e) {
            logger.error("Caught exception on generateChart. Details: {}", e);
        }
    }

    private void getItems() {
        Iterator aIt = session.getAccounts().listIterator();
        LinkedHashMap<String, Long> sumForMonth = null;

        while (aIt.hasNext()) {
            Account account = (Account) aIt.next();

            logger.info("Processing account in getItems... {}", account.getName());
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

            logger.info("Account {} is selected. Proceeding wth calculating the summation.", account.getName());
            LinkedHashMap<String, Long> temp = getSumForMonth(account);

            logger.info("---------temp before merging follows----------");
            temp.forEach((x, y) -> {
                logger.info("key:{};value:{}", x, y);
            });


            // Merging two maps
            if (sumForMonth == null) {
                logger.info("First entry for the sumForMonth. SO no merging..");
                sumForMonth = temp;
            } else {
                logger.info("sumForMonth has previous entries. So proceeding with merge.");
                LinkedHashMap<String, Long> finalSumForMonth = sumForMonth;
                temp.forEach((x, y) -> {
                    finalSumForMonth.compute(x, (a, b) -> {
                        return (b != null) ? b + y : y;
                    });
                });
                sumForMonth = finalSumForMonth;
            }
        }

        Comparator<Map.Entry<String, Long>> comparator = (x, y) -> {
            String key_1 = x.getKey();
            String key_2 = y.getKey();
            if("startBalance".equals(key_1)){
                return -1;
            } else if("startBalance".equals(key_2)){
                return 1;
            } else {
                String mydate = "01_%s";
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd_yyyy_MMM", Locale.ENGLISH);

                LocalDate dateX = LocalDate.parse(String.format(mydate, key_1), formatter);
                LocalDate dateY = LocalDate.parse(String.format(mydate, key_2), formatter);

                return dateX.compareTo(dateY);
            }
        };
        LinkedHashMap<String, Long> collect = sumForMonth.entrySet().stream().sorted(comparator).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (x, y) -> x, LinkedHashMap::new));

        logger.info("---------sumForMonth after merging follows----------");
        collect.forEach((x, y) -> {
            logger.info("key:{};value:{}", x, y);
        });

        // Convert long to double

        if (cumulative.isSelected()) {
            logger.info("cumulative selected. So proceeding with cumulative calculations");
            collect = getCumulativeSumForMonth(collect);

            logger.info("---------sumForMonth after cumulating follows----------");
            collect.forEach((x, y) -> {
                logger.info("key:{};value:{}", x, y);
            });
        }

        /**
         * Evaluating the Y Axis calculations only if a expense or default category is selected.
         * Hence, excluding no category selected option.
         */
        if(selectedCategoryType != null && !"[CATEGORIES]".equals(selectedCategoryType.getCategoryName())){
            /*logger.info("---------Adjusting Y Axis based on the category {} and type {}----------",selectedCategoryType.getCategoryName(),selectedCategoryType.getCategoryType());
            switch(selectedCategoryType.getCategoryType()){
                case "Expense" : collect.replaceAll((x,y) -> Math.abs(y));
                logger.info("All values converted to absolute values");
                break;
                case "Default"  : collect.replaceAll((x,y) -> y*-1);
                logger.info("All values converted to negative values");
            }*/

            logger.info("---------Adjusting Y Axis based on the category {}----------",selectedCategoryType.getCategoryName());
            collect.replaceAll((x,y) -> y*-1);
        }

        GraphApp graphApp = new GraphApp();
        graphApp.resetSeries();
        collect.forEach(graphApp::addValue);
        graphApp.appLaunch(GraphApp.class);
        logger.info("Graph app launched successfully..");
    }

    /**
     * The following method will process the entries in a given month
     * and returns the 2020_Mar fornat month as key and month ending balance as value.
     *
     * @param account
     * @return
     */
    private LinkedHashMap<String, Long> getSumForMonth(Account account) {
        ArrayList list = Collections.list(account.getEntries().elements());

        // Pre-defined functions
        Function<Entry, String> formatDateFumction = entry -> {
            LocalDate localDate = entry.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            String format = String.format("%s_%s", localDate.getYear(), localDate.getMonth().getDisplayName(TextStyle.SHORT, Locale.getDefault()));
            return format;
        };

        ToLongFunction<Entry> toLongFunction = x -> x.getAmount();
        Function<Entry, Date> entryFunction = x -> x.getDate();
        Predicate<Entry> pr = x -> accept(x);

        //processing
        LinkedHashMap<String, Long> map = (LinkedHashMap<String, Long>) list.stream().
                sorted(Comparator.comparing(entryFunction)).
                filter(pr).
                collect(Collectors.groupingBy(formatDateFumction, LinkedHashMap::new, Collectors.summingLong(toLongFunction)));

        // following steps are not required if the start balance not required.
        if(cumulative.isSelected()){
            long startBalance = account.getStartBalance();
            logger.info("Cumulative selected. Proceeding with including the starting balance {}", startBalance);
            if(startBalance != 0){
                logger.info("Adding start balance to the list. Balance {}.",startBalance);
                map.put("startBalance",startBalance);
            }
        }
        return map;
    }

    /**
     * The method processes a monthly balance map and returns a cumulated map
     * with start balance included and converting long to double
     *
     * @param map
     * @return
     */
    private LinkedHashMap<String, Long> getCumulativeSumForMonth(LinkedHashMap<String, Long> map) {
        LongAdder longAdder = new LongAdder();

        LinkedHashMap<String, Long> map_ = map.entrySet().stream().map(x -> {
            longAdder.add(x.getValue());
            Map.Entry<String, Long> entry = new AbstractMap.SimpleEntry<String, Long>(x.getKey(), longAdder.sum());
            return entry;
        }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (x, y) -> x, LinkedHashMap::new));
        return map_;
    }

    private boolean acceptCategory(Entry e) {
        try {
            // Category not selected or empty category selected - then include all transaction. Hence final value will be savings.
            if(selectedCategoryIndex == 0 || selectedCategoryIndex == -1){
                return true;
            }
            // AllExp selected. Then all negative values excluding transactions are returned. Hence all expenses.
            else if("AllExp".equals(selectedCategoryType.toString())){
                return e.getAmount() < 0 && !(e.getCategory() instanceof Account);
            }
            // Only the entries with matching category are returned.
            else {
                return e.getCategory().equals(selectedCategoryType);
            }
        } catch (NullPointerException ex) {
            // Exception caught when e.getCategory() returns null. In that case
            // we skip that particular entry.
            logger.error("Caught exception on acceptCategory. Details: {}", ex);
            return false;
        }
    }

    private boolean accept(Entry e) {
        return acceptFrom(e.getDate()) && acceptTo(e.getDate()) && acceptCategory(e);
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
                    logger.error("Caught exception on updateFromAndTo. Details: {}", e);
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
                generateChart();
            }
        });

        categoryLabel.setText(Constants.LANGUAGE.getString("Panel.Report.IncomeExpense.Category"));

        controlPanel.setBorder(BorderFactory.createEtchedBorder());
        GridBagLayout gbl_controlPanel = new GridBagLayout();
        gbl_controlPanel.columnWidths = new int[]{0, 78, 0, 0, 0, 0, 0, 0, 0};
        gbl_controlPanel.columnWeights = new double[]{0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
        controlPanel.setLayout(gbl_controlPanel);

        GridBagConstraints gbc_accountLabel = new GridBagConstraints();
        gbc_accountLabel.anchor = GridBagConstraints.EAST;
        gbc_accountLabel.insets = new Insets(6, 6, 5, 5);
        gbc_accountLabel.gridx = 0;
        gbc_accountLabel.gridy = 0;
        accountLabel.setText(Constants.LANGUAGE.getString("Panel.Report.Account"));
        controlPanel.add(accountLabel, gbc_accountLabel);

        controlPanel.add(editAccounts, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, new Insets(6, 6, 5, 5), 0, 0));

        // (Ax,Ay,1,1,0.0,0.0,..,(6,6,0,5),0,0) - Ax - x axis in the
        // controlPanel; Ay - y axis in the control panel
        controlPanel.add(categoryLabel, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, new Insets(6, 6, 5, 5), 0, 0));
        categoryBox.addElementSelectionListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                int selectedIndex = categoryBox.getSelectedIndex();
                if (selectedIndex == 0 || selectedIndex == -1) {
                    cumulative.setVisible(true);
                } else {
                    cumulative.setVisible(false);
                }
            }
        });
        controlPanel.add(categoryBox, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                GridBagConstraints.HORIZONTAL, new Insets(6, 6, 5, 5), 0, 0));
        controlPanel.add(periodLabel, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, new Insets(6, 6, 5, 5), 0, 0));
        controlPanel.add(periodBox, new GridBagConstraints(5, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, new Insets(6, 6, 5, 5), 0, 0));

        GridBagConstraints gbc_isCumulative = new GridBagConstraints();
        gbc_isCumulative.insets = new Insets(0, 0, 5, 5);
        gbc_isCumulative.gridx = 6;
        gbc_isCumulative.gridy = 0;
        controlPanel.add(cumulative, gbc_isCumulative);
        controlPanel.add(fromLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, new Insets(6, 6, 0, 5), 0, 0));
        controlPanel.add(fromDateField, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, new Insets(6, 6, 0, 5), 0, 0));
        controlPanel.add(toLabel, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, new Insets(6, 6, 0, 5), 0, 0));
        controlPanel.add(toDateField, new GridBagConstraints(3, 1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, new Insets(6, 6, 0, 5), 0, 0));
        controlPanel.add(generateButton, new GridBagConstraints(4, 1, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, new Insets(6, 6, 0, 5), 1, 0));
        add(controlPanel, BorderLayout.SOUTH);

        editAccounts.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Edit accounts btn pressed
                accountBalanceFilter.setUpPopupbox();
            }
        });
    }

    private void disableDateField(boolean status, JDatePickerImpl field) {
        Arrays.stream(controlPanel.getComponents()).filter(x -> x.getClass() == field.getClass())
                .map(x -> (JDatePickerImpl) x).map(x -> x.getComponents()).flatMap(x -> Arrays.stream(x))
                .forEach(x -> x.setEnabled(status));
    }
}