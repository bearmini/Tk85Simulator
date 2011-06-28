package com.bearmini.tk85.base8085.instructions;

import com.bearmini.tk85.base8085.CPU8085;
import com.bearmini.tk85.base8085.Reg8085;

//***************************************************************************************************
/* SPHL 命令 */
public class InstructionSPHL extends Instruction8085 {
    public InstructionSPHL(CPU8085 cpu, byte p1, String p2, byte p3) {
        super(cpu, p1, p2, p3);
    }

    public void execute() {
        int valSP = cpu.reg.getReg(Reg8085.SP);
        int valHL = cpu.reg.getReg(Reg8085.HL);

        cpu.reg.setReg(Reg8085.HL, valSP);
        cpu.reg.setReg(Reg8085.SP, valHL);

        cpu.incPC(getSize());
    }

}
