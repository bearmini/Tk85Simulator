package com.bearmini.tk85.base8085;

//***************************************************************************************************
//***************************************************************************************************
// ユーティリティ関数群
public class util {

    // ２バイト整数のエンディアンを逆にする
    public static int swapEndian(int x) {
        return ((x & 0xFF00) >>> 8) + ((x & 0xFF) << 8);
    }

    // 渡された整数を十六進数４桁表現にする
    public static String hex4(int x) {
        int x0, x1, x2, x3;

        x0 = x & 0x000F;
        x1 = (x & 0x00F0) >>> 4;
        x2 = (x & 0x0F00) >>> 8;
        x3 = (x & 0xF000) >>> 12;

        return "" + Character.forDigit(x3, 16) + Character.forDigit(x2, 16)
                + Character.forDigit(x1, 16) + Character.forDigit(x0, 16);
    }

    // 渡された整数を十六進数２桁表現にする
    public static String hex2(int x) {
        int x0, x1;

        x0 = x & 0x000F;
        x1 = (x & 0x00F0) >>> 4;

        return "" + Character.forDigit(x1, 16) + Character.forDigit(x0, 16);
    }

    // 十六進数逆変換
    public static int unhex(String hex) {
        if (hex == null)
            return 0;

        int result = 0;

        hex.trim();
        while (!hex.equals("")) {
            char c = hex.charAt(0);
            if (c == ' ')
                result <<= 4;
            else
                result = (result << 4) + Character.digit(c, 16);

            hex = hex.substring(1);
        }

        return result & 0xFFFF;
    }

    // 指定された個数のスペースを返す
    public static String space(int n) {
        String result = "";

        for (int i = 0; i < n; i++)
            result += " ";

        return result;
    }

    // 指定されたアスキーコードをもつ文字を文字列にして返す
    public static char makeValidChar(char a) {
        if (' ' <= a && a <= '~')
            return a;
        else
            return '.';
    }

    // ビットが立っているかどうかチェックする
    public static boolean bitOn(int x, int bit) {
        return ((x & (1 << bit)) != 0);
    }

    // ビットをたてる
    public static int setBit(int x, int bit, boolean set) {
        if (set)
            return (x | (1 << bit));
        else
            return (x & ((1 << bit) ^ 0xff));
    }

}
