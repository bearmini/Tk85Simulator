package deb8085;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;

public class LED7Seg extends Canvas {
	/**
	 * 
	 */
	private static final long serialVersionUID = 584293909941667985L;

	// セグメントデータ
	public short segdata = 15;

	// セグメント座標
	// a b c d e f g h
	int x[] = { 8, 24, 24, 8, 5, 5, 8, 27 };
	int y[] = { 5, 8, 25, 40, 25, 8, 22, 43 };
	int w[] = { 16, 3, 3, 16, 3, 3, 16, 4 };
	int h[] = { 3, 14, 15, 3, 15, 14, 3, 4 };

	// コンストラクタ
	public LED7Seg() {
		setBackground(Color.red.darker().darker().darker());
		setSize(32, 48);
	}

	// 描画
	public void paint(Graphics g) {
		for (int i = 0; i < 8; i++) {
			if (util.bitOn(segdata, i))
				g.setColor(Color.red.brighter().brighter());
			else
				g.setColor(Color.red.darker().darker());

			g.fillRect(x[i], y[i], w[i], h[i]);
		}

	}

	public void setSegData(short val) {
		segdata = val;
		paint(getGraphics());
	}

	public void update(Graphics g) {
		paint(g);
	}

}
