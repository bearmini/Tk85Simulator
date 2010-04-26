import java.awt.*;
import java.awt.event.*;
import java.io.DataInputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

import deb8085.*;

//***************************************************************************************************
//***************************************************************************************************
/* シミュレータ用のフレームクラス */
public class TestTK85Simulator extends Frame implements SimulatorParent,
		ActionListener, WindowListener {
	TK85Simulator simulator; // TK85シミュレータ
	TK85LED led;
	TK85Keyboard kb;

	final int DMA_BASEADDR_LED = 0x83f8;

	// ***************************************************************************************************
	// コンストラクタ
	public TestTK85Simulator() {
		addWindowListener(this);

		setBackground(Color.green.darker().darker());

		// メニューを作成
		Menu mfile = new Menu("File");
		MenuItem mniLoad = new MenuItem("Load");
		MenuItem mniQuit = new MenuItem("Quit");

		mniLoad.addActionListener(this);
		mniQuit.addActionListener(this);

		mfile.add(mniLoad);
		mfile.add(new MenuItem("-"));
		mfile.add(mniQuit);

		MenuBar mb = new MenuBar();
		mb.add(mfile);
		setMenuBar(mb);

		setLayout(new BorderLayout());
		setFont(new Font("SansSerif", Font.PLAIN, 16));

		// LED を生成、貼り付け
		led = new TK85LED(DMA_BASEADDR_LED);
		add("North", led);

		// キーボードを生成、貼り付け
		kb = new TK85Keyboard();
		add("Center", kb);

		// ダミーのスペーサをいれる
		add("West", new Panel());
		add("East", new Panel());
		add("South", new Panel());

	}

	// ***************************************************************************************************
	// イベントハンドラ
	public void windowOpened(WindowEvent e) {
	}

	public void windowClosing(WindowEvent e) {
		if (e.getID() == Event.WINDOW_DESTROY)
			System.exit(0);
	}

	public void windowClosed(WindowEvent e) {
	}

	public void windowIconified(WindowEvent e) {
	}

	public void windowDeiconified(WindowEvent e) {
	}

	public void windowActivated(WindowEvent e) {
	}

	public void windowDeactivated(WindowEvent e) {
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("Quit"))
			System.exit(0);
		else if (e.getActionCommand().equals("Load")) {
			try {
				loadMicFile(getFilename());
			} catch (IOException ie) {
				System.out.println(ie.toString());
			}
		}

	}

	// ***************************************************************************************************
	// アプリケーションのメイン
	public static void main(String args[]) {
		TestTK85Simulator window = new TestTK85Simulator();

		window.setTitle("TK85シミュレータ 動作確認");
		window.pack();
		window.show();

		window.startSimulation();

	}

	// ***************************************************************************************************
	// シミュレーション開始
	public void startSimulation() {
		simulator = new TK85Simulator(led, kb);
		simulator.start();

	}

	// ***************************************************************************************************
	// シミュレーションが終わったら
	public void onEndSimulation() {
	}

	// ***************************************************************************************************
	// ファイル名をダイアログを用いて取得
	String getFilename() {
		FileDialog fd = new FileDialog(this, "Open MIC File");
		fd.show();
		if (fd.getDirectory() != null && fd.getFile() != null)
			return fd.getDirectory() + fd.getFile();
		else
			return null;
	}

	// ***************************************************************************************************
	// MICファイルを読み込む
	public void loadMicFile(String filename) throws IOException {
		if (filename == null)
			return;

		// ファイルをオープン
		DataInputStream d = new DataInputStream(new BufferedInputStream(
				new FileInputStream(filename)));

		// ファイル ID のチェック
		byte id1 = d.readByte();
		byte id2 = d.readByte();
		if (!(id1 == 80/* 'P' */&& id2 == 72/* 'H' */)) {
			throw new IOException();
		}

		// コード領域・作業領域データの取得＆読み捨て
		int codeAddr = util.swapEndian(d.readUnsignedShort());
		int codeSize = util.swapEndian(d.readUnsignedShort());
		int workSize = d.readUnsignedShort();
		int workAddr = d.readUnsignedShort();

		// コードを取得し、｢メモリ｣ に格納
		for (int addr = codeAddr; addr < codeAddr + codeSize; addr++)
			simulator.mem.setValue(addr, d.readUnsignedByte());

	}

}
