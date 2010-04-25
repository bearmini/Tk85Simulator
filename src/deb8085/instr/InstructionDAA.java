package   deb8085.instr;
import    deb8085.CPU8085;
import    deb8085.Reg8085;
//***************************************************************************************************
/* DAA –½—ß */
public class   InstructionDAA extends Instruction8085
  {
  public   InstructionDAA( CPU8085   cpu, byte   p1, String   p2, byte   p3 )
    {
    super( cpu, p1, p2, p3 );
    }

  public void   execute()
    {
    short   val = cpu.reg.getReg( Reg8085.A );
    short   l = (short)(val & 0x0F);

    // ‰ÁŽZ•â³
    if( cpu.subflag == false )
      {
      if( l > 9 )
        val += 6;
      }
    // Œ¸ŽZ•â³
    else
      {
      if( l > 9 )
        val -= 6;
      }

    cpu.reg.setReg( Reg8085.A, val );
    cpu.incPC( getSize() );
    }

  }


