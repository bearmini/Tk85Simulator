package deb8085.instr;

import deb8085.CPU8085;
import deb8085.Reg8085;

//***************************************************************************************************
/* RET –½—ßŒn */
public class InstructionRET extends Instruction8085 {
	public InstructionRET(CPU8085 cpu, byte p1, String p2, byte p3) {
		super(cpu, p1, p2, p3);
	}

	public void execute() {
		String terms = getMnemonic().substring(1);
		boolean conditionOK = false;

		if (terms.equals("ET"))
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

			short pcH = cpu.mem.getValue(sp + 1);
			short pcL = cpu.mem.getValue(sp);

			cpu.reg.setReg(Reg8085.PC, (pcH << 8) + pcL);
			cpu.reg.setReg(Reg8085.SP, sp + 2);
		} else
			cpu.incPC(getSize());

	}

}
