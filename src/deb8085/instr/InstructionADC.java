package   deb8085.instr;
import    deb8085.CPU8085;
import    deb8085.Reg8085;
//***************************************************************************************************
/* ADC –½—ß */
public class   InstructionADC extends Instruction8085
  {
  public   InstructionADC( CPU8085   cpu, byte   p1, String   p2, byte   p3 )
    {
    super( cpu, p1, p2, p3 );
    }

  public void   execute()
    {
    short   regA = cpu.reg.getReg( Reg8085.A );
    short   val  = cpu.reg.getReg( StringToReg( getOperands() ) );
    byte   flag  = (cpu.reg.getFlag( Reg8085.Cf ))?(byte)1:(byte)0;

    cpu.reg.setReg( Reg8085.A, (short)(regA+val+flag) );
    cpu.updateFlags( CPU8085.FLAGUPDATE_ALL | CPU8085.FLAGUPDATE_RESET_SUB );
    cpu.incPC( getSize() );
    }

  }


