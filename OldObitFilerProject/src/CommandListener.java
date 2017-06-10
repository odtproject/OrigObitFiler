import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

class CommandListener
  implements ActionListener, ItemListener, WindowListener
{
  static final int BUTTON_ADD = 0;
  static final int BUTTON_EDIT = 1;
  static final int BUTTON_DELETE = 2;
  static final int BUTTON_CLEAR = 3;
  static final int BUTTON_PREV = 4;
  static final int BUTTON_NEXT = 5;
  static final int BUTTON_DONE = 6;
  static final int MENU_FILE_EXIT = 90;
  static final int MENU_ACTION_EXPORT = 100;
  static final int MENU_ACTION_SET_DATE = 101;
  static final int MENU_ACTION_CONFIGURE = 102;
  static final int MENU_HELP_CONTENTS = 110;
  static final int MENU_HELP_ABBREV = 111;
  static final int MENU_HELP_ABOUT = 112;
  static final int CHOICE_PAPER = 200;
  static final int PREV_LIST = 300;
  static final int TODAY_LIST = 301;
  int id;
  ObituaryFiler app;
  
  public CommandListener(int paramInt, ObituaryFiler paramObituaryFiler)
  {
    this.id = paramInt;
    this.app = paramObituaryFiler;
  }
  
  public void itemStateChanged(ItemEvent paramItemEvent)
  {
	  String str;
	  
    switch (this.id)
    {
    case 200: 
      if (paramItemEvent.getStateChange() == 1)
      {
        this.app.choicePaperChange((String)paramItemEvent.getItem());
        return;
      }
      str = "Paper selection not handled " + paramItemEvent.paramString();
      new ErrorLog(null, str);
      return;
    case 300: 
      if (paramItemEvent.getStateChange() == 1)
      {
        this.app.selectedPreviousItem((Integer)paramItemEvent.getItem());
        return;
      }
      str = "Prev List event not handled " + paramItemEvent.paramString();
      new ErrorLog(null, str);
      return;
    case 301: 
      if (paramItemEvent.getStateChange() == 1)
      {
        this.app.selectedTodayItem((Integer)paramItemEvent.getItem());
        return;
      }
      str = "Today List event not handled " + paramItemEvent.paramString();
      new ErrorLog(null, str);
      return;
    }
    str = "itemStateChanged hit default " + paramItemEvent.paramString();
    new ErrorLog(null, str);
  }
  
  public void windowDeactivated(WindowEvent paramWindowEvent) {}
  
  public void windowClosing(WindowEvent paramWindowEvent)
  {
    this.app.menuFileExit();
  }
  
  public void actionPerformed(ActionEvent paramActionEvent)
  {
    switch (this.id)
    {
    case 0: 
      this.app.buttonAdd();
      return;
    case 1: 
      this.app.buttonEdit();
      return;
    case 2: 
      this.app.buttonDelete();
      return;
    case 3: 
      this.app.buttonClear();
      return;
    case 4: 
      this.app.buttonPrev();
      return;
    case 5: 
      this.app.buttonNext();
      return;
    case 6: 
      this.app.menuFileExit();
      return;
    case 90: 
      this.app.menuFileExit();
      return;
    case 100: 
      this.app.menuActionExport();
      return;
    case 101: 
      this.app.menuActionSetDate();
      return;
    case 102: 
      this.app.MenuActionConfigure();
      return;
    case 110: 
      this.app.menuHelpContents();
      return;
    case 111: 
      this.app.menuHelpAbbreviations();
      return;
    case 112: 
      this.app.menuHelpAbout();
      return;
    case 300: 
      this.app.doubleClickItem(paramActionEvent.getActionCommand());
      return;
    default: 
      new ErrorLog(null, "actionPerformed hit default");
    }
  }
  
  public void windowOpened(WindowEvent paramWindowEvent) {}
  
  public void windowClosed(WindowEvent paramWindowEvent) {}
  
  public void windowDeiconified(WindowEvent paramWindowEvent) {}
  
  public void windowActivated(WindowEvent paramWindowEvent) {}
  
  public void windowIconified(WindowEvent paramWindowEvent) {}
}
