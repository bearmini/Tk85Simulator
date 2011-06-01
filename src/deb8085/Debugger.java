package deb8085;

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

import deb8085.instr.Instruction8085;
import deb8085.io.KeyboardInputStream;
import deb8085.io.MouseKiller;

//***************************************************************************************************
//***************************************************************************************************
/* スfスoスbスKスNスススX */
public class Debugger extends Thread {
    DebuggerParent parent;

    private boolean shouldExit = false;

    Frame frame;
    TextArea output;

    CPU8085 cpu = null; // CPU
    Mem8085 mem = null; // スススススス

    int codeAddr = 0; // スRス[スhスフ茨ソスフ開スnスAスhスススX
    int codeSize = 0; // スRス[スhスフ茨ソスフサスCスY

    int workAddr = 0; // ススニ領茨ソスフ開スnスAスhスススX
    int workSize = 0; // ススニ領茨ソスフサスCスY

    // スpスuスススbスNスススxスス
    public PublicLabelList publicLabels;

    // スuススス[スNス|スCスススg
    public BreakPointList breakPoints;

    public KeyboardInputStream input;
    public MouseKiller mouseKiller;

    // ***************************************************************************************************
    // スRスススXスgスススNス^
    public Debugger(DebuggerParent parent, Frame frame, TextArea output) {
        this.parent = parent;
        this.frame = frame;
        this.output = output;

        publicLabels = new PublicLabelList();
        breakPoints = new BreakPointList();

        // CPUスEスススススススフ茨ソスススャ
        mem = new Mem8085();
        cpu = new CPU8085(mem, publicLabels, breakPoints);

        // スRススス\ス[スススススススヘゑソススツ能スノゑソスス驕包ソスRススス\ス[スススノ対ゑソススス}スEスXススススウ鯉ソスノゑソススス
        input = new KeyboardInputStream(output);
        mouseKiller = new MouseKiller(output);

        output.requestFocus();
    }

    public void requestStop() {
        this.shouldExit = true;
    }

    // ***************************************************************************************************
    // スfスoスbスOスススス
    public void run() {
        println("");

        do {
            // ススス゜ゑソススヘゑソススAスススs
            dispatch(getCommand());
        } while (!shouldExit);

    }

    // ***************************************************************************************************
    // スfスoスbスOスRス}スススhスフデスBスXスpスbス`
    public void dispatch(String command) {
        char kind = '\0';
        String param = "";

        // スRス}スススhスフ趣ソスス(ス謫ェスフ一文スススナ、Aス`Z)スニ、スススフパススススス[ス^スノ包ソススススス
        try {
            kind = command.charAt(0);
            param = command.substring(1);
        } catch (StringIndexOutOfBoundsException e) {
        }

        // ススス゜の趣ソズにゑソスススト難ソスススェ暦ソス
        switch (kind) {
        // スAスZスススuスス
        case 'A':
        case 'a': {
            StringTokenizer st = new StringTokenizer(param, ",");
            int startAddr;
            // スJスnスAスhスススXススス謫セ
            if (st.hasMoreTokens())
                startAddr = util.unhex(st.nextToken());
            else
                startAddr = cpu.reg.getReg(Reg8085.PC);

            comAssemble(startAddr);

            break;
        }

            // スuススス[スNス|スCスススgスフ設定・会ソススススススス\スス
        case 'B':
        case 'b': {
            StringTokenizer st = new StringTokenizer(param, ",");
            int addr;
            int count;

            int paramCount = st.countTokens();

            // スpススススス[ス^スススススススネゑソススススススス ススス\スス
            if (paramCount == 0) {
                comDisplayBreakPointInfo();
                break;
            }

            // スpススススス[ス^ススススツゑソススススネゑソス スススス
            else if (paramCount == 1) {
                addr = util.unhex(st.nextToken());
                comResetBreakPoint(addr);
            }

            // スpススススス[ス^ススススツなゑソス スン抵ソス
            else if (paramCount == 2) {
                // スAスhスススXスニソススxススススススス謫セ
                addr = util.unhex(st.nextToken());
                count = util.unhex(st.nextToken());
                comSetBreakPoint(addr, count);
            }

            else {
                println("スpススススス[ス^スススススススススワゑソス.");
                println("");
            }

            break;
        }

            //
        case 'C':
        case 'c':
            break;

        // スススススススフ茨ソスフ表スス
        case 'D':
        case 'd': {
            StringTokenizer st = new StringTokenizer(param, ",");
            int startAddr, endAddr;
            // スJスnスAスhスススXススス謫セ
            if (st.hasMoreTokens())
                startAddr = util.unhex(st.nextToken());
            else
                startAddr = cpu.reg.getReg(Reg8085.PC);

            // スIスススAスhスススXススス謫セ
            if (st.hasMoreTokens())
                endAddr = util.unhex(st.nextToken());
            else
                endAddr = startAddr + 127;

            comDumpMemoryArea(startAddr, endAddr);
            break;
        }

            // スススススススフ茨ソスフ編集
        case 'E':
        case 'e': {
            StringTokenizer st = new StringTokenizer(param, ",");
            int startAddr;
            // スJスnスAスhスススXススス謫セ
            if (st.hasMoreTokens())
                startAddr = util.unhex(st.nextToken());
            else
                startAddr = cpu.reg.getReg(Reg8085.PC);

            comEditMemoryArea(startAddr);
            break;
        }

            // スススススススフ茨ソスフフスBスス
        case 'F':
        case 'f': {
            StringTokenizer st = new StringTokenizer(param, ",");
            int startAddr, endAddr;
            short value;

            // スJスnスAスhスススXススス謫セ
            if (st.hasMoreTokens())
                startAddr = util.unhex(st.nextToken());
            else
                startAddr = cpu.reg.getReg(Reg8085.PC);

            // スIスススAスhスススXススス謫セ
            if (st.hasMoreTokens())
                endAddr = util.unhex(st.nextToken());
            else
                endAddr = startAddr + 127;

            // スtスBススススススlススス謫セ
            if (st.hasMoreTokens())
                value = (short) util.unhex(st.nextToken());
            else
                value = 0;

            comFillMemoryArea(startAddr, endAddr, value);
            break;
        }

            // スvスススOスススススフ趣ソススs
        case 'G':
        case 'g': {
            StringTokenizer st = new StringTokenizer(param, ",");
            int startAddr, endAddr;

            // スJスnスAスhスススXススス謫セ
            if (st.hasMoreTokens())
                startAddr = util.unhex(st.nextToken());
            else
                startAddr = cpu.reg.getReg(Reg8085.PC);

            // スIスススAスhスススXススス謫セ
            if (st.hasMoreTokens())
                endAddr = util.unhex(st.nextToken());
            else
                endAddr = 0xFFFF;

            // スススs
            comRunProgram(startAddr, endAddr);

            break;
        }

            // スwスススvスス\スス
        case 'H':
        case 'h':
        case '?': {
            break;
        }

            // ススス闕橸ソスンのシス~ススススス[スg
        case 'I':
        case 'i': {
            break;
        }

            // スRス}スススhスtス@スCスススフ読み搾ソススス
        case 'J':
        case 'j': {
            break;
        }

            //
        case 'K':
        case 'k': {
            break;
        }

            // MIC スtス@スCススススススス[スh
        case 'L':
        case 'l': {
            // L スRス}スススhスフオスyスススススhスス ススス[スhススススtス@スCスススス
            String filename = param;

            // スtス@スCスススススススwス閧ウスストゑソススネゑソスススス
            if (filename.trim().equals("")) {
                // ス_スCスAスススOスススJススストフス@スCススススススススス゜ゑソス
                filename = getFilename();
                // ス_スCスAスススOスナキスススススZススススス黷ススス
                if (filename == null)
                    break;
            }

            try {
                // .MIC スtス@スCススススヌみ搾ソススス
                comLoadMicFile(filename);

                // スvスススOスススススJスEススス^スススZスbスg
                cpu.reg.setReg(Reg8085.PC, codeAddr);
            } catch (IOException e) {
                println("スtス@スCスススフ読み搾ソススンに趣ソススsスススワゑソススススB");
            }
            break;
        }

            // スススススススフ茨ソスフ移難ソス
        case 'M':
        case 'm': {
            StringTokenizer st = new StringTokenizer(param, ",");
            int startAddr, endAddr, destAddr;

            // スRスsス[スJスnスAスhスススXススス謫セ
            if (st.hasMoreTokens())
                startAddr = util.unhex(st.nextToken());
            else
                startAddr = cpu.reg.getReg(Reg8085.PC);

            // スIスススAスhスススXススス謫セ
            if (st.hasMoreTokens())
                endAddr = util.unhex(st.nextToken());
            else
                endAddr = startAddr + 127;

            // スRスsス[ススAスhスススXススス謫セ
            if (st.hasMoreTokens())
                destAddr = util.unhex(st.nextToken());
            else
                destAddr = endAddr + 1;

            comMoveMemoryArea(startAddr, endAddr, destAddr);
            break;
        }

            // スススxスススススフ設抵ソスEスススススEススス\スス
        case 'N':
        case 'n': {
            StringTokenizer st = new StringTokenizer(param, ",");
            String labelName;
            int addr;

            int paramCount = st.countTokens();

            // スpススススス[ス^スススススススネゑソススススススス ススス\スス
            if (paramCount == 0) {
                comDisplayPublicLabelInfo();
                break;
            }

            // スpススススス[ス^ススススツゑソススススネゑソス スススス
            else if (paramCount == 1) {
                labelName = st.nextToken();
                comResetPublicLabel(labelName);
            }

            // スpススススス[ス^ススススツなゑソス スン抵ソス
            else if (paramCount == 2) {
                // スAスhスススXスニソススxススススススス謫セ
                addr = util.unhex(st.nextToken());
                labelName = st.nextToken();
                comSetPublicLabel(labelName, addr);
            }

            else {
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

            // スススススススフ茨ソスフプスススeスNスg スン定・会ソススススEススス\スス
        case 'P':
        case 'p': {
            StringTokenizer st = new StringTokenizer(param, ",");
            int startAddr, endAddr;
            boolean resetProtect = false;

            // スvスススeスNスgスJスnスAスhスススXススス謫セ
            if (st.hasMoreTokens())
                startAddr = util.unhex(st.nextToken());
            else
                startAddr = cpu.reg.getReg(Reg8085.PC);

            // スIスススAスhスススXススス謫セ
            if (st.hasMoreTokens())
                endAddr = util.unhex(st.nextToken());
            else
                endAddr = startAddr; // スPスoスCスgスススス

            // スン定かススススススススス謫セ
            if (st.hasMoreTokens()) {
                String str = st.nextToken();
                resetProtect = (str.equals("R") || str.equals("r"));
            }

            comProtectMemoryArea(startAddr, endAddr, resetProtect);
            break;
        }

            // スfスoスbスKスフ終スス
        case 'Q':
        case 'q': {
            parent.onEndDebug();
            shouldExit = true;
            break;
        }

            // スススWスXス^スフ表スススAスメ集
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

            // スvスススOスススススフトススス[スX
        case 'T':
        case 't': {
            StringTokenizer st = new StringTokenizer(param, ",");
            int startAddr, endAddr;
            boolean onestep;

            // スJスnスAスhスススXススス謫セ
            if (st.hasMoreTokens())
                startAddr = util.unhex(st.nextToken());
            else
                startAddr = cpu.reg.getReg(Reg8085.PC);

            // スIスススAスhスススXススス謫セ
            if (st.hasMoreTokens())
                endAddr = util.unhex(st.nextToken());
            else
                endAddr = 0xFFFF;

            // 1スXスeスbスvスuススス[スNスススH
            if (st.hasMoreTokens())
                onestep = true;
            else
                onestep = false;

            comTraceProgram(startAddr, endAddr, onestep);
            break;
        }

            // スtスAスZスススuスス
        case 'U':
        case 'u': {
            StringTokenizer st = new StringTokenizer(param, ",");
            int startAddr, endAddr;

            // スJスnスAスhスススXススス謫セ
            if (st.hasMoreTokens())
                startAddr = util.unhex(st.nextToken());
            else
                startAddr = cpu.reg.getReg(Reg8085.PC);

            // スIスススAスhスススXススス謫セ
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

            // スススススススフ茨ソスフ擾ソススススoスス
        case 'W':
        case 'w': {
            StringTokenizer st = new StringTokenizer(param, ",");
            int startAddr, endAddr;

            // スJスnスAスhスススXススス謫セ
            if (st.hasMoreTokens())
                startAddr = util.unhex(st.nextToken());
            else
                startAddr = cpu.reg.getReg(Reg8085.PC);

            // スIスススAスhスススXススス謫セ
            if (st.hasMoreTokens())
                endAddr = util.unhex(st.nextToken());
            else
                endAddr = startAddr + 127;

            // スZス[スuススススtス@スCスススススセゑソス
            String filename = null;
            if (st.hasMoreTokens())
                filename = st.nextToken();

            // スtス@スCスススススススwス閧ウスストゑソススネゑソスススス
            if (filename == null || filename.trim().equals("")) {
                // ス_スCスAスススOスススJススストフス@スCススススススススス゜ゑソス
                filename = getFilename();
                // ス_スCスAスススOスナキスススススZススススス黷ススス
                if (filename == null)
                    break;
                // スススススススOスフフス@スCスススススススナに托ソススンゑソスストゑソススススス

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

            // スgススス[スXスススフススeスbスvスIス[スoス[スフ茨ソスス スン定・会ソススススススス\スス
        case 'Y':
        case 'y': {
            break;
        }

            // ススス\スス
        case 'Z':
        case 'z': {
            comDisplayDebugInfo();
            break;
        }

        }

    }

    // ***************************************************************************************************
    // ***************************************************************************************************
    // ス\スススp スト用ススス\スbスhスQ

    // スAスhスススXスス\スス
    void printAddr(int addr) {
        print(util.hex4(addr));
    }

    // スsスフ先頭スニゑソスストアスhスススXスス\スス
    void printAddrAsLineHeader(int addr) {
        println("");
        printAddr(addr);
        print(" ");
    }

    // ススス゜コス[スhスカ趣ソスススノゑソススト返ゑソス
    String sprintOpecode(Instruction8085 inst) {
        StringBuffer sb = new StringBuffer();

        // ススス゜コス[スhスス1スoスCスgス゜ゑソス\スス
        sb.append(util.hex2(inst.getOpecode()) + " ");

        // ススス゜サスCスYスノ会ソススススス 2スoスCスgス゜、3スoスCスgス゜ゑソス\スス
        if (inst.getSize() == 1)
            sb.append(util.space(8));
        else if (inst.getSize() == 2)
            sb.append(util.hex2(inst.getB2()) + util.space(6));
        else if (inst.getSize() == 3)
            sb.append(util.hex2(inst.getB2()) + " " + util.hex2(inst.getB3())
                    + util.space(3));

        return sb.toString();
    }

    // スススWスXス^ス\スススフヘスbス_
    void printRegistersHeader() {
        println(" A  B  C  D  E  H  L  SP   PC  Z C S P AC");
    }

    // スススWスXス^スフ値スス\スス
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

    // スススススススススeス\スススフとゑソススフヘスbス_
    void printMemoryHeader() {
        println("Addr  0  1  2  3  4  5  6  7  8  9  A  B  C  D  E  F  0123456789ABCDEF");
    }

    // スススススススススeススススsス\スス スAスhスススXス{ス_スススvス{スAスXスLス[
    void printMemoryAreaOneLine(int startAddr, int endAddr) {
        StringBuffer sbDump = new StringBuffer();
        StringBuffer sbHead = new StringBuffer();
        StringBuffer sbAscii = new StringBuffer();

        // スwスbス_スニゑソスストのアスhスススXスニ、スススススススフ難ソススeスノゑソススヌり着スススワでの具ソスススロの包ソススス
        sbHead.append(util.hex4(startAddr % 0xFFF0) + " ");
        sbAscii.append('*');
        for (int addr = startAddr & 0xFFF0; addr < startAddr; addr++)
            sbDump.append(util.space(3));

        // ススススススス_スススvスス\スス
        for (int addr = startAddr; addr <= endAddr; addr++) {
            // スススススススフ難ソススeスニ対会ソスススススAスXスLス[スススススス\スス
            char c = (char) mem.getValue(addr);
            sbDump.append(util.hex2(c));
            sbAscii.append(util.makeValidChar(c));
            if (addr % 0x10 != 7)
                sbDump.append(" ");
            else
                sbDump.append("-");
        }

        // 16スoスCスgスノ達ススストゑソススネゑソススススス竭ォ
        for (int addr = endAddr + 1; addr < ((endAddr + 0x10) & 0xFFF0); addr++)
            sbDump.append(util.space(3));

        println("" + sbHead + sbDump + sbAscii);
    }

    // スgススス[スXスススiススス゜のアスhスススXスAススス゜コス[スhスAスjス[スススjスbスNスjスス\スス
    void printTraceInfo(Instruction8085 inst) {
        StringBuffer sbAddr = new StringBuffer();
        StringBuffer sbInst = new StringBuffer();

        // ススス゜のアスhスススX
        sbAddr.append(util.hex4(cpu.reg.getReg(Reg8085.PC)));

        // ススス゜コス[スhスAスjス[スススjスbスN
        sbInst.append(sprintOpecode(inst) + inst.toString());
        int len = sbAddr.length() + sbInst.length();
        if (len < 23)
            sbInst.append("\t\t");
        else
            sbInst.append("\t");

        print("" + sbAddr + " " + sbInst);
    }

    // ***************************************************************************************************
    // ***************************************************************************************************
    // スRス}スススhススススススス\スbスhスQ

    // ***************************************************************************************************
    // A スRス}スススh
    // ***************************************************************************************************
    // スネ易アスZスススuスス
    void comAssemble(int startAddr) {
        int addr = startAddr;
        String s;
        int com_bytes;

        SimpleAssembler asm = new SimpleAssembler(cpu);

        do {
            // スvスススススvスg addr:
            print(util.hex4(addr) + ": ");
            s = readln();

            if (s.trim().equals(""))
                break;

            // ススsスAスZスススuスス スマ奇ソスススス黷ススoスCスgスススセゑソス
            try {
                com_bytes = asm.assemble(addr, s);
            } catch (OnEncodeException e) {
                println(e.message);
                continue;
            }

            // スAスhスススXススiス゜ゑソス
            addr += com_bytes;

        } while (true);

        println("");
    }

    // ***************************************************************************************************
    // B スRス}スススh
    // ***************************************************************************************************
    // スuススス[スNス|スCスススgスススス\スス
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

    // ***************************************************************************************************
    // スuススス[スNス|スCスススgスフ設抵ソスEスススス
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

    // ***************************************************************************************************
    // D スRス}スススh
    // ***************************************************************************************************
    // スススススススススeスフ表スス
    void comDumpMemoryArea(int startAddr, int endAddr) {
        // スSスフのヘスbス_
        printMemoryHeader();

        // ススsスナ趣ソスワゑソス鼾
        if ((startAddr & 0xFFF0) == (endAddr & 0xFFF0))
            printMemoryAreaOneLine(startAddr, endAddr);

        // ススsスネ擾ソスノわたスス鼾
        else {
            int addr = startAddr;

            // スナ擾ソススフ行
            printMemoryAreaOneLine(startAddr, (startAddr & 0xFFF0) + 0x0f);

            // スススヤの行スAスネゑソスス鼾ソススススススB
            for (addr = (startAddr + 0x10) & 0xFFF0; addr <= (endAddr - 0x10); addr += 0x10)
                printMemoryAreaOneLine(addr, addr + 0x0f);

            // スナ鯉ソスフ行
            if ((addr & 0xFFF0) == (endAddr & 0xFFF0))
                printMemoryAreaOneLine(addr, endAddr);
        }
        println("");
    }

    // ***************************************************************************************************
    // E スRス}スススh
    // ***************************************************************************************************
    // スススススススフ茨ソスフ編集
    void comEditMemoryArea(int startAddr) {
        // スSスフのヘスbス_
        printMemoryHeader();

        int addr = startAddr;
        int blankaddr = startAddr & 0x0F;

        // ス\ススススススヘソスス[スv
        while (true) {
            // スススススススススeスス\スス
            printMemoryAreaOneLine(addr, (addr & 0xFFF0) + 0x0F);

            // ススヘヘスbス_
            print("     " + util.space(blankaddr * 3));

            // ススス
            String s = readln();

            // ススヘゑソススネゑソスススホ編集スススIスス
            if (s.trim().equals(""))
                break;

            // ススヘゑソスススス
            try {
                for (int i = 0; i < 0x10; i++)
                    mem.setValue(
                            addr + i,
                            !(s.substring(3 * i, 3 * i + 2).equals("  ")) ? util
                                    .unhex(s.substring(3 * i, 3 * i + 2)) : mem
                                    .getValue(addr + i));
            } catch (StringIndexOutOfBoundsException e) {
            }

            // スススノ進スス
            addr = (addr & 0xFFF0) + 0x10;
            blankaddr = 0;
        }

    }

    // ***************************************************************************************************
    // F スRス}スススh
    // ***************************************************************************************************
    // スススススススフ茨ソスフフスBスス
    void comFillMemoryArea(int startAddr, int endAddr, short value) {
        for (int addr = startAddr; addr <= endAddr; addr++)
            mem.setValue(addr, value);
        println("");
    }

    // ***************************************************************************************************
    // G スRス}スススh
    // ***************************************************************************************************
    // スススs
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

    // ***************************************************************************************************
    // L スRス}スススh
    // ***************************************************************************************************
    // MICスtス@スCススススヌみ搾ソススス
    public void comLoadMicFile(String filename) throws IOException {
        // スtス@スCスススススIス[スvスス
        DataInputStream d = new DataInputStream(new BufferedInputStream(
                new FileInputStream(filename)));

        // スtス@スCスス ID スフチスFスbスN
        byte id1 = d.readByte();
        byte id2 = d.readByte();
        if (!(id1 == 80/* 'P' */&& id2 == 72/* 'H' */)) {
            throw new IOException();
        }

        // スRス[スhスフ茨ソスEススニ領茨ソスfス[ス^スフ取得
        codeAddr = d.readUnsignedShort();
        codeSize = d.readUnsignedShort();
        workSize = d.readUnsignedShort();
        workAddr = d.readUnsignedShort();

        codeAddr = util.swapEndian(codeAddr);
        codeSize = util.swapEndian(codeSize);
        workAddr = util.swapEndian(workAddr);
        workSize = util.swapEndian(workSize);

        // スRス[スhススス謫セスススAスススススススス スノ格ス[
        for (int addr = codeAddr; addr < codeAddr + codeSize; addr++)
            mem.setValue(addr, d.readUnsignedByte());

        // スtス@スCスス ID スフチスFスbスN
        byte id3 = d.readByte();
        byte id4 = d.readByte();
        if (!(id3 == 80/* 'P' */&& id4 == 66/* 'B' */)) {
            throw new IOException();
        }

        // PUBLIC スススxスススフデス[ス^ススス謫セ
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
        // スススス\スス
        comDisplayDebugInfo();
        comDisplayPublicLabelInfo();
    }

    // ***************************************************************************************************
    // M スRス}スススh
    // ***************************************************************************************************
    // スススススススフ茨ソスフ移難ソス
    void comMoveMemoryArea(int startAddr, int endAddr, int destAddr) {
        for (int addr = 0; addr <= endAddr - startAddr; addr++)
            mem.setValue(destAddr + addr, mem.getValue(startAddr + addr));
        println("");
    }

    // ***************************************************************************************************
    // N スRス}スススh
    // ***************************************************************************************************
    // スpスuスススbスNスススxスススススス\スス
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

    // ***************************************************************************************************
    // スpスuスススbスNスススxスススフ設抵ソスEスススス
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

    // ***************************************************************************************************
    // P スRス}スススh
    // ***************************************************************************************************
    // スススススススフ茨ソスフプスススeスNスgスフ設抵ソスEスススス
    void comProtectMemoryArea(int startAddr, int endAddr, boolean resetProtect) {
        for (int addr = startAddr; addr <= endAddr; addr++)
            mem.setReadOnly(addr, !resetProtect);
        println("");
    }

    // ***************************************************************************************************
    // R スRス}スススh
    // ***************************************************************************************************
    // スススWスXス^スフ保趣ソスススストゑソスススlスス\スス
    void comDumpRegister() {
        printRegistersHeader();
        printRegisters();
        println("");
        println("");
    }

    // ***************************************************************************************************
    // スススWスXス^スフ保趣ソスススストゑソスススlススメ集
    void comEditRegister() {
        // スススWスXス^スlスス\スス
        printRegistersHeader();
        printRegisters();
        println("");

        // ススsスヌみ搾ソスススナ、スススス
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

    // ***************************************************************************************************
    // T スRス}スススh
    // ***************************************************************************************************
    // スgススス[スX
    void comTraceProgram(int startAddr, int endAddr, boolean traceOneStep) {
        String message = null;
        Instruction8085 inst;

        keyEchoOff();
        cpu.restart();

        try {
            cpu.reg.setReg(Reg8085.PC, startAddr);

            // スwスbス_スス\スス
            println("Addr Code       Mnemonic         A  B  C  D  E  H  L  SP   PC  Z C S P AC");

            // スススsススス[スv
            while (cpu.reg.getReg(Reg8085.PC) <= endAddr) {
                // ススス゜ゑソスス謫セ
                inst = cpu.decode(cpu.fetch());

                // スススsスOスフ擾ソスヤゑソス\スス
                printTraceInfo(inst);

                // ススス゜の趣ソススsスメゑソス
                if (traceOneStep) {
                    int c = read();
                    if (c == 'Q' || c == 'q')
                        break;
                }
                // ススス゜ゑソススススs
                try {
                    cpu.execute(inst);
                } catch (OnBreakPointException e) {
                    message = e.message;
                }

                // スススsスススハのソススWスXス^スス\スス
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

    // ***************************************************************************************************
    // U スRス}スススh
    // ***************************************************************************************************
    // スtスAスZスススuスス
    void comDisassemble(int startAddr, int endAddr) {

        int orgPC = cpu.reg.getReg(Reg8085.PC);
        int newPC;
        Instruction8085 inst;

        cpu.reg.setReg(Reg8085.PC, startAddr);
        while (cpu.reg.getReg(Reg8085.PC) <= endAddr) {
            inst = cpu.decode(cpu.fetch());

            // スAスhスススXスス\スススススAススス゜コス[スhスAスIスyスススススhスAスjス[スススjスbスNスフ表スス
            printTraceInfo(inst);
            println("");

            // スjス[スススjスbスNスス HLT ススススススススIスス
            if (inst.getMnemonic().equals("HLT"))
                break;

            // スススフ厄ソスス゜ゑソス
            newPC = cpu.reg.getReg(Reg8085.PC) + inst.getSize();
            cpu.reg.setReg(Reg8085.PC, newPC);
        }

        // PC スlスススススノ戻ゑソス
        cpu.reg.setReg(Reg8085.PC, orgPC);

        println("");
    }

    // ***************************************************************************************************
    // W スRス}スススh
    // ***************************************************************************************************
    // スススススススススeスススtス@スCスススノ擾ソススススoスス
    void comWriteToFile(int startaddr, int endaddr, String filename)
            throws IOException {
        // スtス@スCスススススIス[スvスス
        DataOutputStream d = new DataOutputStream(new BufferedOutputStream(
                new FileOutputStream(filename)));

        // スtス@スCスス ID スフ擾ソススススススス
        d.writeByte(80/* P */);
        d.writeByte(72/* H */);

        // スRス[スhスフ茨ソスEススニ領茨ソスfス[ス^スフ擾ソススススススス
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

        // スRス[スhススス謫セスススAスtス@スCスススノ格ス[
        for (int addr = temp_codeAddr; addr < temp_codeAddr + temp_codeSize; addr++)
            d.writeByte((byte) mem.getValue(addr));

        // スtス@スCスス ID スフ擾ソススススススス
        d.writeByte(80/* P */);
        d.writeByte(66/* B */);

        // PUBLIC スススxスススフデス[ス^スススススススススス
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

    // ***************************************************************************************************
    // Z スRス}スススh
    // ***************************************************************************************************
    // スfスoスbスOスススス\スス
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

    // ***************************************************************************************************
    // ***************************************************************************************************
    // スススフ托ソス ススススススススス\スbスhスQ

    // ***************************************************************************************************
    // スRス}スススhススス謫セ
    public String getCommand() {
        print(">");
        return input.readln();
    }

    // ***************************************************************************************************
    // スtス@スCスススススセゑソス FileDialog スススgスス
    public String getFilename() {
        FileDialog fd = new FileDialog(frame, "Open MIC File");
        fd.setVisible(true);
        if (fd.getDirectory() != null && fd.getFile() != null)
            return fd.getDirectory() + fd.getFile();
        else
            return null;
    }

    // ***************************************************************************************************
    // スRススス\ス[スススノ包ソスススススス\スス
    public void print(String s) {
        output.append(s);
    }

    public void println(String s) {
        output.append(s + "\n");
    }

    // ***************************************************************************************************
    // スRススス\ス[ススススス逡カスススススス謫セ
    public String readln() {
        return input.readln();
    }

    // ***************************************************************************************************
    // スRススス\ス[ススススススPススススススス謫セ
    public int read() {
        return input.read();
    }

    // ***************************************************************************************************
    // スLス[スGスRス[スススス
    public void keyEchoOff() {
        input.echoOff();
    }

    // ***************************************************************************************************
    // スLス[スGスRス[スLスス
    public void keyEchoOn() {
        input.echoOn();
    }

}
