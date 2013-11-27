package com.diycircuits.microime;

import java.util.ArrayList;
import android.util.Log;
import android.graphics.Rect;

public class Key {

    public double mSize = 0.0;

    public char mKey[] = new char[4];

    public int mKeyLen = 0;

    public int mFontSize = 0;

    public KeyType mType = KeyType.NORMAL;

    public Rect mBounds = new Rect();

    public Rect mIconBounds = null;

    public int mStates[] = { 0, 0, 0 };

    public float mMainX = (float) 0.0;

    public float mMainY = (float) 0.0;

    public void setPressed() {
	if (mType == KeyType.ACTION || mType == KeyType.INPUT_METHOD || mType == KeyType.DELETE || mType == KeyType.SHIFT || mType == KeyType.TAB) {
	    mStates[1] = android.R.attr.state_pressed;
	} else {
	    mStates[0] = android.R.attr.state_pressed;
	}
    }

    public void setRelease() {
	if (mType == KeyType.ACTION || mType == KeyType.INPUT_METHOD || mType == KeyType.DELETE || mType == KeyType.SHIFT || mType == KeyType.TAB) {
	    mStates[1] = 0;
	} else {
	    mStates[0] = 0;
	}
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
