package   deb8085.instr;
import    deb8085.CPU8085;
import    deb8085.Reg8085;
//***************************************************************************************************
/* LDAX –½—ß */
public class   InstructionLDAX extends Instruction8085
  {
  public   InstructionLDAX( CPU8085   cpu, byte   p1, String   p2, byte   p3 )
    {
    super( cpu, p1, p2, p3 );
    }

  public void   execute()
    {
    int    addr = cpu.reg.getReg( StringToPairReg( getOperands() ) );
    short   val = cpu.mem.getValue( addr );

    cpu.reg.setReg( Reg8085.A, val );
    cpu.incPC( getSize() );
    }

  }


