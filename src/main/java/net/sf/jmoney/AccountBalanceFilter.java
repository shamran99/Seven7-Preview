package net.sf.jmoney;

import net.sf.jmoney.model.Account;
import net.sf.jmoney.model.FilterPanels;
import net.sf.jmoney.model.Session;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author ssiddique This class is to filter the accounts that need to be
 * considered in Account Balance calculations.
 */
public class AccountBalanceFilter implements FilterPanels {
    private Session session;
    private final String POPUP_ACCOUNT_MESSAGE_LABEL = "Please slect the accounts that need to be included";
    private final String POPUP_ACCOUNT_TITLE = "Choose Accounts";
    private final JPanel popupBoxContents = new JPanel(new GridBagLayout());

    private final Object[] options = {"Update", "Cancel"};
    public static Map<String, Boolean> checkboxesStatus;

    public AccountBalanceFilter() {
        resetPopupBox();
    }

    public void setSession(Session aSession) {
        this.session = aSession;
        checkboxesStatus = session.getCheckboxesStatus();
    }

    /**
     * Below method updates the checkboxesStatus map with most recent isSelected
     * values. Specially designed to identify the newly created accounts
     */
    public void updateCheckboxesStatus() {

        // 1. check and confirm we need to update or not?
        Map<String, Boolean> temp = new LinkedHashMap<>();
        session.getAccounts().stream().sorted(Comparator.comparing(Account::getCurrencyCode))
                .map(x -> ((Account) x).getName())
                .forEach(x -> {
                    Boolean value = checkboxesStatus.get(x);
                    temp.put((String) x, value == null || value);
                });
        checkboxesStatus = temp;
        session.setCheckboxesStatus(checkboxesStatus);
    }

    public void setUpPopupbox() {
        updateCheckboxesStatus();
        if (popupBoxContents.getComponents().length > 1) {
            resetPopupBox();
        }

        AtomicInteger i = new AtomicInteger(1);

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;

        checkboxesStatus.forEach((accName, isSelected) -> {
            JCheckBox checkBox = new JCheckBox(accName);
            Account account = session.getAccountByName(accName);
            JLabel currencyCode = new JLabel(account.getCurrencyCode());
            checkBox.setSelected(isSelected);

            c.gridx = 0;
            c.gridy = i.get();
            c.weightx = 0.75;
            popupBoxContents.add(checkBox, c);

            c.gridx = 1;
            c.gridy = i.getAndIncrement();
            c.weightx = 0.25;

            popupBoxContents.add(currencyCode, c);
        });
        updateBtnPressed();
    }

    /**
     * The function to be executed when the update btn is pressed
     */
    public void updateBtnPressed() {
        int n = JOptionPane.showOptionDialog(null, popupBoxContents, POPUP_ACCOUNT_TITLE,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
        if (n == 0) {
            Arrays.stream(popupBoxContents.getComponents()).filter(JCheckBox.class::isInstance).forEach(x -> {
                JCheckBox temp = (JCheckBox) x;
                if (temp.isSelected() != checkboxesStatus.get(temp.getText())) {
                    checkboxesStatus.replace(temp.getText(), temp.isSelected());
                }
            });
            // Save the checkboxesStatus to drive.
            session.setCheckboxesStatus(checkboxesStatus);
        }
    }

    /**
     * reset popupbox contents for avoid duplicating
     */
    public void resetPopupBox() {
        popupBoxContents.removeAll();
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        popupBoxContents.add(new JLabel(POPUP_ACCOUNT_MESSAGE_LABEL), c);
    }

}
