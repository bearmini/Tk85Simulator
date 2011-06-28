package com.bearmini.tk85.base8085.instructions;

import com.bearmini.tk85.base8085.CPU8085;
import com.bearmini.tk85.base8085.Reg8085;

//***************************************************************************************************
/* POP 命令 */
public class InstructionPOP extends Instruction8085 {
    public InstructionPOP(CPU8085 cpu, byte p1, String p2, byte p3) {
        super(cpu, p1, p2, p3);
    }

    public void execute() {
        short parereg = StringToPairReg(getOperands());
        int sp = cpu.reg.getReg(Reg8085.SP);

        short mem1 = cpu.mem.getValue(sp);
        short mem2 = cpu.mem.getValue(sp + 1);

        cpu.reg.setReg(parereg, (mem2 << 8) + mem1);
        cpu.reg.setReg(Reg8085.SP, sp + 2);
        cpu.incPC(getSize());
    }

}
