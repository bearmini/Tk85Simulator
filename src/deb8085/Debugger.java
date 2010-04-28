package deb8085;

import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.TextArea;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.StringTokenizer;
import deb8085.io.*;
import deb8085.instr.*;

//***************************************************************************************************
//***************************************************************************************************
/* �f�o�b�K�N���X */
public class Debugger extends Thread {
	DebuggerParent parent;

	private boolean shouldExit = false;

	Frame frame;
	TextArea output;

	CPU8085 cpu = null; // CPU
	Mem8085 mem = null; // ������

	int codeAddr = 0; // �R�[�h�̈�̊J�n�A�h���X
	int codeSize = 0; // �R�[�h�̈�̃T�C�Y

	int workAddr = 0; // ��Ɨ̈�̊J�n�A�h���X
	int workSize = 0; // ��Ɨ̈�̃T�C�Y

	// �p�u���b�N���x��
	public PublicLabelList publicLabels;

	// �u���[�N�|�C���g
	public BreakPointList breakPoints;

	public KeyboardInputStream input;
	public MouseKiller mouseKiller;

	// ***************************************************************************************************
	// �R���X�g���N�^
	public Debugger(DebuggerParent parent, Frame frame, TextArea output) {
		this.parent = parent;
		this.frame = frame;
		this.output = output;

		publicLabels = new PublicLabelList();
		breakPoints = new BreakPointList();

		// CPU�E�������̈���쐬
		mem = new Mem8085();
		cpu = new CPU8085(mem, publicLabels, breakPoints);

		// �R���\�[��������͂��\�ɂ��違�R���\�[���ɑ΂���}�E�X����𖳌��ɂ���
		input = new KeyboardInputStream(output);
		mouseKiller = new MouseKiller(output);

		output.requestFocus();
	}

	public void requestStop() {
		this.shouldExit = true;
	}

	// ***************************************************************************************************
	// �f�o�b�O����
	public void run() {
		println("");

		do {
			// ���߂���͂��A���s
			dispatch(getCommand());
		} while (!shouldExit);

	}

	// ***************************************************************************************************
	// �f�o�b�O�R�}���h�̃f�B�X�p�b�`
	public void dispatch(String command) {
		char kind = '\0';
		String param = "";

		// �R�}���h�̎��(�擪�̈ꕶ���ŁAA�`Z)�ƁA���̃p�����[�^�ɕ�����
		try {
			kind = command.charAt(0);
			param = command.substring(1);
		} catch (StringIndexOutOfBoundsException e) {
		}

		// ���߂̎�ނɂ���ē���𕪗�
		switch (kind) {
		// �A�Z���u��
		case 'A':
		case 'a': {
			StringTokenizer st = new StringTokenizer(param, ",");
			int startAddr;
			// �J�n�A�h���X���擾
			if (st.hasMoreTokens())
				startAddr = util.unhex(st.nextToken());
			else
				startAddr = cpu.reg.getReg(Reg8085.PC);

			comAssemble(startAddr);

			break;
		}

			// �u���[�N�|�C���g�̐ݒ襉�������\��
		case 'B':
		case 'b': {
			StringTokenizer st = new StringTokenizer(param, ",");
			int addr;
			int count;

			int paramCount = st.countTokens();

			// �p�����[�^�������Ȃ������� ���\��
			if (paramCount == 0) {
				comDisplayBreakPointInfo();
				break;
			}

			// �p�����[�^��������Ȃ� ����
			else if (paramCount == 1) {
				addr = util.unhex(st.nextToken());
				comResetBreakPoint(addr);
			}

			// �p�����[�^����Ȃ� �ݒ�
			else if (paramCount == 2) {
				// �A�h���X�ƃ��x�������擾
				addr = util.unhex(st.nextToken());
				count = util.unhex(st.nextToken());
				comSetBreakPoint(addr, count);
			}

			else {
				println("�p�����[�^���������܂�.");
				println("");
			}

			break;
		}

			//
		case 'C':
		case 'c':
			break;

		// �������̈�̕\��
		case 'D':
		case 'd': {
			StringTokenizer st = new StringTokenizer(param, ",");
			int startAddr, endAddr;
			// �J�n�A�h���X���擾
			if (st.hasMoreTokens())
				startAddr = util.unhex(st.nextToken());
			else
				startAddr = cpu.reg.getReg(Reg8085.PC);

			// �I���A�h���X���擾
			if (st.hasMoreTokens())
				endAddr = util.unhex(st.nextToken());
			else
				endAddr = startAddr + 127;

			comDumpMemoryArea(startAddr, endAddr);
			break;
		}

			// �������̈�̕ҏW
		case 'E':
		case 'e': {
			StringTokenizer st = new StringTokenizer(param, ",");
			int startAddr;
			// �J�n�A�h���X���擾
			if (st.hasMoreTokens())
				startAddr = util.unhex(st.nextToken());
			else
				startAddr = cpu.reg.getReg(Reg8085.PC);

			comEditMemoryArea(startAddr);
			break;
		}

			// �������̈�̃t�B��
		case 'F':
		case 'f': {
			StringTokenizer st = new StringTokenizer(param, ",");
			int startAddr, endAddr;
			short value;

			// �J�n�A�h���X���擾
			if (st.hasMoreTokens())
				startAddr = util.unhex(st.nextToken());
			else
				startAddr = cpu.reg.getReg(Reg8085.PC);

			// �I���A�h���X���擾
			if (st.hasMoreTokens())
				endAddr = util.unhex(st.nextToken());
			else
				endAddr = startAddr + 127;

			// �t�B������l���擾
			if (st.hasMoreTokens())
				value = (short) util.unhex(st.nextToken());
			else
				value = 0;

			comFillMemoryArea(startAddr, endAddr, value);
			break;
		}

			// �v���O�����̎��s
		case 'G':
		case 'g': {
			StringTokenizer st = new StringTokenizer(param, ",");
			int startAddr, endAddr;

			// �J�n�A�h���X���擾
			if (st.hasMoreTokens())
				startAddr = util.unhex(st.nextToken());
			else
				startAddr = cpu.reg.getReg(Reg8085.PC);

			// �I���A�h���X���擾
			if (st.hasMoreTokens())
				endAddr = util.unhex(st.nextToken());
			else
				endAddr = 0xFFFF;

			// ���s
			comRunProgram(startAddr, endAddr);

			break;
		}

			// �w���v��\��
		case 'H':
		case 'h':
		case '?': {
			break;
		}

			// ���荞�݂̃V�~�����[�g
		case 'I':
		case 'i': {
			break;
		}

			// �R�}���h�t�@�C���̓ǂݍ���
		case 'J':
		case 'j': {
			break;
		}

			//
		case 'K':
		case 'k': {
			break;
		}

			// MIC �t�@�C�������[�h
		case 'L':
		case 'l': {
			// L �R�}���h�̃I�y�����h�� ���[�h����t�@�C����
			String filename = param;

			// �t�@�C�������w�肳��Ă��Ȃ����
			if (filename.trim().equals("")) {
				// �_�C�A���O���J���ăt�@�C���������߂�
				filename = getFilename();
				// �_�C�A���O�ŃL�����Z�����ꂽ��
				if (filename == null)
					break;
			}

			try {
				// .MIC �t�@�C����ǂݍ���
				comLoadMicFile(filename);

				// �v���O�����J�E���^���Z�b�g
				cpu.reg.setReg(Reg8085.PC, codeAddr);
			} catch (IOException e) {
				println("�t�@�C���̓ǂݍ��݂Ɏ��s���܂����B");
			}
			break;
		}

			// �������̈�̈ړ�
		case 'M':
		case 'm': {
			StringTokenizer st = new StringTokenizer(param, ",");
			int startAddr, endAddr, destAddr;

			// �R�s�[�J�n�A�h���X���擾
			if (st.hasMoreTokens())
				startAddr = util.unhex(st.nextToken());
			else
				startAddr = cpu.reg.getReg(Reg8085.PC);

			// �I���A�h���X���擾
			if (st.hasMoreTokens())
				endAddr = util.unhex(st.nextToken());
			else
				endAddr = startAddr + 127;

			// �R�s�[��A�h���X���擾
			if (st.hasMoreTokens())
				destAddr = util.unhex(st.nextToken());
			else
				destAddr = endAddr + 1;

			comMoveMemoryArea(startAddr, endAddr, destAddr);
			break;
		}

			// ���x�����̐ݒ�E�����E���\��
		case 'N':
		case 'n': {
			StringTokenizer st = new StringTokenizer(param, ",");
			String labelName;
			int addr;

			int paramCount = st.countTokens();

			// �p�����[�^�������Ȃ������� ���\��
			if (paramCount == 0) {
				comDisplayPublicLabelInfo();
				break;
			}

			// �p�����[�^��������Ȃ� ����
			else if (paramCount == 1) {
				labelName = st.nextToken();
				comResetPublicLabel(labelName);
			}

			// �p�����[�^����Ȃ� �ݒ�
			else if (paramCount == 2) {
				// �A�h���X�ƃ��x�������擾
				addr = util.unhex(st.nextToken());
				labelName = st.nextToken();
				comSetPublicLabel(labelName, addr);
			}

			else {
				println("�p�����[�^���������܂�.");
				println("");
			}

		}

			//
		case 'O':
		case 'o': {
			break;
		}

			// �������̈�̃v���e�N�g �ݒ襉����E���\��
		case 'P':
		case 'p': {
			StringTokenizer st = new StringTokenizer(param, ",");
			int startAddr, endAddr;
			boolean resetProtect = false;

			// �v���e�N�g�J�n�A�h���X���擾
			if (st.hasMoreTokens())
				startAddr = util.unhex(st.nextToken());
			else
				startAddr = cpu.reg.getReg(Reg8085.PC);

			// �I���A�h���X���擾
			if (st.hasMoreTokens())
				endAddr = util.unhex(st.nextToken());
			else
				endAddr = startAddr; // �P�o�C�g����

			// �ݒ肩���������擾
			if (st.hasMoreTokens()) {
				String str = st.nextToken();
				resetProtect = (str.equals("R") || str.equals("r"));
			}

			comProtectMemoryArea(startAddr, endAddr, resetProtect);
			break;
		}

			// �f�o�b�K�̏I��
		case 'Q':
		case 'q': {
			parent.onEndDebug();
			shouldExit = true;
			break;
		}

			// ���W�X�^�̕\���A�ҏW
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

			// �v���O�����̃g���[�X
		case 'T':
		case 't': {
			StringTokenizer st = new StringTokenizer(param, ",");
			int startAddr, endAddr;
			boolean onestep;

			// �J�n�A�h���X���擾
			if (st.hasMoreTokens())
				startAddr = util.unhex(st.nextToken());
			else
				startAddr = cpu.reg.getReg(Reg8085.PC);

			// �I���A�h���X���擾
			if (st.hasMoreTokens())
				endAddr = util.unhex(st.nextToken());
			else
				endAddr = 0xFFFF;

			// 1�X�e�b�v�u���[�N���H
			if (st.hasMoreTokens())
				onestep = true;
			else
				onestep = false;

			comTraceProgram(startAddr, endAddr, onestep);
			break;
		}

			// �t�A�Z���u��
		case 'U':
		case 'u': {
			StringTokenizer st = new StringTokenizer(param, ",");
			int startAddr, endAddr;

			// �J�n�A�h���X���擾
			if (st.hasMoreTokens())
				startAddr = util.unhex(st.nextToken());
			else
				startAddr = cpu.reg.getReg(Reg8085.PC);

			// �I���A�h���X���擾
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

			// �������̈�̏����o��
		case 'W':
		case 'w': {
			StringTokenizer st = new StringTokenizer(param, ",");
			int startAddr, endAddr;

			// �J�n�A�h���X���擾
			if (st.hasMoreTokens())
				startAddr = util.unhex(st.nextToken());
			else
				startAddr = cpu.reg.getReg(Reg8085.PC);

			// �I���A�h���X���擾
			if (st.hasMoreTokens())
				endAddr = util.unhex(st.nextToken());
			else
				endAddr = startAddr + 127;

			// �Z�[�u����t�@�C�����𓾂�
			String filename = null;
			if (st.hasMoreTokens())
				filename = st.nextToken();

			// �t�@�C�������w�肳��Ă��Ȃ����
			if (filename == null || filename.trim().equals("")) {
				// �_�C�A���O���J���ăt�@�C���������߂�
				filename = getFilename();
				// �_�C�A���O�ŃL�����Z�����ꂽ��
				if (filename == null)
					break;
				// �������O�̃t�@�C�������łɑ��݂��Ă�����

			}

			try {
				comWriteToFile(startAddr, endAddr, filename);
			} catch (IOException e) {
				println("�t�@�C���̏������݂Ɏ��s���܂���.");
			}

			break;
		}

			//
		case 'X':
		case 'x': {
			break;
		}

			// �g���[�X���̃X�e�b�v�I�[�o�[�̈�� �ݒ襉�������\��
		case 'Y':
		case 'y': {
			break;
		}

			// ���\��
		case 'Z':
		case 'z': {
			comDisplayDebugInfo();
			break;
		}

		}

	}

	// ***************************************************************************************************
	// ***************************************************************************************************
	// �\���p �ėp���\�b�h�Q

	// �A�h���X��\��
	void printAddr(int addr) {
		print(util.hex4(addr));
	}

	// �s�̐擪�Ƃ��ăA�h���X��\��
	void printAddrAsLineHeader(int addr) {
		println("");
		printAddr(addr);
		print(" ");
	}

	// ���߃R�[�h�𕶎���ɂ��ĕԂ�
	String sprintOpecode(Instruction8085 inst) {
		StringBuffer sb = new StringBuffer();

		// ���߃R�[�h��1�o�C�g�߂�\��
		sb.append(util.hex2(inst.getOpecode()) + " ");

		// ���߃T�C�Y�ɉ����� 2�o�C�g�߁A3�o�C�g�߂�\��
		if (inst.getSize() == 1)
			sb.append(util.space(8));
		else if (inst.getSize() == 2)
			sb.append(util.hex2(inst.getB2()) + util.space(6));
		else if (inst.getSize() == 3)
			sb.append(util.hex2(inst.getB2()) + " " + util.hex2(inst.getB3())
					+ util.space(3));

		return sb.toString();
	}

	// ���W�X�^�\���̃w�b�_
	void printRegistersHeader() {
		println(" A  B  C  D  E  H  L  SP   PC  Z C S P AC");
	}

	// ���W�X�^�̒l��\��
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

	// ���������e�\���̂Ƃ��̃w�b�_
	void printMemoryHeader() {
		println("Addr  0  1  2  3  4  5  6  7  8  9  A  B  C  D  E  F  0123456789ABCDEF");
	}

	// ���������e����s�\�� �A�h���X�{�_���v�{�A�X�L�[
	void printMemoryAreaOneLine(int startAddr, int endAddr) {
		StringBuffer sbDump = new StringBuffer();
		StringBuffer sbHead = new StringBuffer();
		StringBuffer sbAscii = new StringBuffer();

		// �w�b�_�Ƃ��ẴA�h���X�ƁA�������̓��e�ɂ��ǂ蒅���܂ł̋���ۂ̕���
		sbHead.append(util.hex4(startAddr % 0xFFF0) + " ");
		sbAscii.append('*');
		for (int addr = startAddr & 0xFFF0; addr < startAddr; addr++)
			sbDump.append(util.space(3));

		// �������_���v��\��
		for (int addr = startAddr; addr <= endAddr; addr++) {
			// �������̓��e�ƑΉ�����A�X�L�[������\��
			char c = (char) mem.getValue(addr);
			sbDump.append(util.hex2(c));
			sbAscii.append(util.makeValidChar(c));
			if (addr % 0x10 != 7)
				sbDump.append(" ");
			else
				sbDump.append("-");
		}

		// 16�o�C�g�ɒB���Ă��Ȃ�����⑫
		for (int addr = endAddr + 1; addr < ((endAddr + 0x10) & 0xFFF0); addr++)
			sbDump.append(util.space(3));

		println("" + sbHead + sbDump + sbAscii);
	}

	// �g���[�X���i���߂̃A�h���X�A���߃R�[�h�A�j�[���j�b�N�j��\��
	void printTraceInfo(Instruction8085 inst) {
		StringBuffer sbAddr = new StringBuffer();
		StringBuffer sbInst = new StringBuffer();

		// ���߂̃A�h���X
		sbAddr.append(util.hex4(cpu.reg.getReg(Reg8085.PC)));

		// ���߃R�[�h�A�j�[���j�b�N
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
	// �R�}���h�������\�b�h�Q

	// ***************************************************************************************************
	// A �R�}���h
	// ***************************************************************************************************
	// �ȈՃA�Z���u��
	void comAssemble(int startAddr) {
		int addr = startAddr;
		String s;
		int com_bytes;

		SimpleAssembler asm = new SimpleAssembler(cpu);

		do {
			// �v�����v�g addr:
			print(util.hex4(addr) + ": ");
			s = readln();

			if (s.trim().equals(""))
				break;

			// ��s�A�Z���u�� �ϊ����ꂽ�o�C�g���𓾂�
			try {
				com_bytes = asm.assemble(addr, s);
			} catch (OnEncodeException e) {
				println(e.message);
				continue;
			}

			// �A�h���X��i�߂�
			addr += com_bytes;

		} while (true);

		println("");
	}

	// ***************************************************************************************************
	// B �R�}���h
	// ***************************************************************************************************
	// �u���[�N�|�C���g����\��
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
	// �u���[�N�|�C���g�̐ݒ�E����
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
	// D �R�}���h
	// ***************************************************************************************************
	// ���������e�̕\��
	void comDumpMemoryArea(int startAddr, int endAddr) {
		// �S�̂̃w�b�_
		printMemoryHeader();

		// ��s�Ŏ��܂�ꍇ
		if ((startAddr & 0xFFF0) == (endAddr & 0xFFF0))
			printMemoryAreaOneLine(startAddr, endAddr);

		// ��s�ȏ�ɂ킽��ꍇ
		else {
			int addr = startAddr;

			// �ŏ��̍s
			printMemoryAreaOneLine(startAddr, (startAddr & 0xFFF0) + 0x0f);

			// ���Ԃ̍s�A�Ȃ��ꍇ������B
			for (addr = (startAddr + 0x10) & 0xFFF0; addr <= (endAddr - 0x10); addr += 0x10)
				printMemoryAreaOneLine(addr, addr + 0x0f);

			// �Ō�̍s
			if ((addr & 0xFFF0) == (endAddr & 0xFFF0))
				printMemoryAreaOneLine(addr, endAddr);
		}
		println("");
	}

	// ***************************************************************************************************
	// E �R�}���h
	// ***************************************************************************************************
	// �������̈�̕ҏW
	void comEditMemoryArea(int startAddr) {
		// �S�̂̃w�b�_
		printMemoryHeader();

		int addr = startAddr;
		int blankaddr = startAddr & 0x0F;

		// �\�������̓��[�v
		while (true) {
			// ���������e��\��
			printMemoryAreaOneLine(addr, (addr & 0xFFF0) + 0x0F);

			// ���̓w�b�_
			print("     " + util.space(blankaddr * 3));

			// ����
			String s = readln();

			// ���͂��Ȃ���ΕҏW���I��
			if (s.trim().equals(""))
				break;

			// ���͂����
			try {
				for (int i = 0; i < 0x10; i++)
					mem.setValue(addr + i, !(s.substring(3 * i, 3 * i + 2)
							.equals("  ")) ? util.unhex(s.substring(3 * i,
							3 * i + 2)) : mem.getValue(addr + i));
			} catch (StringIndexOutOfBoundsException e) {
			}

			// ���ɐi��
			addr = (addr & 0xFFF0) + 0x10;
			blankaddr = 0;
		}

	}

	// ***************************************************************************************************
	// F �R�}���h
	// ***************************************************************************************************
	// �������̈�̃t�B��
	void comFillMemoryArea(int startAddr, int endAddr, short value) {
		for (int addr = startAddr; addr <= endAddr; addr++)
			mem.setValue(addr, value);
		println("");
	}

	// ***************************************************************************************************
	// G �R�}���h
	// ***************************************************************************************************
	// ���s
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
	// L �R�}���h
	// ***************************************************************************************************
	// MIC�t�@�C����ǂݍ���
	public void comLoadMicFile(String filename) throws IOException {
		// �t�@�C�����I�[�v��
		DataInputStream d = new DataInputStream(new BufferedInputStream(
				new FileInputStream(filename)));

		// �t�@�C�� ID �̃`�F�b�N
		byte id1 = d.readByte();
		byte id2 = d.readByte();
		if (!(id1 == 80/* 'P' */&& id2 == 72/* 'H' */)) {
			throw new IOException();
		}

		// �R�[�h�̈�E��Ɨ̈�f�[�^�̎擾
		codeAddr = d.readUnsignedShort();
		codeSize = d.readUnsignedShort();
		workSize = d.readUnsignedShort();
		workAddr = d.readUnsignedShort();

		codeAddr = util.swapEndian(codeAddr);
		codeSize = util.swapEndian(codeSize);
		workAddr = util.swapEndian(workAddr);
		workSize = util.swapEndian(workSize);

		// �R�[�h���擾���A�������� �Ɋi�[
		for (int addr = codeAddr; addr < codeAddr + codeSize; addr++)
			mem.setValue(addr, d.readUnsignedByte());

		// �t�@�C�� ID �̃`�F�b�N
		byte id3 = d.readByte();
		byte id4 = d.readByte();
		if (!(id3 == 80/* 'P' */&& id4 == 66/* 'B' */)) {
			throw new IOException();
		}

		// PUBLIC ���x���̃f�[�^���擾
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
		// ����\��
		comDisplayDebugInfo();
		comDisplayPublicLabelInfo();
	}

	// ***************************************************************************************************
	// M �R�}���h
	// ***************************************************************************************************
	// �������̈�̈ړ�
	void comMoveMemoryArea(int startAddr, int endAddr, int destAddr) {
		for (int addr = 0; addr <= endAddr - startAddr; addr++)
			mem.setValue(destAddr + addr, mem.getValue(startAddr + addr));
		println("");
	}

	// ***************************************************************************************************
	// N �R�}���h
	// ***************************************************************************************************
	// �p�u���b�N���x������\��
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
	// �p�u���b�N���x���̐ݒ�E����
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
	// P �R�}���h
	// ***************************************************************************************************
	// �������̈�̃v���e�N�g�̐ݒ�E����
	void comProtectMemoryArea(int startAddr, int endAddr, boolean resetProtect) {
		for (int addr = startAddr; addr <= endAddr; addr++)
			mem.setReadOnly(addr, !resetProtect);
		println("");
	}

	// ***************************************************************************************************
	// R �R�}���h
	// ***************************************************************************************************
	// ���W�X�^�̕ێ����Ă���l��\��
	void comDumpRegister() {
		printRegistersHeader();
		printRegisters();
		println("");
		println("");
	}

	// ***************************************************************************************************
	// ���W�X�^�̕ێ����Ă���l��ҏW
	void comEditRegister() {
		// ���W�X�^�l��\��
		printRegistersHeader();
		printRegisters();
		println("");

		// ��s�ǂݍ���ŁA����
		String s = readln();

		try {
			cpu.reg.setReg(Reg8085.A,
					!(s.substring(0, 2).equals("  ")) ? (short) util.unhex(s
							.substring(0, 2)) : cpu.reg.getReg(Reg8085.A));
			cpu.reg.setReg(Reg8085.B,
					!(s.substring(3, 5).equals("  ")) ? (short) util.unhex(s
							.substring(3, 5)) : cpu.reg.getReg(Reg8085.B));
			cpu.reg.setReg(Reg8085.C,
					!(s.substring(6, 8).equals("  ")) ? (short) util.unhex(s
							.substring(6, 8)) : cpu.reg.getReg(Reg8085.C));
			cpu.reg.setReg(Reg8085.D,
					!(s.substring(9, 11).equals("  ")) ? (short) util.unhex(s
							.substring(9, 11)) : cpu.reg.getReg(Reg8085.D));
			cpu.reg.setReg(Reg8085.E,
					!(s.substring(12, 14).equals("  ")) ? (short) util.unhex(s
							.substring(12, 14)) : cpu.reg.getReg(Reg8085.E));
			cpu.reg.setReg(Reg8085.H,
					!(s.substring(15, 17).equals("  ")) ? (short) util.unhex(s
							.substring(15, 17)) : cpu.reg.getReg(Reg8085.H));
			cpu.reg.setReg(Reg8085.L,
					!(s.substring(18, 20).equals("  ")) ? (short) util.unhex(s
							.substring(18, 20)) : cpu.reg.getReg(Reg8085.L));
			cpu.reg.setReg(Reg8085.SP,
					!(s.substring(21, 25).equals("    ")) ? util.unhex(s
							.substring(21, 25)) : cpu.reg.getReg(Reg8085.SP));
			cpu.reg.setReg(Reg8085.PC,
					!(s.substring(26, 30).equals("    ")) ? util.unhex(s
							.substring(26, 30)) : cpu.reg.getReg(Reg8085.PC));
			cpu.reg.setFlag(Reg8085.Zf, !(s.substring(31, 32).equals(" ")) ? s
					.substring(31, 32).equals("1") : cpu.reg
					.getFlag(Reg8085.Zf));
			cpu.reg.setFlag(Reg8085.Cf, !(s.substring(33, 34).equals(" ")) ? s
					.substring(33, 34).equals("1") : cpu.reg
					.getFlag(Reg8085.Cf));
			cpu.reg.setFlag(Reg8085.Sf, !(s.substring(35, 36).equals(" ")) ? s
					.substring(35, 36).equals("1") : cpu.reg
					.getFlag(Reg8085.Sf));
			cpu.reg.setFlag(Reg8085.Pf, !(s.substring(37, 38).equals(" ")) ? s
					.substring(37, 38).equals("1") : cpu.reg
					.getFlag(Reg8085.Pf));
			cpu.reg.setFlag(Reg8085.ACf, !(s.substring(39, 40).equals(" ")) ? s
					.substring(39, 40).equals("1") : cpu.reg
					.getFlag(Reg8085.ACf));
		} catch (StringIndexOutOfBoundsException e) {
		}

		println("");
	}

	// ***************************************************************************************************
	// T �R�}���h
	// ***************************************************************************************************
	// �g���[�X
	void comTraceProgram(int startAddr, int endAddr, boolean traceOneStep) {
		String message = null;
		Instruction8085 inst;

		keyEchoOff();
		cpu.restart();

		try {
			cpu.reg.setReg(Reg8085.PC, startAddr);

			// �w�b�_��\��
			println("Addr Code       Mnemonic         A  B  C  D  E  H  L  SP   PC  Z C S P AC");

			// ���s���[�v
			while (cpu.reg.getReg(Reg8085.PC) <= endAddr) {
				// ���߂��擾
				inst = cpu.decode(cpu.fetch());

				// ���s�O�̏�Ԃ�\��
				printTraceInfo(inst);

				// ���߂̎��s�҂�
				if (traceOneStep) {
					int c = read();
					if (c == 'Q' || c == 'q')
						break;
				}
				// ���߂����s
				try {
					cpu.execute(inst);
				} catch (OnBreakPointException e) {
					message = e.message;
				}

				// ���s���ʂ̃��W�X�^��\��
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
	// U �R�}���h
	// ***************************************************************************************************
	// �t�A�Z���u��
	void comDisassemble(int startAddr, int endAddr) {

		int orgPC = cpu.reg.getReg(Reg8085.PC);
		int newPC;
		Instruction8085 inst;

		cpu.reg.setReg(Reg8085.PC, startAddr);
		while (cpu.reg.getReg(Reg8085.PC) <= endAddr) {
			inst = cpu.decode(cpu.fetch());

			// �A�h���X��\�����A���߃R�[�h�A�I�y�����h�A�j�[���j�b�N�̕\��
			printTraceInfo(inst);
			println("");

			// �j�[���j�b�N�� HLT ��������I��
			if (inst.getMnemonic().equals("HLT"))
				break;

			// ���̖��߂�
			newPC = cpu.reg.getReg(Reg8085.PC) + inst.getSize();
			cpu.reg.setReg(Reg8085.PC, newPC);
		}

		// PC �l�����ɖ߂�
		cpu.reg.setReg(Reg8085.PC, orgPC);

		println("");
	}

	// ***************************************************************************************************
	// W �R�}���h
	// ***************************************************************************************************
	// ���������e���t�@�C���ɏ����o��
	void comWriteToFile(int startaddr, int endaddr, String filename)
			throws IOException {
		// �t�@�C�����I�[�v��
		DataOutputStream d = new DataOutputStream(new BufferedOutputStream(
				new FileOutputStream(filename)));

		// �t�@�C�� ID �̏�������
		d.writeByte(80/* P */);
		d.writeByte(72/* H */);

		// �R�[�h�̈�E��Ɨ̈�f�[�^�̏�������
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

		// �R�[�h���擾���A�t�@�C���Ɋi�[
		for (int addr = temp_codeAddr; addr < temp_codeAddr + temp_codeSize; addr++)
			d.writeByte((byte) mem.getValue(addr));

		// �t�@�C�� ID �̏�������
		d.writeByte(80/* P */);
		d.writeByte(66/* B */);

		// PUBLIC ���x���̃f�[�^����������
		d.writeShort(util.swapEndian(publicLabels.size()));

		for (int num = 0; num < publicLabels.size(); num++) {
			PublicLabel8085 p = publicLabels.getPublicLabelAt(num);
			for (int i = 0; i < 8; i++)
				d.writeByte((byte) p.name.charAt(i));

			d.writeShort(util.swapEndian(p.addr));
		}

		d.close();
		println("����ɏ����o���܂���.");
	}

	// ***************************************************************************************************
	// Z �R�}���h
	// ***************************************************************************************************
	// �f�o�b�O����\��
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
	// ���̑� ���������\�b�h�Q

	// ***************************************************************************************************
	// �R�}���h���擾
	public String getCommand() {
		print(">");
		return input.readln();
	}

	// ***************************************************************************************************
	// �t�@�C�����𓾂� FileDialog ���g��
	public String getFilename() {
		FileDialog fd = new FileDialog(frame, "Open MIC File");
		fd.setVisible(true);
		if (fd.getDirectory() != null && fd.getFile() != null)
			return fd.getDirectory() + fd.getFile();
		else
			return null;
	}

	// ***************************************************************************************************
	// �R���\�[���ɕ������\��
	public void print(String s) {
		output.append(s);
	}

	public void println(String s) {
		output.append(s + "\n");
	}

	// ***************************************************************************************************
	// �R���\�[�����當������擾
	public String readln() {
		return input.readln();
	}

	// ***************************************************************************************************
	// �R���\�[������P�������擾
	public int read() {
		return input.read();
	}

	// ***************************************************************************************************
	// �L�[�G�R�[����
	public void keyEchoOff() {
		input.echoOff();
	}

	// ***************************************************************************************************
	// �L�[�G�R�[�L��
	public void keyEchoOn() {
		input.echoOn();
	}

}
