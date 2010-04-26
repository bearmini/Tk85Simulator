package deb8085.instr;

import deb8085.CPU8085;
import deb8085.Reg8085;

//***************************************************************************************************
/* PCHL ���� */
public class InstructionPCHL extends Instruction8085 {
	public InstructionPCHL(CPU8085 cpu, byte p1, String p2, byte p3) {
		super(cpu, p1, p2, p3);
	}

	public void execute() {
		int valPC = cpu.reg.getReg(Reg8085.PC);
		int valHL = cpu.reg.getReg(Reg8085.HL);

		cpu.reg.setReg(Reg8085.HL, valPC);
		cpu.reg.setReg(Reg8085.PC, valHL);

		// ���̖��ߎ��̂ɂ���� PC �̒l���ς���Ă��܂��̂�
		// ���̍s�͗v��Ȃ�
		// cpu.incPC( getSize() );
	}

}
