package deb8085.instr;

import deb8085.CPU8085;
import deb8085.Reg8085;

//***************************************************************************************************
/* STC 命令 */
public class InstructionSTC extends Instruction8085 {
	public InstructionSTC(CPU8085 cpu, byte p1, String p2, byte p3) {
		super(cpu, p1, p2, p3);
	}

	public void execute() {
		cpu.reg.setFlag(Reg8085.Cf, true);
		cpu.incPC(getSize());
	}

}
