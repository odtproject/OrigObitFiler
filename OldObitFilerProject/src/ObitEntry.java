import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;

class ObitEntry
  implements Serializable, Cloneable
{
	static final long serialVersionUID = 218400001L;
	String lastName;
	String firstName;
	String maidenName;
	String nickName;
	Vector otherNames;
	String age;
	String birthCity;
	String birthState;
	String city;
	String state;
	String paperAbbreviation;
	String paperLocation;
	String tagname;
	Date date;
	boolean dirty;
	int version;
  
	public String format(ObitEntryConfig paramObitEntryConfig)
	{
		String str = new String("");
		str = str + this.lastName;
		str = str + ", " + this.firstName;
		if (!this.nickName.equals("")) {
			str = str + " \"" + this.nickName + "\"";
		}
		if (this.maidenName.equals("?")) {
			str = str + " ( )";
		} else if (!this.maidenName.equals("")) {
			str = str + " (" + this.maidenName + ")";
		}
		
		Enumeration localEnumeration = this.otherNames.elements();
		while (localEnumeration.hasMoreElements()) {
			str = str + " [" + localEnumeration.nextElement() + "]";
		}
		str = str + "; ";
		if (!this.age.equals("")) {
			str = str + this.age.toString();
		}
		str = str + "; ";
		if (!this.birthCity.equals("")) {
			str = str + this.birthCity;
		}
		if (!this.birthState.equals(""))
		{
			if (!this.birthCity.equals("")) {
				str = str + " ";
			}
			str = str + this.birthState;
		}
		if ((!this.birthCity.equals("")) || (!this.birthState.equals(""))) {
			str = str + ">";
		}
		if (!this.city.equals("")) {
			str = str + this.city + " ";
		}
		if (!this.state.equals("")) {
			str = str + this.state;
		}
		str = str + "; " + this.paperAbbreviation;
		if (!this.state.equals(this.paperLocation)) {
			str = str + " (" + this.paperLocation + ")";
		}
		str = str + "; ";
		str = str + paramObitEntryConfig.getDateFORMAT(this.date) + "; " + this.tagname;
		return str;
	}
  
	public ObitEntry()
	{
		new ErrorLog(null, "calling empty constructor for ObitEntry");
	}
  
	public ObitEntry(Vector paramVector, Date paramDate, String paramString1, String paramString2, String paramString3)
	{
		this.dirty = true;
		this.version = 100;
		this.date = paramDate;
		this.paperAbbreviation = paramString1;
		this.paperLocation = paramString2;
		this.tagname = paramString3;
		this.lastName = ((String)paramVector.elementAt(0)).trim();
		this.firstName = ((String)paramVector.elementAt(1)).trim();
		this.maidenName = ((String)paramVector.elementAt(2)).trim();
		this.nickName = ((String)paramVector.elementAt(4)).trim();
		this.age = ((String)paramVector.elementAt(5)).trim();
		this.birthCity = ((String)paramVector.elementAt(6)).trim();
		this.birthState = ((String)paramVector.elementAt(7)).trim();
		this.city = ((String)paramVector.elementAt(8)).trim();
		this.state = ((String)paramVector.elementAt(9)).trim();
		this.otherNames = new Vector();
		String str1 = (String)paramVector.elementAt(3);
		if (!str1.equals(""))
		{
			String str2 = new String("");
			int i = 0;
			for (int j = 0; j < str1.length(); j++)
			{
				char c = str1.charAt(j);
				if (c == '"') {
					i++;
				} else if ((i % 2 == 0) && (c == ' ')) {
					str2 = str2 + ',';
				} else {
					str2 = str2 + c;
				}
			}
			
			Vector localVector = parseStringList(str2);
			Enumeration localEnumeration = localVector.elements();
			while (localEnumeration.hasMoreElements()) {
				this.otherNames.addElement((String)localEnumeration.nextElement());
			}
		}
	}
  
	private Vector parseStringList(String paramString)
	{
		Vector localVector = new Vector();
		StringTokenizer localStringTokenizer = new StringTokenizer(paramString, ",");
		while (localStringTokenizer.hasMoreTokens()) {
			localVector.addElement(localStringTokenizer.nextToken());
		}
		return localVector;
	}
  
	public boolean isDirty()
	{
		return this.dirty;
	}
  
	private void readObject(ObjectInputStream paramObjectInputStream)
			throws IOException
	{
		try
		{
			paramObjectInputStream.defaultReadObject();
			if (this.version > 100)
			{
				new ErrorLog(null, "Data file created with a newer version, exitting.");
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
  
	public String sortKey()
	{
		return this.lastName + this.firstName;
	}
  
	public void dump(String paramString)
	{
		System.err.println("---- " + paramString + " ----");
		System.err.println("lastName: " + this.lastName);
		System.err.println("firstName: " + this.firstName);
		System.err.println("maidenName: " + this.maidenName);
		System.err.println("nickName: " + this.nickName);
		System.err.println("otherNames: " + this.otherNames);
		System.err.println("age: " + this.age);
		System.err.println("birthCity: " + this.birthCity);
		System.err.println("birthState: " + this.birthState);
		System.err.println("city: " + this.city);
		System.err.println("state: " + this.state);
		System.err.println("paperAbbreviation: " + this.paperAbbreviation);
		System.err.println("paperLocation: " + this.paperLocation);
		System.err.println("tagname: " + this.tagname);
		System.err.println("date: " + this.date);
		System.err.println("dirty: " + this.dirty);
		System.err.println("version: " + this.version);
	}
  
	public Object getClone()
	{
		try
		{
			return clone();
		}
		catch (CloneNotSupportedException localCloneNotSupportedException)
		{
			new ErrorLog(localCloneNotSupportedException, "clone not supported in getClone");
		}
		return null;
	}
}
