package   deb8085.instr;
import    deb8085.*;
//***************************************************************************************************
/* LXI 命令 */
public class   InstructionLXI extends Instruction8085
  {
  public   InstructionLXI( CPU8085   cpu, byte   p1, String   p2, byte   p3 )
    {
    super( cpu, p1, p2, p3 );
    }

  public void   execute()
    {
    short   regl = StringToPareReg( getOperands() );
    int     val  = getB3B2();

    cpu.reg.setReg( regl, val );
    cpu.incPC( getSize() );
    }

  public String   toString()
    {
    return   getMnemonic() + "," + util.hex4(getB3B2());
    }

  public void   encode( String   operand1, String   operand2 ) throws OnEncodeException
    {
    if( operand1 == null )
      throw   new OnEncodeException( "不正なオペランド（２つめ）" );
    int   addr = util.unhex( operand2 );
    setB2( (short)( addr % 0x100 ) );
    setB3( (short)( addr / 0x100 ) );
    }

  }


