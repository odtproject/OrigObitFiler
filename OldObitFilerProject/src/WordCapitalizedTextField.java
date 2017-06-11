import java.awt.TextField;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class WordCapitalizedTextField
  extends TextField
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6460029389212698669L;

	public WordCapitalizedTextField(int paramInt)
	{
		super(paramInt);
		addKeyListener(new WordCapitalizedTextField.WordCapitalizedTextKeyAdapter());
	}
  
	class WordCapitalizedTextKeyAdapter
    	extends KeyAdapter
    {
		WordCapitalizedTextKeyAdapter() {}
    
		public void keyPressed(KeyEvent paramKeyEvent)
		{
			String str1 = WordCapitalizedTextField.this.getText();
			int i = WordCapitalizedTextField.this.getSelectionStart();
			int j = paramKeyEvent.getKeyCode();
			if ((j < 65) || (j > 90)) {
				return;
			}
			char c1;
			if (paramKeyEvent.isControlDown()) {
				if (paramKeyEvent.isShiftDown()) {
					c1 = (char)(j - 65 + 65);
				} else {
					c1 = (char)(j - 65 + 97);
				}
			}
			else {
				char c2 = paramKeyEvent.getKeyChar();
				if ((i == 0) || (str1.charAt(i - 1) == ' ') || (str1.charAt(i - 1) == '>')) {
					c1 = Character.toUpperCase(c2);
				} else {
					c1 = c2;
				}
			}
			
			int k = WordCapitalizedTextField.this.getSelectionEnd();
			String str2 = str1.substring(0, i);
			String str3 = str1.substring(k, str1.length());
			WordCapitalizedTextField.this.setText(str2 + c1 + str3);
			WordCapitalizedTextField.this.setCaretPosition(i + 1);
			paramKeyEvent.consume();
			
		}
    }
    	
}
