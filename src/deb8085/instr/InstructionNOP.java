package deb8085.instr;

import deb8085.CPU8085;

//***************************************************************************************************
/* NOP 命令 */
public class InstructionNOP extends Instruction8085 {
	public InstructionNOP(CPU8085 cpu, byte p1, String p2, byte p3) {
		super(cpu, p1, p2, p3);
	}

	public void execute() {
		cpu.incPC(getSize());
	}

}
