package   deb8085.instr;
import    deb8085.CPU8085;
import    deb8085.Reg8085;
//***************************************************************************************************
/* CMA –½—ß */
public class   InstructionCMA extends Instruction8085
  {
  public   InstructionCMA( CPU8085   cpu, byte   p1, String   p2, byte   p3 )
    {
    super( cpu, p1, p2, p3 );
    }

  public void   execute()
    {
    short   valA = cpu.reg.getReg( Reg8085.A );
    short   val = (short)((~valA) & 0xFF);

    cpu.reg.setReg( Reg8085.A, val );
    cpu.incPC( getSize() );
    }

  }


