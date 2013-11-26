package com.diycircuits.microime;

import java.util.ArrayList;
import android.util.Log;

public class KeyboardState {

    private final static KeyboardState mInstance = new KeyboardState();

    private String mCurrent = "CANGJIE";
    
    private KeyboardState() {
    }

    public static KeyboardState getInstance() {
	return mInstance;
    }

    public Keyboard getCurrentKeyboard() {
	return SystemParams.getInstance().getKeyboard(mCurrent);
    }
    
}
