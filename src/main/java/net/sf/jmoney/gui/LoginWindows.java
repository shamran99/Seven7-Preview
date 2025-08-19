package net.sf.jmoney.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import net.sf.jmoney.Constants;
import net.sf.jmoney.LoginValidator;
import net.sf.jmoney.ReadPreferencesFile;
import net.sf.jmoney.UserProperties;

public class LoginWindows extends JFrame {

	private JPanel contentPane;
	private JTextField txtusername;
	private JPasswordField txtpassword;
	private UserProperties userProperties = new UserProperties();

	/**
	 * Create the frame.
	 */
	public LoginWindows() {
		ReadPreferencesFile preferencesFile = new ReadPreferencesFile();
		userProperties = preferencesFile.getUserProperties();
		setResizable(false);
		setTitle(Constants.LANGUAGE.getString("Login.title"));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 277);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		initLookAndFeel();

		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.CENTER);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] { 129, 225, 0, 0 };
		gbl_panel.rowHeights = new int[] { 70, 44, 40, 0, 9, 2, 0 };
		gbl_panel.columnWeights = new double[] { 0.0, 0.0, 1.0, Double.MIN_VALUE };
		gbl_panel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		panel.setLayout(gbl_panel);

		JLabel lblWelcomeToThe = new JLabel(Constants.LANGUAGE.getString("Welcome.message"));
		lblWelcomeToThe.setFont(new Font("Songti TC", Font.BOLD, 15));
		lblWelcomeToThe.setForeground(SystemColor.controlShadow);
		GridBagConstraints gbc_lblWelcomeToThe = new GridBagConstraints();
		gbc_lblWelcomeToThe.gridwidth = 3;
		gbc_lblWelcomeToThe.insets = new Insets(0, 0, 5, 5);
		gbc_lblWelcomeToThe.gridx = 0;
		gbc_lblWelcomeToThe.gridy = 0;
		panel.add(lblWelcomeToThe, gbc_lblWelcomeToThe);

		JLabel lblusername = new JLabel("User Name :");
		GridBagConstraints gbc_lblusername = new GridBagConstraints();
		gbc_lblusername.insets = new Insets(0, 0, 5, 5);
		gbc_lblusername.anchor = GridBagConstraints.EAST;
		gbc_lblusername.gridx = 0;
		gbc_lblusername.gridy = 1;
		panel.add(lblusername, gbc_lblusername);

		txtusername = new JTextField();
		GridBagConstraints gbc_txtusername = new GridBagConstraints();
		gbc_txtusername.insets = new Insets(0, 0, 5, 5);
		gbc_txtusername.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtusername.gridx = 1;
		gbc_txtusername.gridy = 1;
		panel.add(txtusername, gbc_txtusername);
		txtusername.setColumns(10);

		JLabel lblpassword = new JLabel("Password :");
		GridBagConstraints gbc_lblpassword = new GridBagConstraints();
		gbc_lblpassword.insets = new Insets(0, 0, 5, 5);
		gbc_lblpassword.anchor = GridBagConstraints.EAST;
		gbc_lblpassword.gridx = 0;
		gbc_lblpassword.gridy = 2;
		panel.add(lblpassword, gbc_lblpassword);

		txtpassword = new JPasswordField();
		txtpassword.addKeyListener(new KeyListener() {

			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub

			}

			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub

			}

			public void keyPressed(KeyEvent e) {
				keyCheck(e);

			}
		});
		GridBagConstraints gbc_txtpassword = new GridBagConstraints();
		gbc_txtpassword.insets = new Insets(0, 0, 5, 5);
		gbc_txtpassword.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtpassword.gridx = 1;
		gbc_txtpassword.gridy = 2;
		panel.add(txtpassword, gbc_txtpassword);

		JButton btnlogin = new JButton("Login");
		btnlogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loginButtonClicked();
			}
		});

		btnlogin.setHorizontalAlignment(SwingConstants.LEFT);
		GridBagConstraints gbc_btnlogin = new GridBagConstraints();
		gbc_btnlogin.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc_btnlogin.insets = new Insets(0, 0, 5, 5);
		gbc_btnlogin.gridx = 1;
		gbc_btnlogin.gridy = 3;
		panel.add(btnlogin, gbc_btnlogin);

		/**
		 * Set Location
		 */
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);
	}

	private void loginButtonClicked() {
		String username = txtusername.getText();
		char[] password = txtpassword.getPassword();
		if (username != null && password != null) {
			boolean result = new LoginValidator(username, password).validate();
			if (result) {
				openMainWindow();
			} else {
				JOptionPane.showMessageDialog(this, Constants.LANGUAGE.getString("login.authentication.failed.message"),
						Constants.LANGUAGE.getString("login.authentication.failed.title"), JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	/**
	 * The following method will be called to skip any login window.
	 */
	public void openMainWindow(){
		MainFrame world = new MainFrame();
		world.show();
		try {
			world.initProperties();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		disposeWindow();
	}

	private void disposeWindow() {
		this.dispose();
	}

	private void keyCheck(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			loginButtonClicked();
		}
	}

	private void initLookAndFeel() {
		try {
			UIManager.setLookAndFeel(userProperties.getLookAndFeel());
		} catch (Exception ex) {
			System.err.println("Invalid/missing Look&Feel: " + userProperties.getLookAndFeel());
		}
	}

}
