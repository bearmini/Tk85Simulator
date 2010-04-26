package deb8085.instr;

import deb8085.CPU8085;

//***************************************************************************************************
/* DI –½—ß */
public class InstructionDI extends Instruction8085 {
	public InstructionDI(CPU8085 cpu, byte p1, String p2, byte p3) {
		super(cpu, p1, p2, p3);
	}

	public void execute() {
		cpu.interruptEnabled = false;
		cpu.incPC(getSize());
	}

}
