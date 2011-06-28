package com.bearmini.tk85.base8085.instructions;

import com.bearmini.tk85.base8085.CPU8085;

//***************************************************************************************************
/* EI 命令 */
public class InstructionEI extends Instruction8085 {
    public InstructionEI(CPU8085 cpu, byte p1, String p2, byte p3) {
        super(cpu, p1, p2, p3);
    }

    public void execute() {
        cpu.interruptEnabled = true;
        cpu.incPC(getSize());
    }

}
