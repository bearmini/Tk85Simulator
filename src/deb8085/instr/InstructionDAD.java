package   deb8085.instr;
import    deb8085.CPU8085;
import    deb8085.Reg8085;
//***************************************************************************************************
/* DAD –½—ß */
public class   InstructionDAD extends Instruction8085
  {
  public   InstructionDAD( CPU8085   cpu, byte   p1, String   p2, byte   p3 )
    {
    super( cpu, p1, p2, p3 );
    }

  public void   execute()
    {
    int   regHL = cpu.reg.getReg( Reg8085.HL );
    int   val   = cpu.reg.getReg( StringToPareReg( getOperands() ) );

    cpu.reg.setReg( Reg8085.HL, regHL+val );
    cpu.updateFlags( CPU8085.FLAGUPDATE_CARRY_ONLY | CPU8085.FLAGUPDATE_RESET_SUB );
    cpu.incPC( getSize() );
    }

  }


