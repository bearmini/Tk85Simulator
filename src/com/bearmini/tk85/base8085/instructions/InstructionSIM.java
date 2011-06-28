package com.bearmini.tk85.base8085.instructions;

import com.bearmini.tk85.base8085.CPU8085;
import com.bearmini.tk85.base8085.Reg8085;
import com.bearmini.tk85.base8085.util;

//***************************************************************************************************
/* SIM 命令 */
public class InstructionSIM extends Instruction8085 {
    public InstructionSIM(CPU8085 cpu, byte p1, String p2, byte p3) {
        super(cpu, p1, p2, p3);
    }

    public void execute() {
        short regA = cpu.reg.getReg(Reg8085.A);

        if (util.bitOn(regA, 3)) {
            cpu.interruptMaskedRST75 = util.bitOn(regA, 2);
            cpu.interruptMaskedRST65 = util.bitOn(regA, 1);
            cpu.interruptMaskedRST55 = util.bitOn(regA, 0);
        }
        if (util.bitOn(regA, 4))
            cpu.interruptMaskedRST75 = false;

        cpu.incPC(getSize());
    }

}
