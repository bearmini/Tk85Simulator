package   deb8085.instr;
import    deb8085.*;
//***************************************************************************************************
/* MVI ���� */
public class   InstructionMVI extends Instruction8085
  {
  public   InstructionMVI( CPU8085   cpu, byte   p1, String   p2, byte   p3 )
    {
    super( cpu, p1, p2, p3 );
    }

  public void   execute()
    {
    byte   regd = StringToReg( getOperands() );
    short   val = getB2();

    cpu.reg.setReg( regd, val );
    cpu.incPC( getSize() );
    }

  public String   toString()
    {
    return   getMnemonic() + "," +util.hex2(getB2());
    }

  public void   encode( String   operand1, String   operand2 ) throws OnEncodeException
    {
    if( operand2 == null )
      throw   new OnEncodeException( "�s���ȃI�y�����h�i�Q�߁j" );
    setB2( (short)util.unhex( operand2 ) );
    }

  }


