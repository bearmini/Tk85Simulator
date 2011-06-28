package com.bearmini.tk85.base8085.instructions;

import com.bearmini.tk85.base8085.CPU8085;
import com.bearmini.tk85.base8085.Reg8085;

//***************************************************************************************************
/* CMC 命令 */
public class InstructionCMC extends Instruction8085 {
    public InstructionCMC(CPU8085 cpu, byte p1, String p2, byte p3) {
        super(cpu, p1, p2, p3);
    }

    public void execute() {
        cpu.reg.setFlag(Reg8085.Cf, !cpu.reg.getFlag(Reg8085.Cf));
        cpu.incPC(getSize());
    }

}
