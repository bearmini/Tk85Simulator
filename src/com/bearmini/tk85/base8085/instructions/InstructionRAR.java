package com.bearmini.tk85.base8085.instructions;

import com.bearmini.tk85.base8085.*;

//***************************************************************************************************
/* RAR 命令 */
public class InstructionRAR extends Instruction8085 {
	public InstructionRAR(CPU8085 cpu, byte p1, String p2, byte p3) {
		super(cpu, p1, p2, p3);
	}

	public void execute() {
		short regA = cpu.reg.getReg(Reg8085.A);
		boolean cf = cpu.reg.getFlag(Reg8085.Cf);
		boolean a0 = util.bitOn(regA, 0);
		short val = (short) ((regA >>> 1) + (cf ? 0x80 : 0));

		cpu.reg.setFlag(Reg8085.Cf, a0);
		cpu.reg.setReg(Reg8085.A, val);
		cpu.incPC(getSize());
	}

}
