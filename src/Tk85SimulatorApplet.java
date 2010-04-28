import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import deb8085.TK85Keyboard;
import deb8085.TK85LED;
import deb8085.TK85Simulator;

public class Tk85SimulatorApplet extends Applet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8112550200725052568L;
	
	TK85Simulator simulator; // TK85�V�~�����[�^
	TK85LED led;
	TK85Keyboard kb;
	final int DMA_BASEADDR_LED = 0x83f8;

	public void init() {
		setBackground(Color.green.darker().darker());
		setLayout(new BorderLayout());
		setFont(new Font("SansSerif", Font.PLAIN, 16));

		// LED �𐶐��A�\��t��
		led = new TK85LED(DMA_BASEADDR_LED);
		add("North", led);

		// �L�[�{�[�h�𐶐��A�\��t��
		kb = new TK85Keyboard();
		add("Center", kb);

		simulator = new TK85Simulator(led, kb);
		simulator.start();

	}
}
