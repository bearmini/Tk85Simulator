package deb8085.instr;

import deb8085.*;

//***************************************************************************************************
/* CALL 命令系 */
public class InstructionCALL extends Instruction8085 {
	public InstructionCALL(CPU8085 cpu, byte p1, String p2, byte p3) {
		super(cpu, p1, p2, p3);
	}

	public void execute() {
		String terms = getMnemonic().substring(1);
		boolean conditionOK = false;

		if (terms.equals("ALL"))
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

		if (conditionOK) {
			int sp = cpu.reg.getReg(Reg8085.SP);
			int pc = cpu.reg.getReg(Reg8085.PC) + getSize();// 命令再開アドレスは、このCall命令の次の命令のアドレス
			short pcH = (short) ((pc & 0xFF00) >>> 8);
			short pcL = (short) (pc & 0x00FF);

			cpu.mem.setValue(sp - 1, pcH);
			cpu.mem.setValue(sp - 2, pcL);
			cpu.reg.setReg(Reg8085.PC, getB3B2());
			cpu.reg.setReg(Reg8085.SP, sp - 2);
		} else
			cpu.incPC(getSize());

	}

	public String toString() {
		int addr = getB3B2();
		if (!cpu.publicLabels.existPublicLabel(addr))
			return getMnemonic() + " " + util.hex4(addr);
		else
			return getMnemonic() + " "
					+ cpu.publicLabels.toPublicLabelName(addr);
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
