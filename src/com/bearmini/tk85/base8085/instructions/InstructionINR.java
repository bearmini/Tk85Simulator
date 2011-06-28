package com.bearmini.tk85.base8085.instructions;

import com.bearmini.tk85.base8085.CPU8085;
import com.bearmini.tk85.base8085.Reg8085;

//***************************************************************************************************
/* INR 命令 */
public class InstructionINR extends Instruction8085 {
    public InstructionINR(CPU8085 cpu, byte p1, String p2, byte p3) {
        super(cpu, p1, p2, p3);
    }

    public void execute() {
        byte regi = StringToReg(getOperands());
        short val = cpu.reg.getReg(regi);

        cpu.reg.setReg(regi, val + 1);
        cpu.updateFlags(CPU8085.FLAGUPDATE_INRDCR
                | CPU8085.FLAGUPDATE_RESET_SUBTRUCTED, regi);
        cpu.reg.setFlag(Reg8085.Cf, calcAddCarry(regi, 1));
        cpu.reg.setFlag(Reg8085.ACf, calcAddHalfCarry(regi, 1));

        cpu.incPC(getSize());
    }

}
