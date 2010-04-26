package deb8085.io;

import java.awt.*;

public class RingBufferTest extends Frame {
	RingBuffer buf;
	TextArea console;

	public RingBufferTest() {
		buf = new RingBuffer(10);
		// �{�[�_�[���C�A�E�g�ŃR���|�[�l���g��z�u
		setLayout(new BorderLayout());

		add("South", new Button("TEST"));
		console = new TextArea("", 20, 30);
		add("Center", console);
	}

	public static void main(String args[]) {
		RingBufferTest appli = new RingBufferTest();

		appli.setTitle("�����O�o�b�t�@����m�F");
		appli.pack();
		appli.show();
	}

	void test() {
		for (int i = 0; i < 50; i++) {
			buf.put(i);
			console.append("put:" + i + "\n");
			if (i % 3 == 0)
				console.append("get: " + buf.get() + "\n");
		}
	}

	public boolean handleEvent(Event e) {
		switch (e.id) {
		case Event.WINDOW_DESTROY:
			System.exit(0);
		}
		return super.handleEvent(e);
	}

	public boolean action(Event event, Object arg) {
		String label = (String) arg;

		if (event.target instanceof Button) {
			if (label.equals("TEST"))
				test();
		}

		return super.action(event, arg);
	}

}