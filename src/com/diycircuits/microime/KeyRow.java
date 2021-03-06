package com.diycircuits.microime;

import java.util.ArrayList;
import android.graphics.Rect;

public class KeyRow {

    private ArrayList<Key> mKeyList = new ArrayList<Key>();

    private double mOffset = 0.0;

    private double mSize = 0.0;
    
    public Rect mBounds = new Rect();

    public void setOffset(double m) {
	mOffset = m;
    }

    public double getOffset() {
	return mOffset;
    }

    public void setSize(double m) {
	mSize = m;
    }

    public double getSize() {
	return mSize;
    }

    public void addKey(double size, String type) {
	Key key = new Key();
	key.mSize = size;
	key.mKeyLen = 0;
	if (type.compareTo("shift") == 0) {
	    key.mType = KeyType.SHIFT;
	} else if (type.compareTo("enter") == 0) {
	    key.mType = KeyType.ENTER;
	} else if (type.compareTo("delete") == 0) {
	    key.mType = KeyType.DELETE;
	} else if (type.compareTo("tab") == 0) {
	    key.mType = KeyType.TAB;
	} else if (type.compareTo("inputmethod") == 0) {
	    key.mType = KeyType.INPUT_METHOD;
	    key.mKey[0] = '?';
	    key.mKey[1] = '1';
	    key.mKey[2] = '2';
	    key.mKey[3] = '3';
	    key.mKeyLen = 4;
	} else if (type.compareTo("comma") == 0) {
	    key.mType = KeyType.COMMA;
	    key.mKey[0] = ',';
	    key.mKeyLen = 1;
	} else if (type.compareTo("space") == 0) {
	    key.mType = KeyType.SPACE;
	} else if (type.compareTo("moresymbols") == 0) {
	    key.mType = KeyType.MORESYMBOLS;
	    key.mKey[0] = '=';
	    key.mKey[1] = '\\';
	    key.mKey[2] = '<';
	    key.mKeyLen = 3;
	} else if (type.compareTo("symbols") == 0) {
	    key.mType = KeyType.SYMBOLS;
	    key.mKey[0] = '?';
	    key.mKey[1] = '1';
	    key.mKey[2] = '2';
	    key.mKey[3] = '3';
	    key.mKeyLen = 4;
	} else if (type.compareTo("dot") == 0) {
	    key.mType = KeyType.DOT;
	    key.mKey[0] = '.';
	    key.mKeyLen = 1;
	} else if (type.compareTo("action") == 0) {
	    key.mType = KeyType.ACTION;
	}
	mKeyList.add(key);
    }

    public void addKeys(double size, String value, String shift, String alt) {
	for (int count = 0; count < value.length(); count++) {
	    Key key = new Key();
	    key.mSize = size;
	    key.mKey[0] = value.charAt(count);
	    if (alt != null && alt.length() == value.length()) key.mAlt[0] = alt.charAt(count);
	    if (shift != null && shift.length() == value.length()) key.mShift[0] = shift.charAt(count);
	    key.mKeyLen = 1;
	    key.mType = KeyType.NORMAL;
	    mKeyList.add(key);
	}
    }

    public Key getKey(int index) {
	return mKeyList.get(index);
    }

    public int getColumn() {
	return mKeyList.size();
    }
    
    public String toString() {
	StringBuffer buf = new StringBuffer();

	for (int count = 0; count < mKeyList.size(); count++) {
	    buf.append(mKeyList.get(count).toString());
	}

	return buf.toString();
    }

}
