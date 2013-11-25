package com.diycircuits.microime;

import java.util.ArrayList;
import android.graphics.Rect;

public class KeyRow {

    private ArrayList<Key> mKeyList = new ArrayList<Key>();

    private double mOffset = 0.0;
    
    public Rect mBounds = new Rect();

    public void setOffset(double m) {
	mOffset = m;
    }

    public double getOffset() {
	return mOffset;
    }

    public void addKey(double size, String type) {
	Key key = new Key();
	key.mSize = size;
	key.mKey = ' ';
	if (type.compareTo("shift") == 0) {
	    key.mType = KeyType.SHIFT;
	} else if (type.compareTo("enter") == 0) {
	    key.mType = KeyType.ENTER;
	} else if (type.compareTo("delete") == 0) {
	    key.mType = KeyType.DELETE;
	} else if (type.compareTo("inputmethod") == 0) {
	    key.mType = KeyType.INPUT_METHOD;
	} else if (type.compareTo("comma") == 0) {
	    key.mType = KeyType.COMMA;
	} else if (type.compareTo("space") == 0) {
	    key.mType = KeyType.SPACE;
	} else if (type.compareTo("dot") == 0) {
	    key.mType = KeyType.DOT;
	} else if (type.compareTo("action") == 0) {
	    key.mType = KeyType.ACTION;
	}
	mKeyList.add(key);
    }

    public void addKeys(double size, String value) {
	for (int count = 0; count < value.length(); count++) {
	    Key key = new Key();
	    key.mSize = size;
	    key.mKey = value.charAt(count);
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
