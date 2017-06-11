import java.awt.Choice;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.TextField;
import java.awt.Toolkit;
import java.util.Vector;

class GeneralConfigPanel
  extends ConfigPanel
{
  /**
	 * 
	 */
	private static final long serialVersionUID = -7555589609188740484L;
	static final int TAGNAME = 0;
	static final int FONTSIZE = 1;
	static final int LISTENTRIES = 2;
	static final int TEXT_FIELDS = 3;
	static final String[] fieldNames = { "Tagname", "Font Size", "Visible Entries in Lists" };
	TextField[] fields;
	Choice fontNameChoice;
	EditConfigDialog dialog;
  
	public boolean isValidForm(boolean paramBoolean)
	{
		for (int i = 0; i < fieldNames.length; i++)
		{
			String str = getText(i).trim();
			if (str.equals(""))
			{
				if (paramBoolean)
				{
					this.fields[i].selectAll();
					this.fields[i].requestFocus();
					Toolkit.getDefaultToolkit().beep();
				}
				return false;
			}
		}
		return true;
	}
  
	public GeneralConfigPanel(EditConfigDialog paramEditConfigDialog)
	{
		this.dialog = paramEditConfigDialog;
		this.fields = new TextField[3];
		GridBagLayout localGridBagLayout = new GridBagLayout();
		setLayout(localGridBagLayout);
		GridBagConstraints localGridBagConstraints = new GridBagConstraints();
		localGridBagConstraints.fill = 2;
		localGridBagConstraints.insets = new Insets(0, 5, 5, 5);
		int i = 0;
		Label localLabel;
		do
		{
			add(localLabel = new Label(fieldNames[i], 0));
			localGridBagConstraints.gridwidth = -1;
			localGridBagLayout.setConstraints(localLabel, localGridBagConstraints);
			add(this.fields[i] = new TextField("", 20));
			this.fields[i].addKeyListener(new DialogCommandListener(paramEditConfigDialog, i));
			localGridBagConstraints.gridwidth = 0;
			localGridBagLayout.setConstraints(this.fields[i], localGridBagConstraints);
			i++;
		} while (i <= 0);
		
		add(localLabel = new Label("Font Name", 0));
		localGridBagConstraints.gridwidth = -1;
		localGridBagLayout.setConstraints(localLabel, localGridBagConstraints);
		add(this.fontNameChoice = new Choice());
		for (i = 0; i < ObitEntryConfig.fontNames.length; i++) {
			this.fontNameChoice.add(ObitEntryConfig.fontNames[i]);
		}
		
		this.fontNameChoice.addItemListener(new DialogCommandListener(paramEditConfigDialog, 200));
		localGridBagConstraints.gridwidth = 0;
		localGridBagLayout.setConstraints(this.fontNameChoice, localGridBagConstraints);
		
		i = 1;
		do
		{
			add(localLabel = new Label(fieldNames[i], 0));
			localGridBagConstraints.gridwidth = -1;
			localGridBagLayout.setConstraints(localLabel, localGridBagConstraints);
			add(this.fields[i] = new DigitOnlyTextField(20));
			this.fields[i].addKeyListener(new DialogCommandListener(paramEditConfigDialog, i));
			localGridBagConstraints.gridwidth = 0;
			localGridBagLayout.setConstraints(this.fields[i], localGridBagConstraints);
			i++;
		} while (i < 3);
	}
  
	public void updateFonts(Font paramFont)
	{
		int i = 0;
		do
		{
			this.fields[i].setFont(paramFont);
			this.fields[i].invalidate();
			i++;
		} while (i < 3);
		this.fontNameChoice.setFont(paramFont);
		this.fontNameChoice.invalidate();
	}
  
	public void setField(int paramInt, String paramString)
	{
		if (!paramString.equals(getText(paramInt))) {
			this.fields[paramInt].setText(paramString);
		}
	}
  
	public void clearAll()
	{
		int i = 0;
		do
		{
			this.fields[i].setText("");
			i++;
		} while (i < 3);
	}
  
	public void selectAll()
	{
		int i = 0;
		do
		{
			this.fields[i].selectAll();
			i++;
		} while (i < 3);
	}
  
	public Dimension getPreferredSize()
	{
		Dimension localDimension = super.getPreferredSize();
		localDimension.height += 30;
		return localDimension;
	}
  
	public String cardType()
	{
		return "General";
	}
  
	public Vector getTextFields()
	{
		Vector localVector = new Vector(3);
		int i = 0;
		do
		{
			localVector.addElement(this.fields[i].getText().trim());
			i++;
		} while (i < 3);
		return localVector;
	}
  
	public String getText(int paramInt)
	{
		return this.fields[paramInt].getText();
	}
}
