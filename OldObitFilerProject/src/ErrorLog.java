import java.awt.Toolkit;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

public class ErrorLog
{
  public ErrorLog(Exception paramException, String paramString)
  {
    Toolkit.getDefaultToolkit().beep();
    Date localDate = new Date();
    try
    {
      System.err.println("Error occurred " + localDate);
      System.err.println(paramString);
      if (paramException != null) {
        System.err.println(paramException.getMessage());
      }
      System.err.println("----------------------------");
      PrintWriter localPrintWriter = new PrintWriter(new FileOutputStream("error.log", true));
      localPrintWriter.println("Error occurred " + localDate);
      localPrintWriter.println(paramString);
      if (paramException != null) {
        localPrintWriter.println(paramException.getMessage());
      }
      localPrintWriter.println("----------------------------");
      localPrintWriter.close();
      return;
    }
    catch (IOException localIOException)
    {
      System.err.println("Error writing to error.log: " + localIOException.getMessage());
    }
  }
}
