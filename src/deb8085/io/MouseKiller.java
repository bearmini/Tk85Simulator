package deb8085.io;

import java.awt.TextArea;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class MouseKiller extends Object implements MouseListener {

	private boolean enabled = false;

	public MouseKiller(TextArea console) {
		console.addMouseListener(this);
	}

	public void mouseClicked(MouseEvent e) {
		if (enabled)
			e.consume();
	}

	public void mousePressed(MouseEvent e) {
		if (enabled)
			e.consume();
	}

	public void mouseReleased(MouseEvent e) {
		if (enabled)
			e.consume();
	}

	public void mouseEntered(MouseEvent e) {
		if (enabled)
			e.consume();
	}

	public void mouseExited(MouseEvent e) {
		if (enabled)
			e.consume();
	}

	public void enable() {
		enabled = true;
	}

	public void disable() {
		enabled = false;
	}

}