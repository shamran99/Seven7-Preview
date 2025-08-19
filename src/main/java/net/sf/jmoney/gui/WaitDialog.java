package net.sf.jmoney.gui;

import java.awt.BorderLayout;
import java.awt.Frame;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import net.sf.jmoney.Constants;

public class WaitDialog extends JDialog implements Runnable {

	private JLabel messageLabel = new JLabel();

	private boolean stop = false;

	public WaitDialog(Frame owner) {
		super(owner, Constants.LANGUAGE.getString("Dialog.Wait.Title"));
		try {
			jbInit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run() {
		messageLabel.paintImmediately(messageLabel.getVisibleRect());
		while (!stop) {
			try {
				Thread.sleep(300);
			} catch (InterruptedException ie) {
			}
		}
		dispose();
	}

	public void show(String message) {
		messageLabel.setText(message);
		pack();
		setLocationRelativeTo(getParent());
		show();

		stop = false;
		Thread thread = new Thread(Thread.currentThread().getThreadGroup(), this, "WaitDialog");
		thread.start();
	}

	public void stop() {
		stop = true;
	}

	private void jbInit() throws Exception {
		messageLabel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
		messageLabel.setHorizontalAlignment(SwingConstants.CENTER);

		getContentPane().add(messageLabel, BorderLayout.CENTER);
	}
}