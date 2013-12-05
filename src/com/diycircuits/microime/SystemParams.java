package com.diycircuits.microime;

import android.content.res.Configuration;
import android.content.Context;
import android.view.Display;
import android.view.WindowManager;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.util.Log;
import java.util.ArrayList;
import java.util.Set;

import android.util.SparseArray;
import android.util.TypedValue;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;

public class SystemParams {

    private static SystemParams mInstance = new SystemParams();
    private int mKbWidth  = 0;
    private int mKbHeight = 0;
    private int mKbCandidateHeight = 0;
    private int mKbOffset = 0;
    private boolean mKeyboardLoaded = false;
    private SparseArray<Keyboard> mKeyboard = new SparseArray<Keyboard>();
    private SparseArray<Drawable> mKeyIcon = new SparseArray<Drawable>();
    private Typeface tf = Typeface.create("Droid Sans",Typeface.BOLD);
    private Paint mPaint;
    private FontMetricsInt mFmi;
    private Rect mTextBounds = new Rect();

    private SystemParams() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
	mPaint.setTypeface(tf);
        mFmi = mPaint.getFontMetricsInt();
    }
    
    public static SystemParams getInstance() {
	return mInstance;
    }

    public Drawable getIcon(KeyType type) {
	if (mKeyIcon.keyAt(type.ordinal()) < 0) return null;
	return mKeyIcon.get(type.ordinal());
    }
    
    public void configurationChanged(Configuration newConfig, final Context context) {
    	WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    	Display d = wm.getDefaultDisplay();

    	int screenWidth = d.getWidth();
    	int screenHeight = d.getHeight();

    	mKbWidth = screenWidth;
    	mKbHeight = (int) (screenHeight * 0.35);
    	// mKbHeight = (int) (screenHeight * 0.45);

	mKbOffset = context.getResources().getDimensionPixelSize(R.dimen.suggestions_strip_height);
	// mKbCandidateHeight = mKbHeight;
	// mKbHeight = mKbHeight - mKbOffset;
	mKbCandidateHeight = mKbHeight + mKbOffset;

	if (!mKeyboardLoaded) {
	    mKeyboardLoaded = true;

	    mKeyIcon.put(KeyType.DELETE.ordinal(), context.getResources().getDrawable(R.drawable.sym_keyboard_delete_holo));
	    mKeyIcon.put(KeyType.SHIFT.ordinal(), context.getResources().getDrawable(R.drawable.sym_keyboard_shift_holo));
	    mKeyIcon.put(KeyType.ACTION.ordinal(), context.getResources().getDrawable(R.drawable.sym_keyboard_return_holo));
	    mKeyIcon.put(KeyType.SPACE.ordinal(), context.getResources().getDrawable(R.drawable.sym_keyboard_space_holo));
	    mKeyIcon.put(KeyType.TAB.ordinal(), context.getResources().getDrawable(R.drawable.sym_keyboard_tab_holo));
	    
	    loadKeyboardXml(context, R.xml.qwerty);
	    loadKeyboardXml(context, R.xml.cangjie);
	    loadKeyboardXml(context, R.xml.quick);
	    loadKeyboardXml(context, R.xml.stroke);
	    loadKeyboardXml(context, R.xml.symbol);
	    loadKeyboardXml(context, R.xml.moresymbol);

	    for (int i = 0; i < mKeyboard.size(); i++) {
		Keyboard keyboard = mKeyboard.get(mKeyboard.keyAt(i));
	     	calculateKeyboard(keyboard, context);
	    }
	}
    }

    private void calculateKeyboard(Keyboard keyboard, final Context context) {
	float keyHeight = (float) mKbHeight / (float) keyboard.getRow();
    	float keyYMargin = (float) (keyHeight * 0.05);
    	float keyYStart = 0;
    	float keyXStart = 0;
	int rowNum = 0;
	int colNum = 0;
	char c[] = new char[4];
	int states[] = new int[1];
	int normal_states[] = new int[0];
	
	for (float y = keyYStart; y < mKbHeight + keyYStart;) {
	    if (rowNum >= keyboard.getRow()) continue;
	    final KeyRow row = keyboard.getRow(rowNum);

	    keyHeight = (float) (mKbHeight * row.getSize());
	    keyYMargin = (float) (keyHeight * 0.05);

	    keyXStart = (float) mKbWidth * (float) row.getOffset();
	    float x = keyXStart;
	    colNum = 0;
	    while (colNum < row.getColumn()) {
		final Key key = row.getKey(colNum);
		final float keyWidth = (float) mKbWidth * (float) key.mSize;
		final float keyXMargin = (float) (keyWidth * 0.008);

		key.mBounds.set((int) (x + keyXMargin),
				(int) (y + keyYMargin),
				(int) (x + keyWidth - keyXMargin),
				(int) (y + keyHeight - keyYMargin));

		row.mBounds.union(key.mBounds);

		if (key.mType == KeyType.ACTION) {
		    key.mStates[0] = android.R.attr.state_active;
		} else if (key.mType == KeyType.INPUT_METHOD || key.mType == KeyType.DELETE || key.mType == KeyType.SHIFT || key.mType == KeyType.TAB) {
		    key.mStates[0] = android.R.attr.state_single;
		} else {
		    key.mStates[0] = 0;
		}

		if (key.mType == KeyType.NORMAL ||
		    key.mType == KeyType.DOT ||
		    key.mType == KeyType.INPUT_METHOD ||
		    key.mType == KeyType.COMMA) {

		    // key.mFontSize = context.getResources().getDimensionPixelSize(R.dimen.textSize);
		    key.mFontSize = (int) (keyHeight * 0.40);
		    if (key.mType == KeyType.INPUT_METHOD) {
			// key.mFontSize = context.getResources().getDimensionPixelSize(R.dimen.textSizeSymbol);
			key.mFontSize = (int) (keyHeight * 0.30);
		    }
		    mPaint.setTextSize(key.mFontSize);
		    mPaint.getTextBounds(key.mKey, 0, key.mKeyLen, mTextBounds);
		    final float textWidth = mTextBounds.width();
		    final float textHeight = mTextBounds.height();

		    float mx = x + keyWidth / 2;
		    // float my = (keyHeight - textWidth) / 2.0f;
		    // my = y + keyYMargin + my - mFmi.top + mFmi.bottom / 1.5f;
		    float my = y + (keyHeight / 2) + textHeight / 2;

		    key.mMainX = mx;
		    key.mMainY = my;
		} else {
		    final Drawable icon = getIcon(key.mType);
		    if (icon != null) {
			int marginLeft   = (int) ((keyWidth - icon.getIntrinsicWidth()) / 2);
			int marginRight  = (int) (keyWidth - icon.getIntrinsicWidth() - marginLeft);
			int marginTop    = (int) ((keyHeight - icon.getIntrinsicHeight()) / 2);
			int marginBottom = (int) (keyHeight - icon.getIntrinsicHeight() - marginTop);
			key.mIconBounds = new Rect();
			key.mIconBounds.set((int) (x + marginLeft), (int) (y + marginTop), (int) (x + keyWidth - marginRight), (int) (y + keyHeight - marginBottom));
		    }
		}
		x += keyWidth;
		colNum++;
	    }
	    rowNum++;
	    y += keyHeight;
	}
    }
    
    private void loadKeyboardXml(Context context, int id) {
	Keyboard keyboard = new Keyboard();

	KeyRow row = null;
	String name = null;
	XmlResourceParser xrp = context.getResources().getXml(id);
	try {
	    int xmlEvent = xrp.next();

	    while (xmlEvent != XmlResourceParser.END_DOCUMENT) {
		if (xmlEvent == XmlResourceParser.START_TAG) {
		    String tag = xrp.getName();
		    if (tag.compareTo("keyboard") == 0) {
			name = getXmlString(xrp, "name", "");
			keyboard.setName(name);
		    } else if (tag.compareTo("row") == 0) {
			if (row != null) keyboard.addRow(row);
			row = new KeyRow();
			row.setOffset(getXmlSize(xrp, "offset", 0.0));
			row.setSize(getXmlSize(xrp, "size", 0.0));
		    } else if (tag.compareTo("keys") == 0) {
			double size = getXmlSize(xrp, "size", 0.0); 
			String value = getXmlString(xrp, "value", "");
			row.addKeys(size, value);
		    } else if (tag.compareTo("key") == 0) {
			double size = getXmlSize(xrp, "size", 0.0); 
			String value = getXmlString(xrp, "type", "");
			row.addKey(size, value);
		    }
		}
		xmlEvent = xrp.next();
	    }
	    if (row != null) keyboard.addRow(row);

	    mKeyboard.append(id, keyboard);

	} catch (org.xmlpull.v1.XmlPullParserException ex) {
	    Log.i("MicroIME", "LoadKeyboardXml", ex);
	} catch (java.io.IOException ex) {
	    Log.i("MicroIME", "LoadKeyboardXml", ex);
	}
    }

    public String getXmlString(XmlResourceParser xrp, String name, String defValue) {
	int resId = xrp.getAttributeResourceValue(null, name, 0);
	if (resId == 0) {
	    return xrp.getAttributeValue(null, name);
	} else {
	    return defValue;
	}
    }

    public double getXmlSize(XmlResourceParser xrp, String name, double defValue) {
	int resId = xrp.getAttributeResourceValue(null, name, 0);
	if (resId == 0) {
	    String s = xrp.getAttributeValue(null, name);
	    if (s == null) return defValue;
	    if (s.endsWith("%")) {
		return Double.parseDouble(s.substring(0, s.length() - 1)) / 100.0;
	    }
	}

	return defValue;
    }

    public Keyboard getKeyboard(int id) {
	if (mKeyboard.indexOfKey(id) < 0)
	    return null;

	return mKeyboard.get(id);
    }
    
    public int getWidth() {
    	return mKbWidth;
    }

    public int getHeight() {
    	return mKbHeight;
    }

    public int getHeightWithCandidate() {
    	return mKbCandidateHeight;
    }

    public int getKeyboardOffset() {
	return mKbOffset;
    }

}
