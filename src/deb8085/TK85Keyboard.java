package deb8085;

import java.awt.*;
import java.awt.event.*;

/*

             line
8255          3       2       1
--------+     |       |       |
PortA 7 |-----+-------+-------+-------
        |     |Reg    |F      |7
        |     |       |       |
      6 |-----+-------+-------+-------
        |     |Mode   |E      |6
        |     |       |       |
      5 |-----+-------+-------+-------
        |     |WR/ENT |D      |5
        |     |       |       |
      4 |-----+-------+-------+-------
        |     |R.INC  |C      |4
        |     |       |       |
      3 |-----+-------+-------+-------
        |     |R.DEC  |B      |3
        |     |       |       |
      2 |-----+-------+-------+-------
        |     |A.SET  |A      |2
        |     |       |       |
      1 |-----+-------+-------+-------
        |     |CONT   |9      |1
        |     |       |       |
      0 |-----+-------+-------+-------
        |     |RUN    |8      |0
        |     |       |       |
        |     |       |       |
PortC 6 |-----+       |       |
      5 |-------------+       |
      4 |---------------------+


*/

//***************************************************************************************************
//***************************************************************************************************
/* TK85 キーボード クラス */
public class TK85Keyboard extends Panel implements ParallelInputDevice,
		ParallelOutputDevice, MouseListener, ItemListener {
	// TK85 シミュレータ
	public TK85Simulator simulator;

	// 現在押されているボタンのキーコード
	int currKeyCode = 0xFF;

	// 選択されているライン
	int line;

	// リセットボタン
	Button btnReset;

	// ステップ実行選択スイッチ
	Checkbox cbStep;

	// ボタン
	TK85Button buttons[] = new TK85Button[25];

	// ボタンのキャプション
	String buttonLabel[] = {
	/* Line 1 */"0", "1", "2", "3", "4", "5", "6", "7",
	/* Line 2 */"8", "9", "A", "B", "C", "D", "E", "F",
	/* Line 3 */"RUN", "CONT", "ADDR SET", "READ DEC", "READ INC", "WR/ENT",
			"MODE", "REG",
			/* Special */"MON" };

	// ボタン定数
	static final int BUTTON_0 = 0;
	static final int BUTTON_1 = 1;
	static final int BUTTON_2 = 2;
	static final int BUTTON_3 = 3;
	static final int BUTTON_4 = 4;
	static final int BUTTON_5 = 5;
	static final int BUTTON_6 = 6;
	static final int BUTTON_7 = 7;
	static final int BUTTON_8 = 8;
	static final int BUTTON_9 = 9;
	static final int BUTTON_A = 10;
	static final int BUTTON_B = 11;
	static final int BUTTON_C = 12;
	static final int BUTTON_D = 13;
	static final int BUTTON_E = 14;
	static final int BUTTON_F = 15;
	static final int BUTTON_RUN = 16;
	static final int BUTTON_CONT = 17;
	static final int BUTTON_ADDRSET = 18;
	static final int BUTTON_READDEC = 19;
	static final int BUTTON_READINC = 20;
	static final int BUTTON_WRENT = 21;
	static final int BUTTON_MODE = 22;
	static final int BUTTON_REG = 23;
	static final int BUTTON_MON = 24;

	// ***************************************************************************************************
	// コンストラクタ
	public TK85Keyboard() {
		setLayout(new BorderLayout(10, 10));

		Panel pnlResetAndStep = new Panel();
		Panel pnlButtons = new Panel();

		// ボタン生成・貼り付け
		pnlButtons.setLayout(new GridLayout(5, 5));

		for (int i = 0; i < 25; i++) {
			buttons[i] = new TK85Button(i, buttonLabel[i]);
			buttons[i].addMouseListener(this);
		}

		pnlButtons.add(buttons[BUTTON_CONT]);
		pnlButtons.add(buttons[BUTTON_RUN]);
		pnlButtons.add(buttons[BUTTON_REG]);
		pnlButtons.add(buttons[BUTTON_MODE]);
		pnlButtons.add(buttons[BUTTON_MON]);
		pnlButtons.add(buttons[BUTTON_C]);
		pnlButtons.add(buttons[BUTTON_D]);
		pnlButtons.add(buttons[BUTTON_E]);
		pnlButtons.add(buttons[BUTTON_F]);
		pnlButtons.add(buttons[BUTTON_ADDRSET]);
		pnlButtons.add(buttons[BUTTON_8]);
		pnlButtons.add(buttons[BUTTON_9]);
		pnlButtons.add(buttons[BUTTON_A]);
		pnlButtons.add(buttons[BUTTON_B]);
		pnlButtons.add(buttons[BUTTON_READINC]);
		pnlButtons.add(buttons[BUTTON_4]);
		pnlButtons.add(buttons[BUTTON_5]);
		pnlButtons.add(buttons[BUTTON_6]);
		pnlButtons.add(buttons[BUTTON_7]);
		pnlButtons.add(buttons[BUTTON_READDEC]);
		pnlButtons.add(buttons[BUTTON_0]);
		pnlButtons.add(buttons[BUTTON_1]);
		pnlButtons.add(buttons[BUTTON_2]);
		pnlButtons.add(buttons[BUTTON_3]);
		pnlButtons.add(buttons[BUTTON_WRENT]);

		// リセットボタン生成・貼り付け
		pnlResetAndStep.setLayout(new BorderLayout());
		btnReset = new Button("Reset");
		btnReset.addMouseListener(this);
		pnlResetAndStep.add("East", btnReset);

		// ステップ実行ラジオボタン生成・貼り付け
		cbStep = new Checkbox("Step", false);
		cbStep.addItemListener(this);
		pnlResetAndStep.add("West", cbStep);

		add("North", pnlResetAndStep);
		add("Center", pnlButtons);
	}

	// ***************************************************************************************************
	// 8255 からの出力
	public void out(int val) {
		select(val);
	}

	// ***************************************************************************************************
	// 8255 への入力
	public int in() {
		return scan();
	}

	// ***************************************************************************************************
	// 8255 によるライン選択
	static final int PC4 = 0xEF;
	static final int PC5 = 0xDF;
	static final int PC6 = 0xBF;

	public void select(int portc) {
		if (!util.bitOn(portc, 4))
			line = 1;
		else if (!util.bitOn(portc, 5))
			line = 2;
		else if (!util.bitOn(portc, 6))
			line = 3;
		else
			line = 0;

		if (util.bitOn(portc, 7))
			simulator.startCountInstruction();
		else
			simulator.stopCountInstruction();
	}

	// ***************************************************************************************************
	// 押されているキーをスキャン 8255 に渡す形式
	public int scan() {
		// 選択されているラインによって場合分け
		switch (line) {
		case 1:
			if (currKeyCode < 8)
				return (1 << currKeyCode) ^ 0xFF;
			else
				return 0xFF;

		case 2:
			if (8 <= currKeyCode && currKeyCode < 0x10)
				return (1 << (currKeyCode & 0x07)) ^ 0xFF;
			else
				return 0xFF;

		case 3:
			if (0x10 <= currKeyCode && currKeyCode < 0x18)
				return (1 << (currKeyCode & 0x07)) ^ 0xFF;
			else
				return 0xFF;

		default:
			return 0xFF;
		}
	}

	// ***************************************************************************************************
	// マウスリスナ としてのインプリメント
	// ***************************************************************************************************

	// ***************************************************************************************************
	// マウスがクリックされたら
	public void mouseClicked(MouseEvent e) {
	}

	// ***************************************************************************************************
	// マウスが押されたら
	public void mousePressed(MouseEvent e) {
		if (e.getComponent() instanceof TK85Button) {
			// 押されたキーのキーコードを登録
			currKeyCode = ((TK85Button) e.getComponent()).keycode;
		}
	}

	// ***************************************************************************************************
	// マウスが離されたら
	public void mouseReleased(MouseEvent e) {
		if (e.getComponent() instanceof TK85Button) {
			// キーコードを "何も押されていない" にする
			currKeyCode = 0xFF;
		}

		if (e.getComponent() instanceof TK85Button) {
			// MON キーが押されたら
			if (((TK85Button) e.getComponent()).getLabel().equals("MON"))
				if (simulator != null) {
					simulator.cpu.interruptTRAP();
					simulator.restart();
				}
		} else if (e.getComponent() instanceof Button) {
			// Reset ボタンが押されたら
			if (((Button) e.getComponent()).getLabel().equals("Reset"))
				if (simulator != null) {
					simulator.cpu.reset();
					simulator.restart();
				}
		}

	}

	// ***************************************************************************************************
	// マウスが入ってきたら
	public void mouseEntered(MouseEvent e) {
	}

	// ***************************************************************************************************
	// マウスが出ていったら
	public void mouseExited(MouseEvent e) {
	}

	// ***************************************************************************************************
	// アイテムリスナ としてのインプリメント
	// ***************************************************************************************************

	// ***************************************************************************************************
	// チェックボックスの状態が変更されたら
	public void itemStateChanged(ItemEvent e) {
		if (e.getItemSelectable() instanceof Checkbox)
			// Step チェックボックスが変更されたら
			if (((Checkbox) e.getItemSelectable()).getLabel().equals("Step"))
				simulator.step = ((Checkbox) e.getItemSelectable()).getState();
	}

}
