package com.bearmini.tk85.base8085.instructions;

import com.bearmini.tk85.base8085.CPU8085;

//***************************************************************************************************
/* DCX 命令 */
public class InstructionDCX extends Instruction8085 {
    public InstructionDCX(CPU8085 cpu, byte p1, String p2, byte p3) {
        super(cpu, p1, p2, p3);
    }

    public void execute() {
        short regd = StringToPairReg(getOperands());
        int val = cpu.reg.getReg(regd);

        cpu.reg.setReg(regd, val - 1);
        cpu.incPC(getSize());
    }

}
