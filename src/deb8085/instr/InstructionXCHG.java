package deb8085.instr;

import deb8085.CPU8085;
import deb8085.Reg8085;

//***************************************************************************************************
/* XCHG 命令 */
public class InstructionXCHG extends Instruction8085 {
	public InstructionXCHG(CPU8085 cpu, byte p1, String p2, byte p3) {
		super(cpu, p1, p2, p3);
	}

	public void execute() {
		int valDE = cpu.reg.getReg(Reg8085.DE);
		int valHL = cpu.reg.getReg(Reg8085.HL);

		cpu.reg.setReg(Reg8085.HL, valDE);
		cpu.reg.setReg(Reg8085.DE, valHL);

		cpu.incPC(getSize());
	}

}
