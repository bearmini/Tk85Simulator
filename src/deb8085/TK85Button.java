package deb8085;

import java.awt.Button;

//***************************************************************************************************
//***************************************************************************************************
/* TK85 �L�[�{�[�h�p�� �L�[�{�^�� */
public class TK85Button extends Button {
	/**
	 * 
	 */
	private static final long serialVersionUID = 689966738015090151L;
	
	public int keycode;

	// ***************************************************************************************************
	// �R���X�g���N�^
	public TK85Button(int code, String caption) {
		keycode = code;
		setLabel(caption);
	}

}
