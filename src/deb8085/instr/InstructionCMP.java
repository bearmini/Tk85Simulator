package deb8085.instr;

import deb8085.CPU8085;
import deb8085.Reg8085;

//***************************************************************************************************
/* CMP –½—ß */
public class InstructionCMP extends Instruction8085 {
	public InstructionCMP(CPU8085 cpu, byte p1, String p2, byte p3) {
		super(cpu, p1, p2, p3);
	}

	public void execute() {
		short regAorg = cpu.reg.getReg(Reg8085.A);
		short val = cpu.reg.getReg(StringToReg(getOperands()));

		cpu.reg.setReg(Reg8085.A, (short) (regAorg - val));
		cpu.updateFlags(CPU8085.FLAGUPDATE_ALL
				| CPU8085.FLAGUPDATE_SET_SUBTRUCTED);
		cpu.reg.setFlag(Reg8085.Cf, calcSubCarry(regAorg, val));
		cpu.reg.setFlag(Reg8085.ACf, calcSubHalfCarry(regAorg, val));
		cpu.restoreAreg();
		cpu.incPC(getSize());
	}

}
