package com.bearmini.tk85.base8085.instructions;

import com.bearmini.tk85.base8085.*;

//***************************************************************************************************
/* XRI 命令 */
public class InstructionXRI extends Instruction8085 {
	public InstructionXRI(CPU8085 cpu, byte p1, String p2, byte p3) {
		super(cpu, p1, p2, p3);
	}

	public void execute() {
		short regA = cpu.reg.getReg(Reg8085.A);
		short val = getB2();

		cpu.reg.setReg(Reg8085.A, (short) (regA ^ val));
		cpu.updateFlags(CPU8085.FLAGUPDATE_CARRY_0);
		cpu.incPC(getSize());
	}

	public String toString() {
		return getMnemonic() + " " + util.hex2(getB2());
	}

	public void encode(String operand1, String operand2)
			throws OnEncodeException {
		if (operand1 == null)
			throw new OnEncodeException("不正なオペランド（１つめ）");
		setB2((short) util.unhex(operand1));
	}

}
