import java.awt.List;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;

class ObituaryFiler
{
	static final int version = 100;
	static final int LAST = 0;
	static final int FIRST = 1;
	static final int MAIDEN = 2;
	static final int OTHER = 3;
	static final int NICK = 4;
	static final int AGE = 5;
	static final int BIRTHCITY = 6;
	static final int BIRTHSTATE = 7;
	static final int CITY = 8;
	static final int STATE = 9;
	static final int FIELDS = 10;
	static GUI gui;
	ObitEntryConfig config;
	Vector todayObits;
	Vector previousObits;
	int previousSelected;
	int todaySelected;
	StringSelection clipString;
	String abbreviations;
  
	public void menuHelpAbout()
	{
		String str = "Obituary Filer Version 2.01\n";
		str = str + "\nCopyright © 1996-1999 Michael Rice, All rights reserved.\n\n";
		str = str + "Dove and Cross icon created by Gene Ulrich.\n\n";
		gui.doMessageBox(str);
	}
	
	public static void main(String[] paramArrayOfString)
	{
		ObituaryFiler localObituaryFiler = new ObituaryFiler();
		gui = new GUI(localObituaryFiler);
		localObituaryFiler.initialize();
	}

	public void menuHelpAbbreviations()
	{
		gui.doMessageBox(this.abbreviations);
	}
  
	public void initialize()
	{
		if (this.config.isConfigured())
		{
			gui.initTextFields();
			gui.initListBoxes();
			readToday();
			gui.changeDateLabel();
			gui.fillTodayList(this.todayObits);
			generatePreviousList();
			gui.fillPreviousList(this.previousObits);
		}
	}
  
	void merge(ObitEntry paramObitEntry)
	{
		String str = paramObitEntry.sortKey();
		for (int i = 0; i < this.previousObits.size(); i++)
		{
			ObitEntry localObitEntry = (ObitEntry)this.previousObits.elementAt(i);
			if (str.compareTo(localObitEntry.sortKey()) < 0)
			{
				this.previousObits.insertElementAt(paramObitEntry, i);
				return;
			}
		}
		this.previousObits.addElement(paramObitEntry);
	}
  
	public void menuActionSetDate()
	{
		gui.doDateDialog();
		Date localDate = gui.getChangedDate();
		if (localDate != null)
		{
			writeToday();
			this.config.setCurrentDate(localDate);
			initialize();
		}
	}
  
	public void menuFileExit()
	{
		if (this.config.isConfigured())
		{
			writeToday();
			this.config.incrementDate();
			writeConfigFile();
		}
		System.exit(0);
	}
  
	public ObituaryFiler()
	{
		readConfigFile();
		this.todayObits = new Vector();
		this.previousObits = new Vector();
		this.previousSelected = -1;
		this.todaySelected = -1;
		this.abbreviations = "";
		try
		{
			BufferedReader localBufferedReader = new BufferedReader(new FileReader("abbrev.txt"));
			while (localBufferedReader.ready())
			{
				String str = localBufferedReader.readLine() + "\n";
				this.abbreviations += str;
			}
			localBufferedReader.close();
			return;
		}
		catch (FileNotFoundException localFileNotFoundException)
		{
			new ErrorLog(localFileNotFoundException, "Error opening abbrev.txt");
			return;
		}
		catch (IOException localIOException)
		{
			new ErrorLog(localIOException, "Error reading abbreviation file.");
		}
	}
  
	public void menuActionExport()
	{
		if (!hasDirtyItems())
		{
			Toolkit.getDefaultToolkit().beep();
			return;
		}
		Clipboard localClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		String str1 = processDirtyFiles();
		this.clipString = new StringSelection(str1);
		localClipboard.setContents(this.clipString, this.clipString);
		String str2 = writeSentFile(str1);
		String str3 = "All unsent entries have been stored in the system ";
		str3 = str3 + "clipboard.\nOpen your email program and paste them ";
		str3 = str3 + "in now.\n\n";
		if (str2 != null)
		{
			str3 = str3 + "A backup of these entries has been stored in the ";
			str3 = str3 + "file " + str2 + ".\n\n";
		}
		else
		{
			str3 = str3 + "Normally a backup of these entries is stored in ";
			str3 = str3 + "a file in the Sent directory. This operation ";
			str3 = str3 + "has failed.\n\n";
		}
		str3 = str3 + "After you are done pasting hit the Ok button.";
		gui.doMessageBox(str3);
		writeToday();
		gui.buttonPanel.statusLabel.setText("");
	}
  
	public void MenuActionConfigure()
	{
		gui.doConfigDialog();
		writeConfigFile();
		gui.initTextFields();
		gui.initListBoxes();
		initialize();
	}
  
	public Vector read(Date paramDate)
	{
		return readFile(this.config.getCurrentPaperAbbreviation() + File.separator + this.config.getDateFILE(paramDate));
	}
  
	public void writeFile(String paramString, Vector paramVector)
	{
		try
		{
			File localFile = new File(paramString);
			if (paramVector.size() == 0)
			{
				if (localFile.exists()) {
					localFile.delete();
				}
				return;
			}
			
			FileOutputStream localFileOutputStream = new FileOutputStream(localFile);
			ObjectOutputStream localObjectOutputStream = new ObjectOutputStream(localFileOutputStream);
			localObjectOutputStream.writeObject(paramVector);
			localObjectOutputStream.close();
			return;
		}
		catch (IOException localIOException)
		{
			new ErrorLog(localIOException, "IOException in writeFile");
		}
	}
  
	public Vector readFile(String paramString)
	{
		Vector localVector = new Vector();
		try
		{
			File localFile = new File(paramString);
			if (localFile.exists())
			{
				FileInputStream localFileInputStream = new FileInputStream(localFile);
				ObjectInputStream localObjectInputStream = new ObjectInputStream(localFileInputStream);
				localVector = (Vector)localObjectInputStream.readObject();
				localObjectInputStream.close();
			}
		}
		catch (FileNotFoundException localFileNotFoundException)
		{
			new ErrorLog(localFileNotFoundException, "FileNotFoundException: " + paramString);
		}
		catch (IOException localIOException)
		{
			new ErrorLog(localIOException, "IOException in readFile: " + paramString);
		}
		catch (ClassNotFoundException localClassNotFoundException)
		{
			new ErrorLog(localClassNotFoundException, "ClassNotFoundException in readFile");
		}
		return localVector;
	}
  
	public void buttonClear()
	{
		gui.initTextFields();
	}
  
	void generatePreviousList()
	{
		Date localDate = this.config.getCurrentDate();
		int i = this.config.getCurrentDaysPrevious();
		if (this.previousObits != null) {
			this.previousObits.removeAllElements();
		}
		
		for (int j = 0; j < i; j++)
		{
			localDate = this.config.getPreviousDate(localDate);
			Vector localVector = read(localDate);
			Enumeration localEnumeration = localVector.elements();
			while (localEnumeration.hasMoreElements())
			{
				ObitEntry localObitEntry = (ObitEntry)localEnumeration.nextElement();
				merge(localObitEntry);
			}
		}
	}
  
	public void choicePaperChange(String paramString)
	{
		writeToday();
		this.config.setPaper(paramString);
		initialize();
	}
  
	public void buttonEdit()
	{
		if (this.todaySelected != -1)
		{
			gui.removeTodayItem(this.todaySelected);
			ObitEntry localObitEntry = (ObitEntry)this.todayObits.elementAt(this.todaySelected);
			gui.fillTextFields(localObitEntry);
			this.todayObits.removeElementAt(this.todaySelected);
			this.todaySelected = -1;
			writeToday();
			return;
		}
		Toolkit.getDefaultToolkit().beep();
	}
  
	public void buttonDelete()
	{
		if (this.todaySelected != -1)
		{
			gui.removeTodayItem(this.todaySelected);
			this.todayObits.removeElementAt(this.todaySelected);
			this.todaySelected = -1;
			writeToday();
			return;
		}
		Toolkit.getDefaultToolkit().beep();
	}
  
  
	public void selectedPreviousItem(Integer paramInteger)
	{
		this.previousSelected = paramInteger.intValue();
	}
  
	public void doubleClickItem(String paramString)
	{
		ObitEntry localObitEntry1 = (ObitEntry)this.previousObits.elementAt(this.previousSelected);
		ObitEntry localObitEntry2 = (ObitEntry)localObitEntry1.getClone();
		localObitEntry2.date = this.config.getCurrentDate();
		localObitEntry2.dirty = true;
		this.todayObits.addElement(localObitEntry2);
		writeToday();
		gui.addToToday(localObitEntry2.format(this.config));
		gui.initTextFields();
		List localList = gui.todayPanel.obitList;
		localList.makeVisible(localList.getItemCount() - 1);
	}
    
	public void writeConfigFile()
	{
		try
		{
			FileOutputStream localFileOutputStream = new FileOutputStream("obit.dat");
			ObjectOutputStream localObjectOutputStream = new ObjectOutputStream(localFileOutputStream);
			localObjectOutputStream.writeObject(this.config);
			localObjectOutputStream.close();
			return;
		}
		catch (IOException localIOException)
		{
			new ErrorLog(localIOException, "Error writing initialization file obit.dat");
		}
	}
  
	public void readConfigFile()
	{
		try
		{
			FileInputStream localFileInputStream = new FileInputStream("obit.dat");
			ObjectInputStream localObjectInputStream = new ObjectInputStream(localFileInputStream);
			this.config = ((ObitEntryConfig)localObjectInputStream.readObject());
			localObjectInputStream.close();
			return;
		}
		catch (FileNotFoundException localFileNotFoundException)
		{
			this.config = new ObitEntryConfig();
			return;
		}
		catch (IOException localIOException)
		{
			new ErrorLog(localIOException, "Error reading initialization file obit.dat");
			return;
		}
		catch (ClassNotFoundException localClassNotFoundException)
		{
			new ErrorLog(localClassNotFoundException, "ClassNotFoundException in readConfigFile");
		}
	}
  
	private String writeSentFile(String paramString)
	{
		File localFile1 = new File("Sent");
		if (!localFile1.exists())
		{
			if (!localFile1.mkdir()) {
				new ErrorLog(null, "Could not make directory: Sent");
			}
		}
		else if (!localFile1.isDirectory()) {
			new ErrorLog(null, "File exists but is not a directory: Sent");
		}
		
		Date localDate = new Date();
		String str1 = "Sent" + File.separator + this.config.getDateFILE(localDate) + '.';
		NumberFormat localNumberFormat = NumberFormat.getInstance();
		localNumberFormat.setMinimumIntegerDigits(3);
		int i = 0;
		String str2 = null;
		int j = 0;
		do
		{
			String str3 = localNumberFormat.format(j);
			str2 = new String(str1 + str3);
			File localFile2 = new File(str2);
			if (!localFile2.exists()) {
				try
				{
					PrintWriter localPrintWriter = new PrintWriter(new FileOutputStream(localFile2));
					StringTokenizer localStringTokenizer = new StringTokenizer(paramString, "\n");
					while (localStringTokenizer.hasMoreTokens()) {
						localPrintWriter.println(localStringTokenizer.nextToken());
					}
					localPrintWriter.close();
					i = 1;
				}
				catch (IOException localIOException)
				{
					new ErrorLog(localIOException, "Error writing Sent obits");
				}
			}
			j++;
			
		} while (j <= 999);
		
		if (i == 0)
		{
			new ErrorLog(null, "Error: file not saved.");
			return null;
		}
		return str2;
	}
  
	public void writeToday()
	{
		try
		{
			String str = this.config.getCurrentPaperAbbreviation() + File.separator + this.config.getCurrentDateFILE();
			File localFile = new File(str);
			if (this.todayObits.size() == 0)
			{
				if (localFile.exists()) {
					localFile.delete();
				}
				return;
			}
			this.config.addDirtyFile(str);
			FileOutputStream localFileOutputStream = new FileOutputStream(localFile);
			ObjectOutputStream localObjectOutputStream = new ObjectOutputStream(localFileOutputStream);
			localObjectOutputStream.writeObject(this.todayObits);
			localObjectOutputStream.close();
			return;
		}
		catch (IOException localIOException)
		{
			new ErrorLog(localIOException, "IOException in writeToday");
		}
	}
  
	public void readToday()
	{
		this.todayObits = read(this.config.getCurrentDate());
	}
  
	public void menuHelpContents()
	{
		String str = "Online help will be available in a future version.\n\n";
		str = str + "The latest Documentation and FAQs are available at:\n\n";
		str = str + "http://www.concentric.net/~Mikerice/obitprog/";
		gui.doMessageBox(str);
	}
  
	public String processDirtyFiles()
	{
		String str1 = new String("");
		int i;
		for (i = 0; i < this.config.dirtyFiles.size(); i++)
		{
			String str2 = (String)this.config.dirtyFiles.elementAt(i);
			Vector localVector = readFile(str2);
			for (int j = 0; j < localVector.size(); j++)
			{
				ObitEntry localObitEntry = (ObitEntry)localVector.elementAt(j);
				if (localObitEntry.dirty)
				{
					str1 = str1 + "\n" + localObitEntry.format(this.config);
					localObitEntry.dirty = false;
				}
			}
			writeFile(str2, localVector);
		}
		for (i = 0; i < this.todayObits.size(); i++) {
			((ObitEntry)this.todayObits.elementAt(i)).dirty = false;
		}
		this.config.dirtyFiles.removeAllElements();
		return str1;
	}
  
	public void selectedTodayItem(Integer paramInteger)
	{
		this.todaySelected = paramInteger.intValue();
	}
  
	public boolean hasDirtyItems()
	{
		for (int i = 0; i < this.config.dirtyFiles.size(); i++)
		{
			String str = (String)this.config.dirtyFiles.elementAt(i);
			Vector localVector = readFile(str);
			for (int j = 0; j < localVector.size(); j++)
			{
				ObitEntry localObitEntry = (ObitEntry)localVector.elementAt(j);
				if (localObitEntry.dirty) {
					return true;
				}
			}
		}
		return false;
	}
  
	public void buttonAdd()
	{
		if (gui.isValidText())
		{
			this.config.addAutoCity(gui.getAutoCity());
			@SuppressWarnings("rawtypes")
			Vector localVector = gui.getTextFields();
			ObitEntry localObitEntry = new ObitEntry(localVector, this.config.getCurrentDate(), this.config.getCurrentPaperAbbreviation(), this.config.getCurrentPaperLocation(), this.config.getTagname());
			this.todayObits.addElement(localObitEntry);
			gui.addToToday(localObitEntry.format(this.config));
			writeToday();
			gui.initTextFields();
			List localList = gui.todayPanel.obitList;
			localList.makeVisible(localList.getItemCount() - 1);
			return;
		}
		Toolkit.getDefaultToolkit().beep();
	}
  
	public void buttonPrev()
	{
		writeToday();
		this.config.decrementDate();
		initialize();
	}
  
	public void buttonNext()
	{
		writeToday();
		this.config.incrementDate();
		initialize();
	}
}
