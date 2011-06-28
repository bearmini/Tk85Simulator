package com.bearmini.tk85.base8085;

import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.TextArea;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.StringTokenizer;

import com.bearmini.tk85.base8085.instructions.Instruction8085;
import com.bearmini.tk85.io.KeyboardInputStream;
import com.bearmini.tk85.io.MouseKiller;

public class Debugger extends Thread {
    private DebuggerParent parent;

    private boolean shouldExit = false;

    private Frame frame;
    private TextArea output;

    private CPU8085 cpu = null; // CPU
    private Mem8085 mem = null;

    private int codeAddr = 0;
    private int codeSize = 0;

    private int workAddr = 0;
    private int workSize = 0;

    public PublicLabelList publicLabels;

    public BreakPointList breakPoints;

    public KeyboardInputStream input;
    public MouseKiller mouseKiller;

    public Debugger(DebuggerParent parent, Frame frame, TextArea output) {
        this.parent = parent;
        this.frame = frame;
        this.output = output;

        publicLabels = new PublicLabelList();
        breakPoints = new BreakPointList();

        mem = new Mem8085();
        cpu = new CPU8085(mem, publicLabels, breakPoints);

        input = new KeyboardInputStream(output);
        mouseKiller = new MouseKiller(output);

        output.requestFocus();
    }

    public void requestStop() {
        this.shouldExit = true;
    }

    public void run() {
        println("");

        do {
            dispatch(getCommand());
        } while (!shouldExit);

    }

    public void dispatch(String command) {
        char kind = '\0';
        String param = "";

        try {
            kind = command.charAt(0);
            param = command.substring(1);
        } catch (StringIndexOutOfBoundsException e) {
        }

        switch (kind) {
        case 'A':
        case 'a': {
            StringTokenizer st = new StringTokenizer(param, ",");
            int startAddr;
            if (st.hasMoreTokens()) {
                startAddr = util.unhex(st.nextToken());
            } else {
                startAddr = cpu.reg.getReg(Reg8085.PC);
            }
            comAssemble(startAddr);

            break;
        }

        case 'B':
        case 'b': {
            StringTokenizer st = new StringTokenizer(param, ",");
            int addr;
            int count;

            int paramCount = st.countTokens();

            if (paramCount == 0) {
                comDisplayBreakPointInfo();
                break;
            } else if (paramCount == 1) {
                addr = util.unhex(st.nextToken());
                comResetBreakPoint(addr);
            } else if (paramCount == 2) {
                addr = util.unhex(st.nextToken());
                count = util.unhex(st.nextToken());
                comSetBreakPoint(addr, count);
            } else {
                println("スpススススス[ス^スススススススススワゑソス.");
                println("");
            }

            break;
        }

        //
        case 'C':
        case 'c':
            break;

        case 'D':
        case 'd': {
            StringTokenizer st = new StringTokenizer(param, ",");
            int startAddr, endAddr;
            if (st.hasMoreTokens())
                startAddr = util.unhex(st.nextToken());
            else
                startAddr = cpu.reg.getReg(Reg8085.PC);

            if (st.hasMoreTokens())
                endAddr = util.unhex(st.nextToken());
            else
                endAddr = startAddr + 127;

            comDumpMemoryArea(startAddr, endAddr);
            break;
        }

        case 'E':
        case 'e': {
            StringTokenizer st = new StringTokenizer(param, ",");
            int startAddr;
            if (st.hasMoreTokens())
                startAddr = util.unhex(st.nextToken());
            else
                startAddr = cpu.reg.getReg(Reg8085.PC);

            comEditMemoryArea(startAddr);
            break;
        }

        case 'F':
        case 'f': {
            StringTokenizer st = new StringTokenizer(param, ",");
            int startAddr, endAddr;
            short value;

            if (st.hasMoreTokens())
                startAddr = util.unhex(st.nextToken());
            else
                startAddr = cpu.reg.getReg(Reg8085.PC);

            if (st.hasMoreTokens())
                endAddr = util.unhex(st.nextToken());
            else
                endAddr = startAddr + 127;

            if (st.hasMoreTokens())
                value = (short) util.unhex(st.nextToken());
            else
                value = 0;

            comFillMemoryArea(startAddr, endAddr, value);
            break;
        }

        case 'G':
        case 'g': {
            StringTokenizer st = new StringTokenizer(param, ",");
            int startAddr, endAddr;

            if (st.hasMoreTokens())
                startAddr = util.unhex(st.nextToken());
            else
                startAddr = cpu.reg.getReg(Reg8085.PC);

            if (st.hasMoreTokens())
                endAddr = util.unhex(st.nextToken());
            else
                endAddr = 0xFFFF;

            comRunProgram(startAddr, endAddr);

            break;
        }

        case 'H':
        case 'h':
        case '?': {
            break;
        }

        case 'I':
        case 'i': {
            break;
        }

        case 'J':
        case 'j': {
            break;
        }

        //
        case 'K':
        case 'k': {
            break;
        }

        case 'L':
        case 'l': {
            String filename = param;

            if (filename.trim().equals("")) {
                filename = getFilename();
                if (filename == null)
                    break;
            }

            try {
                comLoadMicFile(filename);

                cpu.reg.setReg(Reg8085.PC, codeAddr);
            } catch (IOException e) {
                println("スtス@スCスススフ読み搾ソススンに趣ソススsスススワゑソススススB");
            }
            break;
        }

        case 'M':
        case 'm': {
            StringTokenizer st = new StringTokenizer(param, ",");
            int startAddr, endAddr, destAddr;

            if (st.hasMoreTokens())
                startAddr = util.unhex(st.nextToken());
            else
                startAddr = cpu.reg.getReg(Reg8085.PC);

            if (st.hasMoreTokens())
                endAddr = util.unhex(st.nextToken());
            else
                endAddr = startAddr + 127;

            if (st.hasMoreTokens())
                destAddr = util.unhex(st.nextToken());
            else
                destAddr = endAddr + 1;

            comMoveMemoryArea(startAddr, endAddr, destAddr);
            break;
        }

        case 'N':
        case 'n': {
            StringTokenizer st = new StringTokenizer(param, ",");
            String labelName;
            int addr;

            int paramCount = st.countTokens();

            if (paramCount == 0) {
                comDisplayPublicLabelInfo();
                break;
            } else if (paramCount == 1) {
                labelName = st.nextToken();
                comResetPublicLabel(labelName);
            } else if (paramCount == 2) {
                addr = util.unhex(st.nextToken());
                labelName = st.nextToken();
                comSetPublicLabel(labelName, addr);
            } else {
                println("スpススススス[ス^スススススススススワゑソス.");
                println("");
            }
            break;
        }

        //
        case 'O':
        case 'o': {
            break;
        }

        case 'P':
        case 'p': {
            StringTokenizer st = new StringTokenizer(param, ",");
            int startAddr, endAddr;
            boolean resetProtect = false;

            if (st.hasMoreTokens())
                startAddr = util.unhex(st.nextToken());
            else
                startAddr = cpu.reg.getReg(Reg8085.PC);

            if (st.hasMoreTokens())
                endAddr = util.unhex(st.nextToken());
            else
                endAddr = startAddr;

            if (st.hasMoreTokens()) {
                String str = st.nextToken();
                resetProtect = (str.equals("R") || str.equals("r"));
            }

            comProtectMemoryArea(startAddr, endAddr, resetProtect);
            break;
        }

        case 'Q':
        case 'q': {
            parent.onEndDebug();
            shouldExit = true;
            break;
        }

        case 'R':
        case 'r': {
            if (param.equals("X") || param.equals("x"))
                comEditRegister();
            else
                comDumpRegister();
            break;
        }

        //
        case 'S':
        case 's': {
            break;
        }

        case 'T':
        case 't': {
            StringTokenizer st = new StringTokenizer(param, ",");
            int startAddr, endAddr;
            boolean onestep;

            if (st.hasMoreTokens())
                startAddr = util.unhex(st.nextToken());
            else
                startAddr = cpu.reg.getReg(Reg8085.PC);

            if (st.hasMoreTokens())
                endAddr = util.unhex(st.nextToken());
            else
                endAddr = 0xFFFF;

            if (st.hasMoreTokens())
                onestep = true;
            else
                onestep = false;

            comTraceProgram(startAddr, endAddr, onestep);
            break;
        }

        case 'U':
        case 'u': {
            StringTokenizer st = new StringTokenizer(param, ",");
            int startAddr, endAddr;

            if (st.hasMoreTokens())
                startAddr = util.unhex(st.nextToken());
            else
                startAddr = cpu.reg.getReg(Reg8085.PC);

            if (st.hasMoreTokens())
                endAddr = util.unhex(st.nextToken());
            else
                endAddr = startAddr + 127;

            comDisassemble(startAddr, endAddr);
            break;
        }

        //
        case 'V':
        case 'v': {
            break;
        }

        case 'W':
        case 'w': {
            StringTokenizer st = new StringTokenizer(param, ",");
            int startAddr, endAddr;

            if (st.hasMoreTokens())
                startAddr = util.unhex(st.nextToken());
            else
                startAddr = cpu.reg.getReg(Reg8085.PC);

            if (st.hasMoreTokens())
                endAddr = util.unhex(st.nextToken());
            else
                endAddr = startAddr + 127;

            String filename = null;
            if (st.hasMoreTokens())
                filename = st.nextToken();

            if (filename == null || filename.trim().equals("")) {
                filename = getFilename();
                if (filename == null)
                    break;
            }

            try {
                comWriteToFile(startAddr, endAddr, filename);
            } catch (IOException e) {
                println("スtス@スCスススフ擾ソススススススンに趣ソススsスススワゑソススス.");
            }

            break;
        }

        //
        case 'X':
        case 'x': {
            break;
        }

        case 'Y':
        case 'y': {
            break;
        }

        case 'Z':
        case 'z': {
            comDisplayDebugInfo();
            break;
        }

        }

    }

    void printAddr(int addr) {
        print(util.hex4(addr));
    }

    void printAddrAsLineHeader(int addr) {
        println("");
        printAddr(addr);
        print(" ");
    }

    String sprintOpecode(Instruction8085 inst) {
        StringBuffer sb = new StringBuffer();

        sb.append(util.hex2(inst.getOpecode()) + " ");

        if (inst.getSize() == 1)
            sb.append(util.space(8));
        else if (inst.getSize() == 2)
            sb.append(util.hex2(inst.getB2()) + util.space(6));
        else if (inst.getSize() == 3)
            sb.append(util.hex2(inst.getB2()) + " " + util.hex2(inst.getB3())
                    + util.space(3));

        return sb.toString();
    }

    void printRegistersHeader() {
        println(" A  B  C  D  E  H  L  SP   PC  Z C S P AC");
    }

    void printRegisters() {
        print(util.hex2(cpu.reg.getReg(Reg8085.A)) + " "
                + util.hex2(cpu.reg.getReg(Reg8085.B)) + " "
                + util.hex2(cpu.reg.getReg(Reg8085.C)) + " "
                + util.hex2(cpu.reg.getReg(Reg8085.D)) + " "
                + util.hex2(cpu.reg.getReg(Reg8085.E)) + " "
                + util.hex2(cpu.reg.getReg(Reg8085.H)) + " "
                + util.hex2(cpu.reg.getReg(Reg8085.L)) + " "
                + util.hex4(cpu.reg.getReg(Reg8085.SP)) + " "
                + util.hex4(cpu.reg.getReg(Reg8085.PC)) + " "
                + (cpu.reg.getFlag(Reg8085.Zf) ? 1 : 0) + " "
                + (cpu.reg.getFlag(Reg8085.Cf) ? 1 : 0) + " "
                + (cpu.reg.getFlag(Reg8085.Sf) ? 1 : 0) + " "
                + (cpu.reg.getFlag(Reg8085.Pf) ? 1 : 0) + " "
                + (cpu.reg.getFlag(Reg8085.ACf) ? 1 : 0));
    }

    void printMemoryHeader() {
        println("Addr  0  1  2  3  4  5  6  7  8  9  A  B  C  D  E  F  0123456789ABCDEF");
    }

    void printMemoryAreaOneLine(int startAddr, int endAddr) {
        StringBuffer sbDump = new StringBuffer();
        StringBuffer sbHead = new StringBuffer();
        StringBuffer sbAscii = new StringBuffer();

        sbHead.append(util.hex4(startAddr % 0xFFF0) + " ");
        sbAscii.append('*');
        for (int addr = startAddr & 0xFFF0; addr < startAddr; addr++)
            sbDump.append(util.space(3));

        for (int addr = startAddr; addr <= endAddr; addr++) {
            char c = (char) mem.getValue(addr);
            sbDump.append(util.hex2(c));
            sbAscii.append(util.makeValidChar(c));
            if (addr % 0x10 != 7)
                sbDump.append(" ");
            else
                sbDump.append("-");
        }

        for (int addr = endAddr + 1; addr < ((endAddr + 0x10) & 0xFFF0); addr++)
            sbDump.append(util.space(3));

        println("" + sbHead + sbDump + sbAscii);
    }

    void printTraceInfo(Instruction8085 inst) {
        StringBuffer sbAddr = new StringBuffer();
        StringBuffer sbInst = new StringBuffer();

        sbAddr.append(util.hex4(cpu.reg.getReg(Reg8085.PC)));

        sbInst.append(sprintOpecode(inst) + inst.toString());
        int len = sbAddr.length() + sbInst.length();
        if (len < 23)
            sbInst.append("\t\t");
        else
            sbInst.append("\t");

        print("" + sbAddr + " " + sbInst);
    }

    void comAssemble(int startAddr) {
        int addr = startAddr;
        String s;
        int com_bytes;

        SimpleAssembler asm = new SimpleAssembler(cpu);

        do {
            print(util.hex4(addr) + ": ");
            s = readln();

            if (s.trim().equals(""))
                break;

            try {
                com_bytes = asm.assemble(addr, s);
            } catch (OnEncodeException e) {
                println(e.message);
                continue;
            }

            addr += com_bytes;

        } while (true);

        println("");
    }

    void comDisplayBreakPointInfo() {
        if (breakPoints.size() == 0) {
            println("No break points.");
            println("");
            return;
        }

        println("");
        println("Break points        count: " + breakPoints.size());
        println(" addr\tcount\tlabel");
        for (int num = 0; num < breakPoints.size(); num++) {
            BreakPoint8085 b = breakPoints.getBreakPointAt(num);
            print("" + util.hex4(b.addr));
            print("\t   ");
            print("" + b.count);
            println("");
        }
        println("");
    }

    void comSetBreakPoint(int addr, int count) {
        try {
            breakPoints.addBreakPoint(addr, count);
        } catch (BreakPointListException e) {
            println(e.message);
        }

        println("");
    }

    void comResetBreakPoint(int addr) {
        try {
            breakPoints.delBreakPoint(addr);
            println("Break point at address " + util.hex4(addr)
                    + " is deleted. OK.");
        } catch (BreakPointListException e) {
            println(e.message);
        }

        println("");
    }

    void comDumpMemoryArea(int startAddr, int endAddr) {
        printMemoryHeader();

        if ((startAddr & 0xFFF0) == (endAddr & 0xFFF0))
            printMemoryAreaOneLine(startAddr, endAddr);

        else {
            int addr = startAddr;

            printMemoryAreaOneLine(startAddr, (startAddr & 0xFFF0) + 0x0f);

            for (addr = (startAddr + 0x10) & 0xFFF0; addr <= (endAddr - 0x10); addr += 0x10)
                printMemoryAreaOneLine(addr, addr + 0x0f);

            if ((addr & 0xFFF0) == (endAddr & 0xFFF0))
                printMemoryAreaOneLine(addr, endAddr);
        }
        println("");
    }

    void comEditMemoryArea(int startAddr) {
        printMemoryHeader();

        int addr = startAddr;
        int blankaddr = startAddr & 0x0F;

        while (true) {
            printMemoryAreaOneLine(addr, (addr & 0xFFF0) + 0x0F);

            print("     " + util.space(blankaddr * 3));

            String s = readln();

            if (s.trim().equals(""))
                break;

            try {
                for (int i = 0; i < 0x10; i++)
                    mem.setValue(
                            addr + i,
                            !(s.substring(3 * i, 3 * i + 2).equals("  ")) ? util
                                    .unhex(s.substring(3 * i, 3 * i + 2)) : mem
                                    .getValue(addr + i));
            } catch (StringIndexOutOfBoundsException e) {
            }

            addr = (addr & 0xFFF0) + 0x10;
            blankaddr = 0;
        }

    }

    void comFillMemoryArea(int startAddr, int endAddr, short value) {
        for (int addr = startAddr; addr <= endAddr; addr++)
            mem.setValue(addr, value);
        println("");
    }

    void comRunProgram(int startAddr, int endAddr) {
        cpu.reg.setReg(Reg8085.PC, startAddr);
        cpu.restart();

        while (cpu.reg.getReg(Reg8085.PC) <= endAddr) {
            try {
                cpu.execute(cpu.decode(cpu.fetch()));
            } catch (OnBreakPointException e) {
                println(e.message);
            }

            if (cpu.isHalted())
                break;
        }
    }

    public void comLoadMicFile(String filename) throws IOException {
        DataInputStream d = new DataInputStream(new BufferedInputStream(
                new FileInputStream(filename)));

        byte id1 = d.readByte();
        byte id2 = d.readByte();
        if (!(id1 == 80/* 'P' */&& id2 == 72/* 'H' */)) {
            throw new IOException();
        }

        codeAddr = d.readUnsignedShort();
        codeSize = d.readUnsignedShort();
        workSize = d.readUnsignedShort();
        workAddr = d.readUnsignedShort();

        codeAddr = util.swapEndian(codeAddr);
        codeSize = util.swapEndian(codeSize);
        workAddr = util.swapEndian(workAddr);
        workSize = util.swapEndian(workSize);

        for (int addr = codeAddr; addr < codeAddr + codeSize; addr++)
            mem.setValue(addr, d.readUnsignedByte());

        byte id3 = d.readByte();
        byte id4 = d.readByte();
        if (!(id3 == 80/* 'P' */&& id4 == 66/* 'B' */)) {
            throw new IOException();
        }

        int numOfPublicLabels = d.readUnsignedShort();
        numOfPublicLabels = util.swapEndian(numOfPublicLabels);

        byte[] buf = new byte[8];
        String publicLabelName;
        int publicLabelAddr;

        for (int num = 0; num < numOfPublicLabels; num++) {
            d.read(buf);
            publicLabelName = new String(buf);
            publicLabelAddr = d.readUnsignedShort();
            publicLabelAddr = util.swapEndian(publicLabelAddr);
            publicLabels.addElement(new PublicLabel8085(publicLabelName,
                    publicLabelAddr));
        }

        d.close();
        comDisplayDebugInfo();
        comDisplayPublicLabelInfo();
    }

    void comMoveMemoryArea(int startAddr, int endAddr, int destAddr) {
        for (int addr = 0; addr <= endAddr - startAddr; addr++)
            mem.setValue(destAddr + addr, mem.getValue(startAddr + addr));
        println("");
    }

    void comDisplayPublicLabelInfo() {
        if (publicLabels.size() == 0) {
            println("No public labels.");
            println("");
            return;
        }

        println("");
        println("Public labels        count: " + publicLabels.size());
        println(" name      addr");
        for (int num = 0; num < publicLabels.size(); num++) {
            PublicLabel8085 p = publicLabels.getPublicLabelAt(num);
            print("" + p.name);
            print("\t   ");
            print("" + util.hex4(p.addr));
            println("");
        }
        println("");
    }

    void comSetPublicLabel(String labelName, int addr) {
        try {
            publicLabels.addPublicLabel(labelName, addr);
        } catch (PublicLabelListException e) {
            println(e.message);
        }

        println("");
    }

    void comResetPublicLabel(String labelName) {
        try {
            publicLabels.delPublicLabel(labelName);
            println("Public label " + labelName + " is deleted. OK.");
        } catch (PublicLabelListException e) {
            println(e.message);
        }

        println("");
    }

    void comProtectMemoryArea(int startAddr, int endAddr, boolean resetProtect) {
        for (int addr = startAddr; addr <= endAddr; addr++)
            mem.setReadOnly(addr, !resetProtect);
        println("");
    }

    void comDumpRegister() {
        printRegistersHeader();
        printRegisters();
        println("");
        println("");
    }

    void comEditRegister() {
        printRegistersHeader();
        printRegisters();
        println("");

        String s = readln();

        try {
            cpu.reg.setReg(
                    Reg8085.A,
                    !(s.substring(0, 2).equals("  ")) ? (short) util.unhex(s
                            .substring(0, 2)) : cpu.reg.getReg(Reg8085.A));
            cpu.reg.setReg(
                    Reg8085.B,
                    !(s.substring(3, 5).equals("  ")) ? (short) util.unhex(s
                            .substring(3, 5)) : cpu.reg.getReg(Reg8085.B));
            cpu.reg.setReg(
                    Reg8085.C,
                    !(s.substring(6, 8).equals("  ")) ? (short) util.unhex(s
                            .substring(6, 8)) : cpu.reg.getReg(Reg8085.C));
            cpu.reg.setReg(
                    Reg8085.D,
                    !(s.substring(9, 11).equals("  ")) ? (short) util.unhex(s
                            .substring(9, 11)) : cpu.reg.getReg(Reg8085.D));
            cpu.reg.setReg(
                    Reg8085.E,
                    !(s.substring(12, 14).equals("  ")) ? (short) util.unhex(s
                            .substring(12, 14)) : cpu.reg.getReg(Reg8085.E));
            cpu.reg.setReg(
                    Reg8085.H,
                    !(s.substring(15, 17).equals("  ")) ? (short) util.unhex(s
                            .substring(15, 17)) : cpu.reg.getReg(Reg8085.H));
            cpu.reg.setReg(
                    Reg8085.L,
                    !(s.substring(18, 20).equals("  ")) ? (short) util.unhex(s
                            .substring(18, 20)) : cpu.reg.getReg(Reg8085.L));
            cpu.reg.setReg(
                    Reg8085.SP,
                    !(s.substring(21, 25).equals("    ")) ? util.unhex(s
                            .substring(21, 25)) : cpu.reg.getReg(Reg8085.SP));
            cpu.reg.setReg(
                    Reg8085.PC,
                    !(s.substring(26, 30).equals("    ")) ? util.unhex(s
                            .substring(26, 30)) : cpu.reg.getReg(Reg8085.PC));
            cpu.reg.setFlag(Reg8085.Zf,
                    !(s.substring(31, 32).equals(" ")) ? s.substring(31, 32)
                            .equals("1") : cpu.reg.getFlag(Reg8085.Zf));
            cpu.reg.setFlag(Reg8085.Cf,
                    !(s.substring(33, 34).equals(" ")) ? s.substring(33, 34)
                            .equals("1") : cpu.reg.getFlag(Reg8085.Cf));
            cpu.reg.setFlag(Reg8085.Sf,
                    !(s.substring(35, 36).equals(" ")) ? s.substring(35, 36)
                            .equals("1") : cpu.reg.getFlag(Reg8085.Sf));
            cpu.reg.setFlag(Reg8085.Pf,
                    !(s.substring(37, 38).equals(" ")) ? s.substring(37, 38)
                            .equals("1") : cpu.reg.getFlag(Reg8085.Pf));
            cpu.reg.setFlag(Reg8085.ACf,
                    !(s.substring(39, 40).equals(" ")) ? s.substring(39, 40)
                            .equals("1") : cpu.reg.getFlag(Reg8085.ACf));
        } catch (StringIndexOutOfBoundsException e) {
        }

        println("");
    }

    void comTraceProgram(int startAddr, int endAddr, boolean traceOneStep) {
        String message = null;
        Instruction8085 inst;

        keyEchoOff();
        cpu.restart();

        try {
            cpu.reg.setReg(Reg8085.PC, startAddr);

            println("Addr Code       Mnemonic         A  B  C  D  E  H  L  SP   PC  Z C S P AC");

            while (cpu.reg.getReg(Reg8085.PC) <= endAddr) {
                inst = cpu.decode(cpu.fetch());

                printTraceInfo(inst);

                if (traceOneStep) {
                    int c = read();
                    if (c == 'Q' || c == 'q')
                        break;
                }
                try {
                    cpu.execute(inst);
                } catch (OnBreakPointException e) {
                    message = e.message;
                }

                printRegisters();
                println("");

                if (message != null)
                    println(message);

                if (cpu.isHalted())
                    break;

            }
        } finally {
            keyEchoOn();
        }

    }

    void comDisassemble(int startAddr, int endAddr) {

        int orgPC = cpu.reg.getReg(Reg8085.PC);
        int newPC;
        Instruction8085 inst;

        cpu.reg.setReg(Reg8085.PC, startAddr);
        while (cpu.reg.getReg(Reg8085.PC) <= endAddr) {
            inst = cpu.decode(cpu.fetch());

            printTraceInfo(inst);
            println("");

            if (inst.getMnemonic().equals("HLT"))
                break;

            newPC = cpu.reg.getReg(Reg8085.PC) + inst.getSize();
            cpu.reg.setReg(Reg8085.PC, newPC);
        }

        cpu.reg.setReg(Reg8085.PC, orgPC);

        println("");
    }

    void comWriteToFile(int startaddr, int endaddr, String filename)
            throws IOException {
        DataOutputStream d = new DataOutputStream(new BufferedOutputStream(
                new FileOutputStream(filename)));

        d.writeByte(80/* P */);
        d.writeByte(72/* H */);

        int temp_codeAddr = startaddr;
        int temp_codeSize = endaddr - startaddr + 1;
        int temp_workAddr = workAddr;
        int temp_workSize = workSize;

        int swaped_codeAddr = util.swapEndian(temp_codeAddr);
        int swaped_codeSize = util.swapEndian(temp_codeSize);
        int swaped_workAddr = util.swapEndian(temp_workAddr);
        int swaped_workSize = util.swapEndian(temp_workSize);

        d.writeShort((short) swaped_codeAddr);
        d.writeShort((short) swaped_codeSize);
        d.writeShort((short) swaped_workSize);
        d.writeShort((short) swaped_workAddr);

        for (int addr = temp_codeAddr; addr < temp_codeAddr + temp_codeSize; addr++)
            d.writeByte((byte) mem.getValue(addr));

        d.writeByte(80/* P */);
        d.writeByte(66/* B */);

        d.writeShort(util.swapEndian(publicLabels.size()));

        for (int num = 0; num < publicLabels.size(); num++) {
            PublicLabel8085 p = publicLabels.getPublicLabelAt(num);
            for (int i = 0; i < 8; i++)
                d.writeByte((byte) p.name.charAt(i));

            d.writeShort(util.swapEndian(p.addr));
        }

        d.close();
        println("ススススノ擾ソススススoスススワゑソススス.");
    }

    void comDisplayDebugInfo() {
        println("");
        if (codeSize > 0)
            println("code: " + util.hex4(codeAddr) + " - "
                    + util.hex4(codeAddr + codeSize - 1));
        else
            println("code: none.");

        if (workSize > 0)
            println("work: " + util.hex4(workAddr) + " - "
                    + util.hex4(workAddr + workSize - 1));
        else
            println("work: none.");

        println("");
    }

    public String getCommand() {
        print(">");
        return input.readln();
    }

    public String getFilename() {
        FileDialog fd = new FileDialog(frame, "Open MIC File");
        fd.setVisible(true);
        if (fd.getDirectory() != null && fd.getFile() != null)
            return fd.getDirectory() + fd.getFile();
        else
            return null;
    }

    public void print(String s) {
        output.append(s);
    }

    public void println(String s) {
        output.append(s + "\n");
    }

    public String readln() {
        return input.readln();
    }

    public int read() {
        return input.read();
    }

    public void keyEchoOff() {
        input.echoOff();
    }

    public void keyEchoOn() {
        input.echoOn();
    }

}
