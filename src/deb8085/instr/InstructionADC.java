package deb8085.instr;

import deb8085.CPU8085;
import deb8085.Reg8085;

//***************************************************************************************************
/* ADC 命令 */
public class InstructionADC extends Instruction8085 {
	public InstructionADC(CPU8085 cpu, byte p1, String p2, byte p3) {
		super(cpu, p1, p2, p3);
	}

	public void execute() {
		short regAorg = cpu.reg.getReg(Reg8085.A);
		short val = cpu.reg.getReg(StringToReg(getOperands()));
		byte flag = (cpu.reg.getFlag(Reg8085.Cf)) ? (byte) 1 : (byte) 0;

		cpu.reg.setReg(Reg8085.A, (short) (regAorg + val + flag));
		cpu.updateFlags(CPU8085.FLAGUPDATE_ALL
				| CPU8085.FLAGUPDATE_RESET_SUBTRUCTED);
		cpu.reg.setFlag(Reg8085.Cf, calcAddCarry(regAorg, val + flag));
		cpu.reg.setFlag(Reg8085.ACf, calcAddHalfCarry(regAorg, val + flag));
		cpu.incPC(getSize());
	}

}
