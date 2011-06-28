package com.bearmini.tk85.base8085.instructions;

import com.bearmini.tk85.base8085.CPU8085;
import com.bearmini.tk85.base8085.Reg8085;

//***************************************************************************************************
/* STAX 命令 */
public class InstructionSTAX extends Instruction8085 {
    public InstructionSTAX(CPU8085 cpu, byte p1, String p2, byte p3) {
        super(cpu, p1, p2, p3);
    }

    public void execute() {
        int addr = cpu.reg.getReg(StringToPairReg(getOperands()));
        short val = cpu.reg.getReg(Reg8085.A);

        cpu.mem.setValue(addr, val);
        cpu.incPC(getSize());
    }

}
