package deb8085.instr;

import deb8085.CPU8085;
import deb8085.Reg8085;

//***************************************************************************************************
/* DAD 命令 */
public class InstructionDAD extends Instruction8085 {
	public InstructionDAD(CPU8085 cpu, byte p1, String p2, byte p3) {
		super(cpu, p1, p2, p3);
	}

	public void execute() {
		int regHLorg = cpu.reg.getReg(Reg8085.HL);
		int val = cpu.reg.getReg(StringToPairReg(getOperands()));

		cpu.reg.setReg(Reg8085.HL, regHLorg + val);
		cpu.updateFlags(CPU8085.FLAGUPDATE_RESET_SUBTRUCTED);
		cpu.reg.setFlag(Reg8085.Cf, calcAddCarry(regHLorg, val));
		cpu.reg.setFlag(Reg8085.ACf, calcAddHalfCarry(regHLorg, val));

		cpu.incPC(getSize());
	}

}
