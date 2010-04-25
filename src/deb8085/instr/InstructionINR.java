package   deb8085.instr;
import    deb8085.CPU8085;
//***************************************************************************************************
/* INR –½—ß */
public class   InstructionINR extends Instruction8085
  {
  public   InstructionINR( CPU8085   cpu, byte   p1, String   p2, byte   p3 )
    {
    super( cpu, p1, p2, p3 );
    }

  public void   execute()
    {
    byte   regi = StringToReg( getOperands() );
    short   val = cpu.reg.getReg( regi );

    cpu.reg.setReg( regi, val+1 );
    cpu.updateFlags( CPU8085.FLAGUPDATE_INRDCR | CPU8085.FLAGUPDATE_RESET_SUB, regi );
    cpu.incPC( getSize() );
    }

  }


