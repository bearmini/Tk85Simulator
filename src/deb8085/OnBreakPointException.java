package   deb8085;

import   java.lang.Throwable;


public class   OnBreakPointException extends Throwable
  {
  String   message;


  public   OnBreakPointException()
    {
    }

  public   OnBreakPointException( String   s )
    {
    message = s;
    }


  }