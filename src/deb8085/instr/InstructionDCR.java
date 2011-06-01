package deb8085.instr;

import deb8085.CPU8085;
import deb8085.Reg8085;

//***************************************************************************************************
/* DCR 命令 */
public class InstructionDCR extends Instruction8085 {
	public InstructionDCR(CPU8085 cpu, byte p1, String p2, byte p3) {
		super(cpu, p1, p2, p3);
	}

	public void execute() {
		byte regd = StringToReg(getOperands());
		short val = cpu.reg.getReg(regd);

		cpu.reg.setReg(regd, (short) (val - 1));
		cpu.updateFlags(CPU8085.FLAGUPDATE_INRDCR
				| CPU8085.FLAGUPDATE_SET_SUBTRUCTED, regd);
		cpu.reg.setFlag(Reg8085.Cf, calcSubCarry(regd, 1));
		cpu.reg.setFlag(Reg8085.ACf, calcSubHalfCarry(regd, 1));
		cpu.incPC(getSize());
	}

}
