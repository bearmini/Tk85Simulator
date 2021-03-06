package com.bearmini.tk85.base8085.instructions;

import com.bearmini.tk85.base8085.*;

//***************************************************************************************************
/* RIM 命令 */
public class InstructionRIM extends Instruction8085 {
	public InstructionRIM(CPU8085 cpu, byte p1, String p2, byte p3) {
		super(cpu, p1, p2, p3);
	}

	public void execute() {
		short val = 0;

		val = (short) util.setBit(val, 0, cpu.interruptMaskedRST55);
		val = (short) util.setBit(val, 1, cpu.interruptMaskedRST65);
		val = (short) util.setBit(val, 2, cpu.interruptMaskedRST75);
		val = (short) util.setBit(val, 3, cpu.interruptEnabled);

		cpu.reg.setReg(Reg8085.A, val);
		cpu.incPC(getSize());
	}

}
