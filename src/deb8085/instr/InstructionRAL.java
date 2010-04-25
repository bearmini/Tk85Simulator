package   deb8085.instr;
import    deb8085.CPU8085;
import    deb8085.Reg8085;
import    deb8085.*;
//***************************************************************************************************
/* RAL –½—ß */
public class   InstructionRAL extends Instruction8085
  {
  public   InstructionRAL( CPU8085   cpu, byte   p1, String   p2, byte   p3 )
    {
    super( cpu, p1, p2, p3 );
    }

  public void   execute()
    {
    short   regA = cpu.reg.getReg( Reg8085.A );
    boolean   cf = cpu.reg.getFlag( Reg8085.Cf );
    boolean   a7 = util.bitOn( regA, 7 );
    short    val = (short)( (regA<<1) + (cf?1:0) );

    cpu.reg.setFlag( Reg8085.Cf, a7 );
    cpu.reg.setReg( Reg8085.A, val );
    cpu.incPC( getSize() );
    }

  }


