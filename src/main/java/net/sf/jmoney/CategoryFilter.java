package net.sf.jmoney;

import net.sf.jmoney.model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/***
 * @author shamran
 *
 */
public class CategoryFilter implements FilterPanels {
    private Session session;
    private final String POPUP_ACCOUNT_MESSAGE_LABEL = "Please select the category that need to be included";
    private final String POPUP_ACCOUNT_TITLE = "Choose Category";

    private final Object[] options = {"Update", "Cancel"};
    public static Map<String, Boolean> categoryStatus;
    private final JPanel popupBoxContents = new JPanel(new GridBagLayout());
    private JScrollPane jScrollPane;
    private List<String> selectGroupCats;


    public CategoryFilter(List selectGroupCats) {
        this.selectGroupCats = selectGroupCats;
        resetPopupBox();
    }

    public void setSession(Session session) {
        this.session = session;
        categoryStatus = session.getCategoryStatus();
    }

    /**
     * Below method updates the checkboxesStatus map with most recent isSelected
     * values. Specially designed to identify the newly created categories
     */
    @Override
    public void updateCheckboxesStatus() {
        Map<String, Boolean> temp = new LinkedHashMap<>();

        CategoryTreeModel categories = session.getCategories();
        for (int j = 0; j < categories.getChildCount(categories.getRootNode()); j++) {
            CategoryNode child = (CategoryNode) categories.getChild(categories.getRootNode(), j);
            Category category = child.getCategory();
            if (category instanceof SplitCategory || category instanceof TransferCategory) {
                continue;
            }

            String categoryName = category.getCategoryName();
            Boolean value = categoryStatus.get(categoryName);
            temp.put(categoryName, value == null || value);
        }

        String noCategory = "No Category";
        temp.put(noCategory, categoryStatus.get(noCategory) != null ? categoryStatus.get(noCategory) : true);

        categoryStatus = temp;
        session.setCategoryStatus(categoryStatus);
    }

    public void setUpPopupbox() {
        updateCheckboxesStatus();
        if (popupBoxContents.getComponents().length > 1) {
            resetPopupBox();
        }

        // Adding the components into the jPanel
        AtomicInteger i = new AtomicInteger(3);

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;

        categoryStatus.forEach((category, isSelected) -> {
            JCheckBox checkBox = new JCheckBox(category);
            checkBox.setSelected(isSelected);

            c.gridx = 0;
            c.gridy = i.getAndIncrement();
            c.weightx = 0.75;
            popupBoxContents.add(checkBox, c);
        });
        updateBtnPressed();
    }

    @Override
    public void updateBtnPressed() {
        int n = JOptionPane.showOptionDialog(null, jScrollPane, POPUP_ACCOUNT_TITLE,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
        if (n == 0) {
            JPanel component = (JPanel) jScrollPane.getComponent(0).getComponentAt(0, 0);

            Arrays.stream(component.getComponents()).filter(JCheckBox.class::isInstance).filter(x -> x.getName() == null).forEach(x -> {
                JCheckBox temp = (JCheckBox) x;
                if (temp.isSelected() != categoryStatus.get(temp.getText())) {
                    categoryStatus.replace(temp.getText(), temp.isSelected());
                }
            });
            // Save the checkboxesStatus to drive.
            session.setCategoryStatus(categoryStatus);
        }
    }

    /**
     * reset popupbox contents for avoid duplicating
     */
    @Override
    public void resetPopupBox() {
        // resetting jpanel
        popupBoxContents.removeAll();

        // adding label to jpanel
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        popupBoxContents.add(new JLabel(POPUP_ACCOUNT_MESSAGE_LABEL), c);

        // adding 'select all' checkbox
        JCheckBox select_all = new JCheckBox("Select All");
        select_all.setName("selectAll");
        select_all.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // retrieving the jpanel
                JPanel component = (JPanel) jScrollPane.getComponent(0).getComponentAt(0, 0);

                /*
                Get the list of items in the jPanel and iterate
                filter  only checkboxes without any specified name. So that we can exclude the 'select all' checkbox.
                set everything true/false based on the selection.
                 */
                Arrays.stream(component.getComponents()).filter(JCheckBox.class::isInstance).forEach(x -> ((JCheckBox) x).setSelected(select_all.isSelected()));
            }
        });
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 0.75;
        c.gridwidth = 1;
        popupBoxContents.add(select_all, c);

        // Adding 'select group' checkbox
        JCheckBox selectGroup = new JCheckBox("Select Group");
        selectGroup.setName("selectGroup");
        selectGroup.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // retrieving the jpanel
                JPanel component = (JPanel) jScrollPane.getComponent(0).getComponentAt(0, 0);

                /*
                Get the list of items in the jPanel and iterate
                filter only matching categories
                set everything true/false based on the selection.
                 */
                Arrays.stream(component.getComponents()).filter(JCheckBox.class::isInstance).filter(x -> selectGroupCats.contains(((JCheckBox) x).getText().toLowerCase())).
                        forEach(x -> ((JCheckBox) x).setSelected(selectGroup.isSelected()));
            }
        });
        c.gridx = 1;
        c.gridy = 1;
        c.weightx = 0.75;
        c.gridwidth = 1;
        popupBoxContents.add(selectGroup, c);

        // Add the jlabel into a new scrollpane
        jScrollPane = new JScrollPane(popupBoxContents, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        jScrollPane.setPreferredSize(new Dimension(370, 230));
    }
}
