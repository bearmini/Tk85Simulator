package   deb8085.instr;
import    deb8085.CPU8085;
import    deb8085.Reg8085;
//***************************************************************************************************
/* PUSH –½—ß */
public class   InstructionPUSH extends Instruction8085
  {
  public   InstructionPUSH( CPU8085   cpu, byte   p1, String   p2, byte   p3 )
    {
    super( cpu, p1, p2, p3 );
    }

  public void   execute()
    {
    int     pair = cpu.reg.getReg( (short)StringToPairReg( getOperands() ) );
    short   reg1 = (short)(( pair&0xFF00 )>>>8);
    short   reg2 = (short)(  pair&0x00FF );
    int     sp   = cpu.reg.getReg( Reg8085.SP );

    cpu.mem.setValue( sp-1, reg1 );
    cpu.mem.setValue( sp-2, reg2 );
    cpu.reg.setReg( Reg8085.SP, sp-2 );
    cpu.incPC( getSize() );
    }

  }


