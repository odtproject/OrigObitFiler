import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Vector;

class ObitEntryConfig
  implements Serializable
{
	static final long serialVersionUID = 218400002L;
	static final String[] fontNames = { "Dialog", "Monospaced", "SansSerif", "Serif" };
	Vector papers = new Vector();
	Vector dirtyFiles = new Vector();
	int currentPaper = -1;
	String tagname;
	int listEntries = 5;
	int fontNameIndex = 0;
	int fontSize = 12;
	Font font = new Font(fontNames[this.fontNameIndex], 0, this.fontSize);
	int version = 100;
  
	public String getCurrentDateDISPLAY()
	{
		Date localDate = getCurrentPaper().currentDay;
		return DateFormat.getDateInstance(1).format(localDate);
	}
  
	public PaperConfig getCurrentPaper()
	{
		if (this.currentPaper == -1) {
			return null;
		}
		PaperConfig localPaperConfig = (PaperConfig)this.papers.elementAt(this.currentPaper);
		return localPaperConfig;
	}
  
	public int getListEntries()
	{
		return this.listEntries;
	}
  
	public void setListEntries(int paramInt)
	{
		this.listEntries = paramInt;
	}
  
	@SuppressWarnings("rawtypes")
	public Enumeration paperAbbreviationElements()
	{
		return new PaperEnumeration(this);
	}
  
	@SuppressWarnings("unchecked")
	public boolean addAutoCity(String paramString)
	{
		if ((paramString.compareTo("") != 0) && (!getCurrentPaper().autoCities.contains(paramString)))
		{
			getCurrentPaper().autoCities.addElement(paramString);
			return true;
		}
		return false;
	}
  
	public int getDaysPreviousAt(int paramInt)
	{
		PaperConfig localPaperConfig = getPaperAt(paramInt);
		return localPaperConfig.daysPrevious;
	}
  
	public Date getNextDate(Date paramDate)
	{
		GregorianCalendar localGregorianCalendar = new GregorianCalendar();
		localGregorianCalendar.setTime(paramDate);
		switch (getCurrentPaper().frequency)
		{
		case 0: 
			localGregorianCalendar.add(6, 1);
			break;
		case 1: 
			localGregorianCalendar.add(6, 7);
			break;
		case 2: 
			localGregorianCalendar.add(2, 1);
			break;
		default: 
			localGregorianCalendar.add(6, 1);
			new ErrorLog(null, "Paper frequency unknown");
			break;
		}
		return localGregorianCalendar.getTime();
	}
  
	public Date getPreviousDate(Date paramDate)
	{
		GregorianCalendar localGregorianCalendar = new GregorianCalendar();
		localGregorianCalendar.setTime(paramDate);
		switch (getCurrentPaper().frequency)
		{
		case 0: 
			localGregorianCalendar.add(6, -1);
			break;
		case 1: 
			localGregorianCalendar.add(6, -7);
			break;
		case 2: 
			localGregorianCalendar.add(2, -1);
			break;
		default: 
			localGregorianCalendar.add(6, -1);
			new ErrorLog(null, "Paper frequency unknown");
			break;
		}
		return localGregorianCalendar.getTime();
	}
  
	public void setCurrentDate(Date paramDate)
	{
		getCurrentPaper().currentDay = paramDate;
	}
  
	public Date getCurrentDate()
	{
		return getCurrentPaper().currentDay;
	}
  
	public String getPaperLocationAt(int paramInt)
	{
		PaperConfig localPaperConfig = getPaperAt(paramInt);
		return localPaperConfig.paperLocation;
	}
  
	public boolean isConfigured()
	{
		if (this.currentPaper == -1) {
			return false;
		}
		return this.tagname != null;
	}
  
	public int getFontNameIndex()
	{
		return this.fontNameIndex;
	}
  
	public void setFontNameIndex(int paramInt)
	{
		this.fontNameIndex = paramInt;
		setFont(new Font(fontNames[paramInt], 0, this.fontSize));
	}
  
	public void setPaper(String paramString)
	{
		for (int i = 0; i < this.papers.size(); i++)
		{
			PaperConfig localPaperConfig = (PaperConfig)this.papers.elementAt(i);
			if (localPaperConfig.paperAbbreviation == paramString)
			{
				this.currentPaper = i;
				return;
			}
		}
	}
  
	private void readObject(ObjectInputStream paramObjectInputStream)
			throws IOException
	{
		try
		{
			paramObjectInputStream.defaultReadObject();
			if (this.version > 100)
			{
				new ErrorLog(null, "Data file created with a newer version, aborting.");
				System.exit(1);
			}
			this.version = 100;
			return;
		}
		catch (ClassNotFoundException localClassNotFoundException)
		{
			new ErrorLog(localClassNotFoundException, "ClassNotFoundException in readObject");
		}
	}
  
	public void incrementDate()
	{
		setCurrentDate(getNextDate(getCurrentDate()));
	}
  
	public int getFrequencyAt(int paramInt)
	{
		PaperConfig localPaperConfig = getPaperAt(paramInt);
		return localPaperConfig.frequency;
	}
  
	public String getCurrentDateFILE()
	{
		return getDateFILE(getCurrentPaper().currentDay);
	}
  
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void addPaper(String paramString1, String paramString2, int paramInt1, int paramInt2, String[] paramArrayOfString)
	{
		int i = 0;
		Object localObject1 = paperAbbreviationElements();
		Object localObject2;
		while (((Enumeration)localObject1).hasMoreElements())
		{
			localObject2 = (String)((Enumeration)localObject1).nextElement();
			if (((String)localObject2).equals(paramString1))
			{
				PaperConfig localPaperConfig = (PaperConfig)this.papers.elementAt(i);
				localPaperConfig.updatePaper(paramString2, paramInt1, paramInt2, paramArrayOfString);
				return;
			}
			i++;
		}
		
		localObject1 = new File(paramString1);
		if (!((File)localObject1).exists())
		{
			if (!((File)localObject1).mkdir()) {
				new ErrorLog(null, "Could not make directory: " + paramString1);
			}
		}
		else if (!((File)localObject1).isDirectory()) {
			new ErrorLog(null, "File exists but is not a directory: " + paramString1);
		}
		localObject2 = new PaperConfig(paramString1, paramString2, paramInt1, paramInt2, paramArrayOfString);
		this.papers.addElement(localObject2);
		if (this.papers.size() == 1) {
			this.currentPaper = 0;
		}
	}
  
	public PaperConfig getPaperAt(int paramInt)
	{
		return (PaperConfig)this.papers.elementAt(paramInt);
	}
  
	public int removeAutoCity(String paramString)
	{
		int i = getCurrentPaper().autoCities.indexOf(paramString);
		getCurrentPaper().autoCities.removeElement(paramString);
		return i;
	}
  
	public int getCurrentDaysPrevious()
	{
		return getCurrentPaper().daysPrevious;
	}
  
	public String getPaperAbbreviationAt(int paramInt)
	{
		PaperConfig localPaperConfig = getPaperAt(paramInt);
		return localPaperConfig.paperAbbreviation;
	}
  
	public void addDirtyFile(String paramString)
	{
		this.dirtyFiles.addElement(paramString);
	}
  
	public int getCurrentFrequency()
	{
		return getCurrentPaper().frequency;
	}
  
	public String getDateFORMAT(Date paramDate)
	{
		SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyy-M-d");
		return localSimpleDateFormat.format(paramDate);
	}
  
	public String getTagname()
	{
		return this.tagname;
	}
  
	public int getFontSize()
	{
		return this.fontSize;
	}
  
	public void setTagname(String paramString)
	{
		this.tagname = paramString;
	}
  
	public void setFontSize(int paramInt)
	{
		this.fontSize = paramInt;
		setFont(new Font(fontNames[this.fontNameIndex], 0, this.fontSize));
	}
  
	public Font getFont()
	{
		return this.font;
	}
  
	public void setFont(Font paramFont)
	{
		this.font = paramFont;
	}
  
	public String getAutoCityAtAt(int paramInt1, int paramInt2)
	{
		PaperConfig localPaperConfig = getPaperAt(paramInt1);
		return (String)localPaperConfig.autoCities.elementAt(paramInt2);
	}
  
	public String getDateFILE(Date paramDate)
	{
		SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyyMMdd");
		return localSimpleDateFormat.format(paramDate);
	}
  
	public void removePaper(String paramString)
	{
		int i = 0;
		@SuppressWarnings("rawtypes")
		Enumeration localEnumeration = paperAbbreviationElements();
		while (localEnumeration.hasMoreElements())
		{
			String str = (String)localEnumeration.nextElement();
			if (str.equals(paramString))
			{
				this.papers.removeElementAt(i);
				this.currentPaper = (this.papers.size() - 1);
				return;
			}
			i++;
		}
	}
  
	public void dump(String paramString)
	{
		System.err.println("---" + paramString + " ---");
		Object localObject;
		int i;
		for (i = 0; i < this.papers.size(); i++)
		{
			localObject = (PaperConfig)this.papers.elementAt(i);
			((PaperConfig)localObject).dump();
		}
		for (i = 0; i < this.dirtyFiles.size(); i++)
		{
			localObject = (String)this.dirtyFiles.elementAt(i);
			System.err.println("dirty paper: " + (String)localObject);
		}
		System.err.println("tagname: " + this.tagname);
		System.err.println("listEntries: " + this.listEntries);
		System.err.println("fontNameIndex: " + this.fontNameIndex);
		System.err.println("    fontName: " + fontNames[this.fontNameIndex]);
		System.err.println("fontSize: " + this.fontSize);
		System.err.println("version: " + this.version);
	}
  
	public int getCurrentNumberOfAutoCities()
	{
		return getCurrentPaper().autoCities.size();
	}
  
	public String getCurrentPaperAbbreviation()
	{
		return getCurrentPaper().paperAbbreviation;
	}
  
	public void decrementDate()
	{
		setCurrentDate(getPreviousDate(getCurrentDate()));
	}
  
	public int getNumberOfAutoCitiesAt(int paramInt)
	{
		PaperConfig localPaperConfig = getPaperAt(paramInt);
		return localPaperConfig.autoCities.size();
	}
  
	public String getCurrentAutoCityAt(int paramInt)
	{
		return (String)getCurrentPaper().autoCities.elementAt(paramInt);
	}
  
	public String getCurrentPaperLocation()
	{
		return getCurrentPaper().paperLocation;
	}
}
