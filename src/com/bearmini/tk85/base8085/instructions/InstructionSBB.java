package com.bearmini.tk85.base8085.instructions;

import com.bearmini.tk85.base8085.CPU8085;
import com.bearmini.tk85.base8085.Reg8085;

//***************************************************************************************************
/* SBB 命令 */
public class InstructionSBB extends Instruction8085 {
    public InstructionSBB(CPU8085 cpu, byte p1, String p2, byte p3) {
        super(cpu, p1, p2, p3);
    }

    public void execute() {
        short regAorg = cpu.reg.getReg(Reg8085.A);
        short val = cpu.reg.getReg(StringToReg(getOperands()));
        byte flag = (cpu.reg.getFlag(Reg8085.Cf)) ? (byte) 1 : (byte) 0;

        cpu.reg.setReg(Reg8085.A, (short) (regAorg - val - flag));
        cpu.updateFlags(CPU8085.FLAGUPDATE_ALL
                | CPU8085.FLAGUPDATE_SET_SUBTRUCTED);
        cpu.reg.setFlag(Reg8085.Cf, calcSubCarry(regAorg, val - flag));
        cpu.reg.setFlag(Reg8085.ACf, calcSubHalfCarry(regAorg, val - flag));
        cpu.incPC(getSize());
    }

}
