package com.bearmini.tk85.base8085.instructions;

import com.bearmini.tk85.base8085.CPU8085;
import com.bearmini.tk85.base8085.Reg8085;

//***************************************************************************************************
/* CMA 命令 */
public class InstructionCMA extends Instruction8085 {
    public InstructionCMA(CPU8085 cpu, byte p1, String p2, byte p3) {
        super(cpu, p1, p2, p3);
    }

    public void execute() {
        short valA = cpu.reg.getReg(Reg8085.A);
        short val = (short) ((~valA) & 0xFF);

        cpu.reg.setReg(Reg8085.A, val);
        cpu.incPC(getSize());
    }

}
