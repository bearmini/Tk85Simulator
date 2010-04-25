package   deb8085.instr;
import    deb8085.CPU8085;
import    deb8085.Reg8085;
//***************************************************************************************************
/* CMC –½—ß */
public class   InstructionCMC extends Instruction8085
  {
  public   InstructionCMC( CPU8085   cpu, byte   p1, String   p2, byte   p3 )
    {
    super( cpu, p1, p2, p3 );
    }

  public void   execute()
    {
    cpu.reg.setFlag( Reg8085.Cf, !cpu.reg.getFlag( Reg8085.Cf ) );
    cpu.incPC( getSize() );
    }

  }


