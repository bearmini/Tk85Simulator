package com.bearmini.tk85.base8085.instructions;

import com.bearmini.tk85.base8085.CPU8085;

//***************************************************************************************************
/* RST 命令 */
public class InstructionRST extends Instruction8085 {
    public InstructionRST(CPU8085 cpu, byte p1, String p2, byte p3) {
        super(cpu, p1, p2, p3);
    }

    public void execute() {
        cpu.incPC(getSize());
    }

}
