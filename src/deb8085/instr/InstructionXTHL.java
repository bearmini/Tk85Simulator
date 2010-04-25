package   deb8085.instr;
import    deb8085.CPU8085;
import    deb8085.Reg8085;
//***************************************************************************************************
/* XTHL –½—ß */
public class   InstructionXTHL extends Instruction8085
  {
  public   InstructionXTHL( CPU8085   cpu, byte   p1, String   p2, byte   p3 )
    {
    super( cpu, p1, p2, p3 );
    }

  public void   execute()
    {
    int   sp = cpu.reg.getReg( Reg8085.SP );

    short   memL = cpu.mem.getValue( sp );
    short   memH = cpu.mem.getValue( sp+1 );

    int    valHL = cpu.reg.getReg( Reg8085.HL );
    short   valL =(short)(  valHL & 0x00FF );
    short   valH =(short)(( valHL & 0xFF00 ) >>> 8);

    cpu.reg.setReg( Reg8085.L, memL );
    cpu.reg.setReg( Reg8085.H, memH );
    cpu.mem.setValue( sp,   valL );
    cpu.mem.setValue( sp+1, valH );

    cpu.incPC( getSize() );
    }

  }


