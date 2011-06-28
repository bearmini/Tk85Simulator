package com.bearmini.tk85.base8085;

import java.awt.Button;

//***************************************************************************************************
//***************************************************************************************************
/* TK85 キーボード用の キーボタン */
public class TK85Button extends Button {
    /**
     */
    private static final long serialVersionUID = 689966738015090151L;

    public int keycode;

    // ***************************************************************************************************
    // コンストラクタ
    public TK85Button(int code, String caption) {
        keycode = code;
        setLabel(caption);
    }

}
