package com.bearmini.tk85.base8085;

public interface DebuggerParent {

    // デバッグ開始前メソッド
    public abstract void onBeginDebug();

    // デバッグ終了時メソッド
    public abstract void onEndDebug();

}
