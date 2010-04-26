package deb8085.instr;

import java.util.StringTokenizer;
import deb8085.CPU8085;

//***************************************************************************************************
/* MOV –½—ß */
public class InstructionMOV extends Instruction8085 {
	public InstructionMOV(CPU8085 cpu, byte p1, String p2, byte p3) {
		super(cpu, p1, p2, p3);
	}

	public void execute() {
		StringTokenizer st = new StringTokenizer(getOperands(), ",");
		byte regd = StringToReg(st.nextToken());
		byte regs = StringToReg(st.nextToken());
		short val = cpu.reg.getReg(regs);

		cpu.reg.setReg(regd, val);
		cpu.incPC(getSize());
	}

}
