package com.bearmini.tk85.base8085.instructions;

import com.bearmini.tk85.base8085.CPU8085;
import com.bearmini.tk85.base8085.Reg8085;

//***************************************************************************************************
/* ANA 命令 */
public class InstructionANA extends Instruction8085 {
    public InstructionANA(CPU8085 cpu, byte p1, String p2, byte p3) {
        super(cpu, p1, p2, p3);
    }

    public void execute() {
        short regA = cpu.reg.getReg(Reg8085.A);
        short val = cpu.reg.getReg(StringToReg(getOperands()));

        cpu.reg.setReg(Reg8085.A, (short) (regA & val));
        cpu.updateFlags(CPU8085.FLAGUPDATE_CARRY_0);
        cpu.incPC(getSize());
    }

}
