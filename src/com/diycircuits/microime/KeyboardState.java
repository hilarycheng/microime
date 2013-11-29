package com.diycircuits.microime;

import java.util.ArrayList;
import android.util.Log;

public class KeyboardState {

    private final static KeyboardState mInstance = new KeyboardState();

    private int mId = R.xml.qwerty;

    private int mIndex = 0;
    
    private KeyboardState() {
    }

    public int getKeyboardIndex() {
	return mIndex;
    }

    public void setKeyboardIndex(int index) {
	switch (index) {
	case 0:
	    mId = R.xml.qwerty;
	    mIndex = index;
	    break;
	case 1:
	    mId = R.xml.cangjie;
	    mIndex = index;
	    break;
	case 2:
	    mId = R.xml.quick;
	    mIndex = index;
	    break;
	case 3:
	    mId = R.xml.stroke;
	    mIndex = index;
	    break;
	}
    }
    
    public static KeyboardState getInstance() {
	return mInstance;
    }

    public Keyboard getCurrentKeyboard() {
	return SystemParams.getInstance().getKeyboard(mId);
    }
    
}
