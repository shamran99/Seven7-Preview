package net.sf.jmoney.io;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import net.sf.jmoney.Constants;
import net.sf.jmoney.gui.AccountChooser;
import net.sf.jmoney.gui.MainFrame;
import net.sf.jmoney.model.Account;
import net.sf.jmoney.model.CategoryNode;
import net.sf.jmoney.model.Entry;
import net.sf.jmoney.model.Session;
import net.sf.jmoney.model.SimpleCategory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Following class is to read contents from a CSV file and add it as the
 * entry.
 * 
 * @author ssiddique
 *
 */
public class CSV implements FileFormat {
	MainFrame mainFrame;
	AccountChooser accountChooser;
	static Calendar calendar = Calendar.getInstance(Locale.US);
	static NumberFormat number = NumberFormat.getInstance(Locale.US);
	private static List<Character> delimeters = new ArrayList<>(Arrays.asList(',', ';'));
	private static final Logger logger = LogManager.getLogger(CSV.class);

	private enum Headers {
		Date, Account, Category, Income, Expense, Description, ItemCode, Quantity, Cost
	};

	/**
	 * Creates a new CSV.
	 */
	public CSV(MainFrame parent, AccountChooser ac) {
		mainFrame = parent;
		accountChooser = ac;
	}

	/**
	 * Create a CSV file filter
	 * 
	 * @return A FileFilter for CSV files
	 */
	@Override
	public FileFilter fileFilter() {
		return new CsvFileFilter();
	}

	@Override
	public void importFile(Session session, File csvFile) {
		try {
			// TODO Auto-generated method stub
			String info = Constants.LANGUAGE.getString("CSV.chooseAccount") + " \"" + csvFile.getName() + "\".";
			int s = accountChooser.showDialog(session.getAccounts(), info, true);
			if (s == Constants.OK) {
				// an existing account has been selected
				importAccount(session, accountChooser.getSelectedAccount(), csvFile);
				logger.info("CSV import completed!");
			} else if (s == Constants.NEW) {
				System.out.println("NEW");
				logger.warn("The new account feature is not implemented yet! Exiting!");
				// Feature to be implemented
			}
		} catch (IllegalArgumentException e) {
			logger.error("Separator is invalid {}",e.getMessage());
			JOptionPane.showMessageDialog(mainFrame, "Separator is invalid {}" + e.getMessage());
		} catch (Exception e) {
			logger.error("Exception occurred: {}",e.getMessage());
			JOptionPane.showMessageDialog(mainFrame, "Exception occurred: " + e.getMessage());
		}
	}

	@Override
	public void importSelectedFile(Session session, File file) {
	    try {
		    String accountName = Constants.EXTERNAL_PROPERTY.getString("selected.account");
		    List<Account> selectedAccount = (List<Account>) session.getAccounts().stream().filter(x -> ((Account) x).getName().equals(accountName))
				    .collect(Collectors.toList());
		    if (selectedAccount.size() == 1){
			importAccount(session, selectedAccount.get(0), file);
			JOptionPane.showMessageDialog(mainFrame, String.format("%s is successfully imported into %s",file,accountName));
		    }
		    else {
			    throw new Exception("Invalid Account Name");
		    }
	    } catch (IllegalArgumentException e) {
		    logger.error("Separator is invalid {}",e.getMessage());
		    JOptionPane.showMessageDialog(mainFrame, "Separator is invalid {}" + e.getMessage());
	    } catch (Exception e) {
		    logger.error("Exception occurred: {}",e.getMessage());
		    JOptionPane.showMessageDialog(mainFrame, "Exception occurred: " + e.getMessage());
	    }
	}

	/**
	 * Following method returns true if the delimiter separation is success!!
	 * 
	 * @return
	 * @throws IOException
	 */
	private boolean isCorrectDelimeter(char del, File csvFile) throws IOException {
		Iterator<CSVRecord> iterator;
		try (Reader in = new FileReader(csvFile)) {
			iterator = CSVFormat.newFormat(del).withFirstRecordAsHeader().parse(in).iterator();
		}
		return iterator.next().size() > 1;
	}

	private void importAccount(Session session, Account account, File csvFile) throws IOException {
		delimeters.stream().filter(x -> {
			try {
				return isCorrectDelimeter(x, csvFile);
			} catch (IOException e) {
				logger.error("Caught exception during import account {}",e.getMessage());
				throwAsUnchecked(e);
			}
			return false;
		}).anyMatch(x -> {
			try {
				importAccountProcess(x, csvFile, session, account);
			} catch (IOException e) {
				logger.error("Caught exception during import account {}",e.getMessage());
				throwAsUnchecked(e);
			}
			return true;
		});
	}

	@SuppressWarnings("unchecked")
	private static <E extends Throwable> void throwAsUnchecked(Exception exception) throws E {
		throw (E) exception;
	}

	private void importAccountProcess(char del, File csvFile, Session session, Account account) throws IOException {
		try (Reader in = new FileReader(csvFile)) {
			Iterable<CSVRecord> records = CSVFormat.newFormat(del).withFirstRecordAsHeader().parse(in);
			records.forEach(record -> {
				String income = record.get(Headers.Income);
				String expense = record.get(Headers.Expense);
				String date = record.get(Headers.Date);
				String desc = record.get(Headers.Description);
				String category = record.get(Headers.Category);

				Entry entry = new Entry();

				if(record.isMapped(Headers.ItemCode.toString())){
					// ItemCode Column does exists
					entry.setItemCode(record.get(Headers.ItemCode));
				}

				if(record.isMapped(Headers.Quantity.toString())){
					// Quantity Column does exists
					entry.setQuantity(Integer.parseInt(record.get(Headers.Quantity)));
				}

				if(record.isMapped(Headers.Cost.toString())){
					// Cost Column does exists
					entry.setCost(Float.parseFloat(record.get(Headers.Cost)));
				}

				String value = !income.equals("") ? income : "-" + expense;
				extractAmount(entry, value, account.getCurrency().getScaleFactor());

				if (!date.equals(""))
					entry.setDate(parseDate(date));

				entry.setDescription(desc);

				if (!category.equals("")) {
					entry.setCategory(getCategory(category, session));
				}

				account.addEntry(entry);
			});
		}
	}

	/**
	 * Returns the category with the specified name. If it doesn't exist a new
	 * category will be created.
	 */
	private SimpleCategory getCategory(String categoryName, Session session) {
		SimpleCategory category = searchCategory(categoryName, session.getCategories().getRootNode());
		if (category == null) {
			category = new SimpleCategory(categoryName);
			session.getCategories().insertNodeInto(category.getCategoryNode(), session.getCategories().getRootNode(),
					0);
			logger.info("New Category created. Category name: {}",categoryName);
		}
		return category;
	}

	/**
	 * Searches a category and returns null if it doesn't exist.
	 */
	private SimpleCategory searchCategory(String name, CategoryNode root) {
		for (Enumeration e = root.children(); e.hasMoreElements();) {
			CategoryNode node = (CategoryNode) e.nextElement();
			Object obj = node.getUserObject();
			if (obj instanceof SimpleCategory) {
				SimpleCategory category = (SimpleCategory) obj;
				if (category.getCategoryName().equals(name))
					return category;
			}
		}
		return null;
	}

	private void extractAmount(Entry entry, String line, short factor) {
		Number n = number.parse(line, new ParsePosition(0));
		entry.setAmount(n == null ? 0 : Math.round(n.doubleValue() * factor));
	}

	@Override
	public void exportAccount(Session session, Account account, File file) {
		// TODO Auto-generated method stub

	}

	/**
	 * A Filter that accepts CSV Files.
	 */
	public static class CsvFileFilter extends FileFilter {
		public boolean accept(File f) {
			if (f == null)
				return false;
			if (f.isDirectory())
				return true;
			return f.getName().toLowerCase().endsWith(".csv");
		}

		public String getDescription() {
			return "Comma Seperated Values (*.csv)";
		}
	}

	/**
	 * Parses the date string and returns a date object: 11/2/98 ->> 11/2/1998
	 * 3/15'00 ->> 3/15/2000
	 */
	private Date parseDate(String line) {
		try {
			StringTokenizer st = new StringTokenizer(line, "D/-'");
			int day = Integer.parseInt(st.nextToken().trim());
			int month = Integer.parseInt(st.nextToken().trim());
			int year = Integer.parseInt(st.nextToken().trim());
			if (year < 100) {
				if (line.indexOf("'") < 0)
					year = year + 1900;
				else
					year = year + 2000;
			}
			calendar.clear();
			calendar.set(year, month - 1, day);
			return calendar.getTime();
		} catch (Exception e) {
			logger.error("Caught Exception during date conversion. Details {}",e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

}
