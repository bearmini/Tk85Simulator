package deb8085.instr;

import deb8085.*;

//***************************************************************************************************
/* JMP 命令系 */
public class InstructionJMP extends Instruction8085 {
	public InstructionJMP(CPU8085 cpu, byte p1, String p2, byte p3) {
		super(cpu, p1, p2, p3);
	}

	public void execute() {
		String terms = getMnemonic().substring(1);
		boolean conditionOK = false;

		if (terms.equals("MP"))
			conditionOK = true;
		else if (terms.equals("NZ"))
			conditionOK = !cpu.reg.getFlag(Reg8085.Zf);
		else if (terms.equals("Z"))
			conditionOK = cpu.reg.getFlag(Reg8085.Zf);
		else if (terms.equals("NC"))
			conditionOK = !cpu.reg.getFlag(Reg8085.Cf);
		else if (terms.equals("C"))
			conditionOK = cpu.reg.getFlag(Reg8085.Cf);
		else if (terms.equals("PO"))
			conditionOK = !cpu.reg.getFlag(Reg8085.Pf);
		else if (terms.equals("PE"))
			conditionOK = cpu.reg.getFlag(Reg8085.Pf);
		else if (terms.equals("P"))
			conditionOK = !cpu.reg.getFlag(Reg8085.Sf);
		else if (terms.equals("M"))
			conditionOK = cpu.reg.getFlag(Reg8085.Sf);

		if (conditionOK)
			cpu.reg.setReg(Reg8085.PC, getB3B2());
		else
			cpu.incPC(getSize());

	}

	public String toString() {
		int addr = getB3B2();
		if (!cpu.publicLabels.existPublicLabel(addr))
			return getMnemonic() + " " + util.hex4(addr);
		else
			return getMnemonic() + " " + cpu.publicLabels.toPublicLabel(addr);
	}

	public void encode(String operand1, String operand2)
			throws OnEncodeException {
		if (operand1 == null)
			throw new OnEncodeException("不正なオペランド（１つめ）");
		int addr = util.unhex(operand1);
		setB2((short) (addr % 0x100));
		setB3((short) (addr / 0x100));
	}

}
