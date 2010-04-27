package deb8085.instr;

import deb8085.CPU8085;
import deb8085.Reg8085;

//***************************************************************************************************
/* DAA ���� */
public class InstructionDAA extends Instruction8085 {
	public InstructionDAA(CPU8085 cpu, byte p1, String p2, byte p3) {
		super(cpu, p1, p2, p3);
	}

	public void execute() {
		short regAorg = cpu.reg.getReg(Reg8085.A);
		short bcd;

		// ���O�̖��߂ɂ���āA���Z�␳�������͌��Z�␳���s��
		// �i��PD8085AC �� 8085 �ƈ���āA���Z��� BCD �␳���ł���j
		if (!cpu.subtractedFlag) {
			bcd = bcdAdd(regAorg);
		} else {
			bcd = bcdSub(regAorg);
		}
		cpu.reg.setReg(Reg8085.A, bcd);
		cpu.updateFlags(CPU8085.FLAGUPDATE_ALL);
		cpu.reg.setFlag(Reg8085.Cf, calcBcdCarry(regAorg, bcd));
		cpu.reg.setFlag(Reg8085.ACf, calcBcdHalfCarry(regAorg, bcd));
		cpu.incPC(getSize());
	}

	private short bcdAdd(short val) {
		int l = (val & 0x0F);
		boolean carry_l = false;

		if ((l > 9) || cpu.reg.getFlag(Reg8085.ACf)) {
			val += 6;
			carry_l = (val > 0xFF);
		}

		int h = (val & 0xF0) >>> 4;

		if ((h > 9) || cpu.reg.getFlag(Reg8085.Cf) || carry_l) {
			val += 0x60;
		}

		return val;
	}

	private short bcdSub(short val) {
		return val; // not implemented
	}

	public boolean calcBcdCarry(int val, int bcd) {
		if (!cpu.subtractedFlag) {
			return bcdAdd((short) val) > 0xFF;
		} else {
			return bcdSub((short) val) < 0;
		}
	}

	public boolean calcBcdHalfCarry(int val, int bcd) {
		if (!cpu.subtractedFlag) {
			int org_l = (val & 0x0F);
			int bcd_l = (bcd & 0x0F);
			if (bcd_l < org_l) {
				return true;
			}
			return false;
		} else {
			return false;
		}

	}

}
