package deb8085.instr;

import deb8085.*;

//***************************************************************************************************
/* SBI 命令 */
public class InstructionSBI extends Instruction8085 {
	public InstructionSBI(CPU8085 cpu, byte p1, String p2, byte p3) {
		super(cpu, p1, p2, p3);
	}

	public void execute() {
		short regAorg = cpu.reg.getReg(Reg8085.A);
		short val = getB2();
		byte flag = (cpu.reg.getFlag(Reg8085.Cf)) ? (byte) 1 : (byte) 0;

		cpu.reg.setReg(Reg8085.A, (short) (regAorg - val - flag));
		cpu.updateFlags(CPU8085.FLAGUPDATE_ALL
				| CPU8085.FLAGUPDATE_SET_SUBTRUCTED);
		cpu.reg.setFlag(Reg8085.Cf, calcSubCarry(regAorg, val - flag));
		cpu.reg.setFlag(Reg8085.ACf, calcSubHalfCarry(regAorg, val - flag));

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
