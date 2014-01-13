package com.diycircuits.microime;

import java.util.ArrayList;
import android.util.Log;
import android.graphics.Rect;

public class Key {

    public double mSize = 0.0;

    public char mKey[] = new char[4];
    
    public char mAlt[] = new char[1];

    public char mShift[] = new char[1];

    public int mAltFontSize = 0;

    public int mKeyLen = 0;

    public int mFontSize = 0;

    public KeyType mType = KeyType.NORMAL;

    public Rect mBounds = new Rect();

    public Rect mIconBounds = null;

    public int mStates[] = { 0, 0, 0 };

    public float mMainX = (float) 0.0;

    public float mMainY = (float) 0.0;

    public float mAltX = (float) 0.0;

    public float mAltY = (float) 0.0;
    
    public long mStartPress = -1;

    public boolean mHold = false;

    public boolean mPressed = false;

    public float mX = 0;

    public boolean mPopupShown = false;

    public void setSymbolState(boolean isSymbol) {
	if (mType == KeyType.INPUT_METHOD) {
	    if (isSymbol) {
		mKey[0] = 'A';
		mKeyLen = 1;
	    } else {
		mKey[0] = '?';
		mKey[1] = '1';
		mKey[2] = '2';
		mKey[3] = '3';
		mKeyLen = 4;
	    }
	}
    }

    public boolean isSpecialKey() {
	return mType == KeyType.ACTION || mType == KeyType.INPUT_METHOD ||
	    mType == KeyType.DELETE || mType == KeyType.SHIFT || mType == KeyType.TAB ||
	    mType == KeyType.SYMBOLS || mType == KeyType.MORESYMBOLS ||
	    mType == KeyType.INPUT_METHOD;
    }
    
    public void setPressed() {
	if (isSpecialKey()) {
	    mStates[1] = android.R.attr.state_pressed;
	} else {
	    mStates[0] = android.R.attr.state_pressed;
	}

	mPressed = true;
	mStartPress = System.currentTimeMillis();
    }

    public void setRelease() {
	if (isSpecialKey()) {
	    mStates[1] = 0;
	    mStates[0] = android.R.attr.state_single;
	} else {
	    mStates[0] = 0;
	}

	mPressed = false;
	mStartPress = -1; mHold = false;
    }

    public long getPressedDuration() {
	if (mStartPress == -1) return 0;
	return System.currentTimeMillis() - mStartPress;
    }
    
    public void setHold() {
	mHold = true;
    }

    public boolean isHold() {
	return mHold;
    }

    public boolean isHoldTimeReach() {
	long dur = getPressedDuration();
	if (dur == -1 || dur < SystemParams.getInstance().getHoldTime()) return false;

	return true;
    }
    
    public String toString() {
	StringBuffer sb = new StringBuffer();
	sb.append('[');
	switch (mType) {
	case NORMAL:
	    sb.append("Normal Key");
	    sb.append(",");
	    sb.append(mKey);
	    break;
	case SHIFT:
	    sb.append("Shift Key");
	    break;
	case ENTER:
	    sb.append("Enter Key");
	    break;
	case DELETE:
	    sb.append("Delete Key");
	    break;
	case INPUT_METHOD:
	    sb.append("IM Key");
	    break;
	case COMMA:
	    sb.append("Comma Key");
	    break;
	case SPACE:
	    sb.append("Space Key");
	    break;
	case DOT:
	    sb.append("Dot Key");
	    break;
	case ACTION:
	    sb.append("Action Key");
	    break;
	default:
	    sb.append("Unknown");
	    break;
	}
	sb.append(",Size=");
	sb.append(mSize);
	sb.append(']');

	return sb.toString();
    }
    
}
