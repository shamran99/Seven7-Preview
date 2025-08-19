package net.sf.jmoney;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.JFormattedTextField.AbstractFormatter;

/**
 * @author ssiddique
 * 
 */
public class DateLabelFormatter extends AbstractFormatter {

	private static final long serialVersionUID = 1L;
	private UserProperties userProperties = new UserProperties();
	private SimpleDateFormat dateFormatter;

	public DateLabelFormatter() {

		try {
			userProperties = new ReadPreferencesFile().getUserProperties();
			dateFormatter = new SimpleDateFormat(userProperties.getDateFormat());
		} catch (Exception e) {
			System.err.println("IO Exception occured: " + e.getMessage());
		}
	}

	@Override
	public Object stringToValue(String text) throws ParseException {
		return dateFormatter.parseObject(text);
	}

	@Override
	public String valueToString(Object value) throws ParseException {
		if (value != null) {
			Calendar cal = (Calendar) value;
			return dateFormatter.format(cal.getTime());
		}

		return "";
	}
}