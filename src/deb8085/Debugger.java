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
/* デバッガクラス */
public class Debugger extends Thread {
	DebuggerParent parent;

	private boolean shouldExit = false;

	Frame frame;
	TextArea output;

	CPU8085 cpu = null; // CPU
	Mem8085 mem = null; // メモリ

	int codeAddr = 0; // コード領域の開始アドレス
	int codeSize = 0; // コード領域のサイズ

	int workAddr = 0; // 作業領域の開始アドレス
	int workSize = 0; // 作業領域のサイズ

	// パブリックラベル
	public PublicLabelList publicLabels;

	// ブレークポイント
	public BreakPointList breakPoints;

	public KeyboardInputStream input;
	public MouseKiller mouseKiller;

	// ***************************************************************************************************
	// コンストラクタ
	public Debugger(DebuggerParent parent, Frame frame, TextArea output) {
		this.parent = parent;
		this.frame = frame;
		this.output = output;

		publicLabels = new PublicLabelList();
		breakPoints = new BreakPointList();

		// CPU・メモリ領域を作成
		mem = new Mem8085();
		cpu = new CPU8085(mem, publicLabels, breakPoints);

		// コンソールから入力を可能にする＆コンソールに対するマウス操作を無効にする
		input = new KeyboardInputStream(output);
		mouseKiller = new MouseKiller(output);

		output.requestFocus();
	}

	public void requestStop() {
		this.shouldExit = true;
	}

	// ***************************************************************************************************
	// デバッグする
	public void run() {
		println("");

		do {
			// 命令を入力し、実行
			dispatch(getCommand());
		} while (!shouldExit);

	}

	// ***************************************************************************************************
	// デバッグコマンドのディスパッチ
	public void dispatch(String command) {
		char kind = '\0';
		String param = "";

		// コマンドの種類(先頭の一文字で、A～Z)と、そのパラメータに分ける
		try {
			kind = command.charAt(0);
			param = command.substring(1);
		} catch (StringIndexOutOfBoundsException e) {
		}

		// 命令の種類によって動作を分離
		switch (kind) {
		// アセンブル
		case 'A':
		case 'a': {
			StringTokenizer st = new StringTokenizer(param, ",");
			int startAddr;
			// 開始アドレスを取得
			if (st.hasMoreTokens())
				startAddr = util.unhex(st.nextToken());
			else
				startAddr = cpu.reg.getReg(Reg8085.PC);

			comAssemble(startAddr);

			break;
		}

			// ブレークポイントの設定･解除･情報表示
		case 'B':
		case 'b': {
			StringTokenizer st = new StringTokenizer(param, ",");
			int addr;
			int count;

			int paramCount = st.countTokens();

			// パラメータが何もなかったら 情報表示
			if (paramCount == 0) {
				comDisplayBreakPointInfo();
				break;
			}

			// パラメータが一つだけなら 解除
			else if (paramCount == 1) {
				addr = util.unhex(st.nextToken());
				comResetBreakPoint(addr);
			}

			// パラメータが二つなら 設定
			else if (paramCount == 2) {
				// アドレスとラベル名を取得
				addr = util.unhex(st.nextToken());
				count = util.unhex(st.nextToken());
				comSetBreakPoint(addr, count);
			}

			else {
				println("パラメータが多すぎます.");
				println("");
			}

			break;
		}

			//
		case 'C':
		case 'c':
			break;

		// メモリ領域の表示
		case 'D':
		case 'd': {
			StringTokenizer st = new StringTokenizer(param, ",");
			int startAddr, endAddr;
			// 開始アドレスを取得
			if (st.hasMoreTokens())
				startAddr = util.unhex(st.nextToken());
			else
				startAddr = cpu.reg.getReg(Reg8085.PC);

			// 終了アドレスを取得
			if (st.hasMoreTokens())
				endAddr = util.unhex(st.nextToken());
			else
				endAddr = startAddr + 127;

			comDumpMemoryArea(startAddr, endAddr);
			break;
		}

			// メモリ領域の編集
		case 'E':
		case 'e': {
			StringTokenizer st = new StringTokenizer(param, ",");
			int startAddr;
			// 開始アドレスを取得
			if (st.hasMoreTokens())
				startAddr = util.unhex(st.nextToken());
			else
				startAddr = cpu.reg.getReg(Reg8085.PC);

			comEditMemoryArea(startAddr);
			break;
		}

			// メモリ領域のフィル
		case 'F':
		case 'f': {
			StringTokenizer st = new StringTokenizer(param, ",");
			int startAddr, endAddr;
			short value;

			// 開始アドレスを取得
			if (st.hasMoreTokens())
				startAddr = util.unhex(st.nextToken());
			else
				startAddr = cpu.reg.getReg(Reg8085.PC);

			// 終了アドレスを取得
			if (st.hasMoreTokens())
				endAddr = util.unhex(st.nextToken());
			else
				endAddr = startAddr + 127;

			// フィルする値を取得
			if (st.hasMoreTokens())
				value = (short) util.unhex(st.nextToken());
			else
				value = 0;

			comFillMemoryArea(startAddr, endAddr, value);
			break;
		}

			// プログラムの実行
		case 'G':
		case 'g': {
			StringTokenizer st = new StringTokenizer(param, ",");
			int startAddr, endAddr;

			// 開始アドレスを取得
			if (st.hasMoreTokens())
				startAddr = util.unhex(st.nextToken());
			else
				startAddr = cpu.reg.getReg(Reg8085.PC);

			// 終了アドレスを取得
			if (st.hasMoreTokens())
				endAddr = util.unhex(st.nextToken());
			else
				endAddr = 0xFFFF;

			// 実行
			comRunProgram(startAddr, endAddr);

			break;
		}

			// ヘルプを表示
		case 'H':
		case 'h':
		case '?': {
			break;
		}

			// 割り込みのシミュレート
		case 'I':
		case 'i': {
			break;
		}

			// コマンドファイルの読み込み
		case 'J':
		case 'j': {
			break;
		}

			//
		case 'K':
		case 'k': {
			break;
		}

			// MIC ファイルをロード
		case 'L':
		case 'l': {
			// L コマンドのオペランドは ロードするファイル名
			String filename = param;

			// ファイル名が指定されていなければ
			if (filename.trim().equals("")) {
				// ダイアログを開いてファイル名を求める
				filename = getFilename();
				// ダイアログでキャンセルされたら
				if (filename == null)
					break;
			}

			try {
				// .MIC ファイルを読み込む
				comLoadMicFile(filename);

				// プログラムカウンタをセット
				cpu.reg.setReg(Reg8085.PC, codeAddr);
			} catch (IOException e) {
				println("ファイルの読み込みに失敗しました。");
			}
			break;
		}

			// メモリ領域の移動
		case 'M':
		case 'm': {
			StringTokenizer st = new StringTokenizer(param, ",");
			int startAddr, endAddr, destAddr;

			// コピー開始アドレスを取得
			if (st.hasMoreTokens())
				startAddr = util.unhex(st.nextToken());
			else
				startAddr = cpu.reg.getReg(Reg8085.PC);

			// 終了アドレスを取得
			if (st.hasMoreTokens())
				endAddr = util.unhex(st.nextToken());
			else
				endAddr = startAddr + 127;

			// コピー先アドレスを取得
			if (st.hasMoreTokens())
				destAddr = util.unhex(st.nextToken());
			else
				destAddr = endAddr + 1;

			comMoveMemoryArea(startAddr, endAddr, destAddr);
			break;
		}

			// ラベル名の設定・解除・情報表示
		case 'N':
		case 'n': {
			StringTokenizer st = new StringTokenizer(param, ",");
			String labelName;
			int addr;

			int paramCount = st.countTokens();

			// パラメータが何もなかったら 情報表示
			if (paramCount == 0) {
				comDisplayPublicLabelInfo();
				break;
			}

			// パラメータが一つだけなら 解除
			else if (paramCount == 1) {
				labelName = st.nextToken();
				comResetPublicLabel(labelName);
			}

			// パラメータが二つなら 設定
			else if (paramCount == 2) {
				// アドレスとラベル名を取得
				addr = util.unhex(st.nextToken());
				labelName = st.nextToken();
				comSetPublicLabel(labelName, addr);
			}

			else {
				println("パラメータが多すぎます.");
				println("");
			}

		}

			//
		case 'O':
		case 'o': {
			break;
		}

			// メモリ領域のプロテクト 設定･解除・情報表示
		case 'P':
		case 'p': {
			StringTokenizer st = new StringTokenizer(param, ",");
			int startAddr, endAddr;
			boolean resetProtect = false;

			// プロテクト開始アドレスを取得
			if (st.hasMoreTokens())
				startAddr = util.unhex(st.nextToken());
			else
				startAddr = cpu.reg.getReg(Reg8085.PC);

			// 終了アドレスを取得
			if (st.hasMoreTokens())
				endAddr = util.unhex(st.nextToken());
			else
				endAddr = startAddr; // １バイトだけ

			// 設定か解除かを取得
			if (st.hasMoreTokens()) {
				String str = st.nextToken();
				resetProtect = (str.equals("R") || str.equals("r"));
			}

			comProtectMemoryArea(startAddr, endAddr, resetProtect);
			break;
		}

			// デバッガの終了
		case 'Q':
		case 'q': {
			parent.onEndDebug();
			shouldExit = true;
			break;
		}

			// レジスタの表示、編集
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

			// プログラムのトレース
		case 'T':
		case 't': {
			StringTokenizer st = new StringTokenizer(param, ",");
			int startAddr, endAddr;
			boolean onestep;

			// 開始アドレスを取得
			if (st.hasMoreTokens())
				startAddr = util.unhex(st.nextToken());
			else
				startAddr = cpu.reg.getReg(Reg8085.PC);

			// 終了アドレスを取得
			if (st.hasMoreTokens())
				endAddr = util.unhex(st.nextToken());
			else
				endAddr = 0xFFFF;

			// 1ステップブレークか？
			if (st.hasMoreTokens())
				onestep = true;
			else
				onestep = false;

			comTraceProgram(startAddr, endAddr, onestep);
			break;
		}

			// 逆アセンブル
		case 'U':
		case 'u': {
			StringTokenizer st = new StringTokenizer(param, ",");
			int startAddr, endAddr;

			// 開始アドレスを取得
			if (st.hasMoreTokens())
				startAddr = util.unhex(st.nextToken());
			else
				startAddr = cpu.reg.getReg(Reg8085.PC);

			// 終了アドレスを取得
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

			// メモリ領域の書き出し
		case 'W':
		case 'w': {
			StringTokenizer st = new StringTokenizer(param, ",");
			int startAddr, endAddr;

			// 開始アドレスを取得
			if (st.hasMoreTokens())
				startAddr = util.unhex(st.nextToken());
			else
				startAddr = cpu.reg.getReg(Reg8085.PC);

			// 終了アドレスを取得
			if (st.hasMoreTokens())
				endAddr = util.unhex(st.nextToken());
			else
				endAddr = startAddr + 127;

			// セーブするファイル名を得る
			String filename = null;
			if (st.hasMoreTokens())
				filename = st.nextToken();

			// ファイル名が指定されていなければ
			if (filename == null || filename.trim().equals("")) {
				// ダイアログを開いてファイル名を求める
				filename = getFilename();
				// ダイアログでキャンセルされたら
				if (filename == null)
					break;
				// 同じ名前のファイルがすでに存在していたら

			}

			try {
				comWriteToFile(startAddr, endAddr, filename);
			} catch (IOException e) {
				println("ファイルの書き込みに失敗しました.");
			}

			break;
		}

			//
		case 'X':
		case 'x': {
			break;
		}

			// トレース時のステップオーバー領域を 設定･解除･情報表示
		case 'Y':
		case 'y': {
			break;
		}

			// 情報表示
		case 'Z':
		case 'z': {
			comDisplayDebugInfo();
			break;
		}

		}

	}

	// ***************************************************************************************************
	// ***************************************************************************************************
	// 表示用 汎用メソッド群

	// アドレスを表示
	void printAddr(int addr) {
		print(util.hex4(addr));
	}

	// 行の先頭としてアドレスを表示
	void printAddrAsLineHeader(int addr) {
		println("");
		printAddr(addr);
		print(" ");
	}

	// 命令コードを文字列にして返す
	String sprintOpecode(Instruction8085 inst) {
		StringBuffer sb = new StringBuffer();

		// 命令コードの1バイトめを表示
		sb.append(util.hex2(inst.getOpecode()) + " ");

		// 命令サイズに応じて 2バイトめ、3バイトめを表示
		if (inst.getSize() == 1)
			sb.append(util.space(8));
		else if (inst.getSize() == 2)
			sb.append(util.hex2(inst.getB2()) + util.space(6));
		else if (inst.getSize() == 3)
			sb.append(util.hex2(inst.getB2()) + " " + util.hex2(inst.getB3())
					+ util.space(3));

		return sb.toString();
	}

	// レジスタ表示のヘッダ
	void printRegistersHeader() {
		println(" A  B  C  D  E  H  L  SP   PC  Z C S P AC");
	}

	// レジスタの値を表示
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

	// メモリ内容表示のときのヘッダ
	void printMemoryHeader() {
		println("Addr  0  1  2  3  4  5  6  7  8  9  A  B  C  D  E  F  0123456789ABCDEF");
	}

	// メモリ内容を一行表示 アドレス＋ダンプ＋アスキー
	void printMemoryAreaOneLine(int startAddr, int endAddr) {
		StringBuffer sbDump = new StringBuffer();
		StringBuffer sbHead = new StringBuffer();
		StringBuffer sbAscii = new StringBuffer();

		// ヘッダとしてのアドレスと、メモリの内容にたどり着くまでの空っぽの部分
		sbHead.append(util.hex4(startAddr % 0xFFF0) + " ");
		sbAscii.append('*');
		for (int addr = startAddr & 0xFFF0; addr < startAddr; addr++)
			sbDump.append(util.space(3));

		// メモリダンプを表示
		for (int addr = startAddr; addr <= endAddr; addr++) {
			// メモリの内容と対応するアスキー文字を表示
			char c = (char) mem.getValue(addr);
			sbDump.append(util.hex2(c));
			sbAscii.append(util.makeValidChar(c));
			if (addr % 0x10 != 7)
				sbDump.append(" ");
			else
				sbDump.append("-");
		}

		// 16バイトに達していない分を補足
		for (int addr = endAddr + 1; addr < ((endAddr + 0x10) & 0xFFF0); addr++)
			sbDump.append(util.space(3));

		println("" + sbHead + sbDump + sbAscii);
	}

	// トレース情報（命令のアドレス、命令コード、ニーモニック）を表示
	void printTraceInfo(Instruction8085 inst) {
		StringBuffer sbAddr = new StringBuffer();
		StringBuffer sbInst = new StringBuffer();

		// 命令のアドレス
		sbAddr.append(util.hex4(cpu.reg.getReg(Reg8085.PC)));

		// 命令コード、ニーモニック
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
	// コマンド処理メソッド群

	// ***************************************************************************************************
	// A コマンド
	// ***************************************************************************************************
	// 簡易アセンブラ
	void comAssemble(int startAddr) {
		int addr = startAddr;
		String s;
		int com_bytes;

		SimpleAssembler asm = new SimpleAssembler(cpu);

		do {
			// プロンプト addr:
			print(util.hex4(addr) + ": ");
			s = readln();

			if (s.trim().equals(""))
				break;

			// 一行アセンブル 変換されたバイト数を得る
			try {
				com_bytes = asm.assemble(addr, s);
			} catch (OnEncodeException e) {
				println(e.message);
				continue;
			}

			// アドレスを進める
			addr += com_bytes;

		} while (true);

		println("");
	}

	// ***************************************************************************************************
	// B コマンド
	// ***************************************************************************************************
	// ブレークポイント情報を表示
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
	// ブレークポイントの設定・解除
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
	// D コマンド
	// ***************************************************************************************************
	// メモリ内容の表示
	void comDumpMemoryArea(int startAddr, int endAddr) {
		// 全体のヘッダ
		printMemoryHeader();

		// 一行で収まる場合
		if ((startAddr & 0xFFF0) == (endAddr & 0xFFF0))
			printMemoryAreaOneLine(startAddr, endAddr);

		// 二行以上にわたる場合
		else {
			int addr = startAddr;

			// 最初の行
			printMemoryAreaOneLine(startAddr, (startAddr & 0xFFF0) + 0x0f);

			// 中間の行、ない場合もある。
			for (addr = (startAddr + 0x10) & 0xFFF0; addr <= (endAddr - 0x10); addr += 0x10)
				printMemoryAreaOneLine(addr, addr + 0x0f);

			// 最後の行
			if ((addr & 0xFFF0) == (endAddr & 0xFFF0))
				printMemoryAreaOneLine(addr, endAddr);
		}
		println("");
	}

	// ***************************************************************************************************
	// E コマンド
	// ***************************************************************************************************
	// メモリ領域の編集
	void comEditMemoryArea(int startAddr) {
		// 全体のヘッダ
		printMemoryHeader();

		int addr = startAddr;
		int blankaddr = startAddr & 0x0F;

		// 表示＆入力ループ
		while (true) {
			// メモリ内容を表示
			printMemoryAreaOneLine(addr, (addr & 0xFFF0) + 0x0F);

			// 入力ヘッダ
			print("     " + util.space(blankaddr * 3));

			// 入力
			String s = readln();

			// 入力がなければ編集を終了
			if (s.trim().equals(""))
				break;

			// 入力を解析
			try {
				for (int i = 0; i < 0x10; i++)
					mem.setValue(addr + i, !(s.substring(3 * i, 3 * i + 2)
							.equals("  ")) ? util.unhex(s.substring(3 * i,
							3 * i + 2)) : mem.getValue(addr + i));
			} catch (StringIndexOutOfBoundsException e) {
			}

			// 次に進む
			addr = (addr & 0xFFF0) + 0x10;
			blankaddr = 0;
		}

	}

	// ***************************************************************************************************
	// F コマンド
	// ***************************************************************************************************
	// メモリ領域のフィル
	void comFillMemoryArea(int startAddr, int endAddr, short value) {
		for (int addr = startAddr; addr <= endAddr; addr++)
			mem.setValue(addr, value);
		println("");
	}

	// ***************************************************************************************************
	// G コマンド
	// ***************************************************************************************************
	// 実行
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
	// L コマンド
	// ***************************************************************************************************
	// MICファイルを読み込む
	public void comLoadMicFile(String filename) throws IOException {
		// ファイルをオープン
		DataInputStream d = new DataInputStream(new BufferedInputStream(
				new FileInputStream(filename)));

		// ファイル ID のチェック
		byte id1 = d.readByte();
		byte id2 = d.readByte();
		if (!(id1 == 80/* 'P' */&& id2 == 72/* 'H' */)) {
			throw new IOException();
		}

		// コード領域・作業領域データの取得
		codeAddr = d.readUnsignedShort();
		codeSize = d.readUnsignedShort();
		workSize = d.readUnsignedShort();
		workAddr = d.readUnsignedShort();

		codeAddr = util.swapEndian(codeAddr);
		codeSize = util.swapEndian(codeSize);
		workAddr = util.swapEndian(workAddr);
		workSize = util.swapEndian(workSize);

		// コードを取得し、｢メモリ｣ に格納
		for (int addr = codeAddr; addr < codeAddr + codeSize; addr++)
			mem.setValue(addr, d.readUnsignedByte());

		// ファイル ID のチェック
		byte id3 = d.readByte();
		byte id4 = d.readByte();
		if (!(id3 == 80/* 'P' */&& id4 == 66/* 'B' */)) {
			throw new IOException();
		}

		// PUBLIC ラベルのデータを取得
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
		// 情報を表示
		comDisplayDebugInfo();
		comDisplayPublicLabelInfo();
	}

	// ***************************************************************************************************
	// M コマンド
	// ***************************************************************************************************
	// メモリ領域の移動
	void comMoveMemoryArea(int startAddr, int endAddr, int destAddr) {
		for (int addr = 0; addr <= endAddr - startAddr; addr++)
			mem.setValue(destAddr + addr, mem.getValue(startAddr + addr));
		println("");
	}

	// ***************************************************************************************************
	// N コマンド
	// ***************************************************************************************************
	// パブリックラベル情報を表示
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
	// パブリックラベルの設定・解除
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
	// P コマンド
	// ***************************************************************************************************
	// メモリ領域のプロテクトの設定・解除
	void comProtectMemoryArea(int startAddr, int endAddr, boolean resetProtect) {
		for (int addr = startAddr; addr <= endAddr; addr++)
			mem.setReadOnly(addr, !resetProtect);
		println("");
	}

	// ***************************************************************************************************
	// R コマンド
	// ***************************************************************************************************
	// レジスタの保持している値を表示
	void comDumpRegister() {
		printRegistersHeader();
		printRegisters();
		println("");
		println("");
	}

	// ***************************************************************************************************
	// レジスタの保持している値を編集
	void comEditRegister() {
		// レジスタ値を表示
		printRegistersHeader();
		printRegisters();
		println("");

		// 一行読み込んで、分析
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
	// T コマンド
	// ***************************************************************************************************
	// トレース
	void comTraceProgram(int startAddr, int endAddr, boolean traceOneStep) {
		String message = null;
		Instruction8085 inst;

		keyEchoOff();
		cpu.restart();

		try {
			cpu.reg.setReg(Reg8085.PC, startAddr);

			// ヘッダを表示
			println("Addr Code       Mnemonic         A  B  C  D  E  H  L  SP   PC  Z C S P AC");

			// 実行ループ
			while (cpu.reg.getReg(Reg8085.PC) <= endAddr) {
				// 命令を取得
				inst = cpu.decode(cpu.fetch());

				// 実行前の状態を表示
				printTraceInfo(inst);

				// 命令の実行待ち
				if (traceOneStep) {
					int c = read();
					if (c == 'Q' || c == 'q')
						break;
				}
				// 命令を実行
				try {
					cpu.execute(inst);
				} catch (OnBreakPointException e) {
					message = e.message;
				}

				// 実行結果のレジスタを表示
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
	// U コマンド
	// ***************************************************************************************************
	// 逆アセンブル
	void comDisassemble(int startAddr, int endAddr) {

		int orgPC = cpu.reg.getReg(Reg8085.PC);
		int newPC;
		Instruction8085 inst;

		cpu.reg.setReg(Reg8085.PC, startAddr);
		while (cpu.reg.getReg(Reg8085.PC) <= endAddr) {
			inst = cpu.decode(cpu.fetch());

			// アドレスを表示し、命令コード、オペランド、ニーモニックの表示
			printTraceInfo(inst);
			println("");

			// ニーモニックが HLT だったら終了
			if (inst.getMnemonic().equals("HLT"))
				break;

			// 次の命令へ
			newPC = cpu.reg.getReg(Reg8085.PC) + inst.getSize();
			cpu.reg.setReg(Reg8085.PC, newPC);
		}

		// PC 値を元に戻す
		cpu.reg.setReg(Reg8085.PC, orgPC);

		println("");
	}

	// ***************************************************************************************************
	// W コマンド
	// ***************************************************************************************************
	// メモリ内容をファイルに書き出し
	void comWriteToFile(int startaddr, int endaddr, String filename)
			throws IOException {
		// ファイルをオープン
		DataOutputStream d = new DataOutputStream(new BufferedOutputStream(
				new FileOutputStream(filename)));

		// ファイル ID の書き込み
		d.writeByte(80/* P */);
		d.writeByte(72/* H */);

		// コード領域・作業領域データの書き込み
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

		// コードを取得し、ファイルに格納
		for (int addr = temp_codeAddr; addr < temp_codeAddr + temp_codeSize; addr++)
			d.writeByte((byte) mem.getValue(addr));

		// ファイル ID の書き込み
		d.writeByte(80/* P */);
		d.writeByte(66/* B */);

		// PUBLIC ラベルのデータを書き込み
		d.writeShort(util.swapEndian(publicLabels.size()));

		for (int num = 0; num < publicLabels.size(); num++) {
			PublicLabel8085 p = publicLabels.getPublicLabelAt(num);
			for (int i = 0; i < 8; i++)
				d.writeByte((byte) p.name.charAt(i));

			d.writeShort(util.swapEndian(p.addr));
		}

		d.close();
		println("正常に書き出しました.");
	}

	// ***************************************************************************************************
	// Z コマンド
	// ***************************************************************************************************
	// デバッグ情報を表示
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
	// その他 下働きメソッド群

	// ***************************************************************************************************
	// コマンドを取得
	public String getCommand() {
		print(">");
		return input.readln();
	}

	// ***************************************************************************************************
	// ファイル名を得る FileDialog を使う
	public String getFilename() {
		FileDialog fd = new FileDialog(frame, "Open MIC File");
		fd.setVisible(true);
		if (fd.getDirectory() != null && fd.getFile() != null)
			return fd.getDirectory() + fd.getFile();
		else
			return null;
	}

	// ***************************************************************************************************
	// コンソールに文字列を表示
	public void print(String s) {
		output.append(s);
	}

	public void println(String s) {
		output.append(s + "\n");
	}

	// ***************************************************************************************************
	// コンソールから文字列を取得
	public String readln() {
		return input.readln();
	}

	// ***************************************************************************************************
	// コンソールから１文字を取得
	public int read() {
		return input.read();
	}

	// ***************************************************************************************************
	// キーエコー無し
	public void keyEchoOff() {
		input.echoOff();
	}

	// ***************************************************************************************************
	// キーエコー有り
	public void keyEchoOn() {
		input.echoOn();
	}

}
