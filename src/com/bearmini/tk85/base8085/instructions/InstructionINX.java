package com.bearmini.tk85.base8085.instructions;

import com.bearmini.tk85.base8085.CPU8085;

//***************************************************************************************************
/* INX 命令 */
public class InstructionINX extends Instruction8085 {
    public InstructionINX(CPU8085 cpu, byte p1, String p2, byte p3) {
        super(cpu, p1, p2, p3);
    }

    public void execute() {
        short regi = StringToPairReg(getOperands());
        int val = cpu.reg.getReg(regi);

        cpu.reg.setReg(regi, val + 1);
        cpu.incPC(getSize());
    }

}
