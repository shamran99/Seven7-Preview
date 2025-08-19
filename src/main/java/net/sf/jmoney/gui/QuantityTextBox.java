package net.sf.jmoney.gui;

import net.sf.jmoney.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ResourceBundle;

/**
 * @author ssiddique
 */
public class QuantityTextBox extends JDialog {
    private final JTextField textField = new JTextField();
    private final JLabel lblText = new JLabel("Quantity");
    ResourceBundle language = Constants.LANGUAGE;
    JPanel rootPanel = new JPanel();
    JPanel commandPanel = new JPanel();
    JButton okButton = new JButton();
    JButton cancelButton = new JButton();
    int status = Constants.CANCEL;
    JFrame parent;

    /**
     * Dialog to choose a category.
     */
    public QuantityTextBox(JFrame parent) {
        super(parent, Constants.LANGUAGE.getString("QuantityTextBox.title"), true);
        textField.setColumns(10);
        this.parent = parent;
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        pack();
    }

    public int showDialog() {
        textField.setText("0");
        pack();
        setLocationRelativeTo(parent);
        show();
        return status;
    }

    public String getText() {
        return textField.getText();
    }

    private void jbInit() throws Exception {
        this.setModal(true);
        // add components to root panel
        GridBagLayout gbl_rootPanel = new GridBagLayout();
        gbl_rootPanel.columnWidths = new int[]{12, 0, 165, 74};
        gbl_rootPanel.columnWeights = new double[]{0.0, 0.0, 0.0, 1.0};
        rootPanel.setLayout(gbl_rootPanel);

        GridBagConstraints gbc_lblText = new GridBagConstraints();
        gbc_lblText.insets = new Insets(0, 0, 5, 5);
        gbc_lblText.anchor = GridBagConstraints.EAST;
        gbc_lblText.gridx = 1;
        gbc_lblText.gridy = 0;
        rootPanel.add(lblText, gbc_lblText);

        GridBagConstraints gbc_textField = new GridBagConstraints();
        gbc_textField.insets = new Insets(0, 0, 5, 5);
        gbc_textField.fill = GridBagConstraints.HORIZONTAL;
        gbc_textField.gridx = 2;
        gbc_textField.gridy = 0;
        rootPanel.add(textField, gbc_textField);

        // this
        // --------------------------------------------------------------------
        getContentPane().add(rootPanel, BorderLayout.CENTER);
        // command panel
        // -----------------------------------------------------------
        // ok button
        okButton.setText(language.getString("Dialog.ok"));
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ok();
            }
        });
        // cancel button
        cancelButton.setText(language.getString("Dialog.cancel"));
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cancel();
            }
        });
        // add components to command panel
        commandPanel.setLayout(new GridLayout(1, 2, 5, 0));
        commandPanel.add(okButton, null);
        commandPanel.add(cancelButton, null);
        rootPanel.add(commandPanel, new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                GridBagConstraints.NONE, new Insets(17, 0, 11, 11), 0, 0));
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                cancel();
            }
        });

        textField.setText("0");
    }

    void ok() {
        status = Constants.OK;
        dispose();
    }

    void cancel() {
        status = Constants.CANCEL;
        dispose();
    }

}
