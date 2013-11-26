package com.diycircuits.microime;

import android.content.res.Configuration;
import android.content.Context;
import android.view.Display;
import android.view.WindowManager;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.util.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;

public class SystemParams {

    private static SystemParams mInstance = new SystemParams();
    private int mKbWidth  = 0;
    private int mKbHeight = 0;
    private boolean mKeyboardLoaded = false;
    private HashMap<String, Keyboard> mKeyboard = new HashMap<String, Keyboard>();
    private HashMap<KeyType, Drawable> mKeyIcon = new HashMap<KeyType, Drawable>();
    private Typeface tf = Typeface.create("Droid Sans",Typeface.BOLD);
    private Paint mPaint;
    private FontMetricsInt mFmi;

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
	if (!mKeyIcon.containsKey(type)) return null;
	return mKeyIcon.get(type);
    }
    
    public void configurationChanged(Configuration newConfig, Context context) {
    	WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    	Display d = wm.getDefaultDisplay();

    	int screenWidth = d.getWidth();
    	int screenHeight = d.getHeight();

    	mKbWidth = screenWidth;
    	mKbHeight = (int) (screenHeight * 0.35);

	if (!mKeyboardLoaded) {
	    mKeyboardLoaded = true;

	    mKeyIcon.put(KeyType.DELETE, context.getResources().getDrawable(R.drawable.sym_keyboard_delete_holo));
	    mKeyIcon.put(KeyType.SHIFT, context.getResources().getDrawable(R.drawable.sym_keyboard_shift_holo));
	    mKeyIcon.put(KeyType.ACTION, context.getResources().getDrawable(R.drawable.sym_keyboard_return_holo));
	    mKeyIcon.put(KeyType.SPACE, context.getResources().getDrawable(R.drawable.sym_keyboard_space_holo));
	    
	    loadKeyboardXml(context, R.xml.qwerty);
	    loadKeyboardXml(context, R.xml.cangjie);
	    loadKeyboardXml(context, R.xml.stroke);

	    Set<String> keys = mKeyboard.keySet();
	    for (String key: keys) {
		Keyboard keyboard = mKeyboard.get(key);
		calculateKeyboard(keyboard);
	    }
	}
    }

    private void calculateKeyboard(Keyboard keyboard) {
	final float keyHeight = (float) mKbHeight / (float) keyboard.getRow();
    	final float keyYMargin = (float) (keyHeight * 0.05);
    	float keyYStart = 0;
    	float keyXStart = 0;
	int rowNum = 0;
	int colNum = 0;
	char c[] = new char[4];
	int states[] = new int[1];
	int normal_states[] = new int[0];
	
	for (float y = keyYStart; y < mKbHeight; y += keyHeight) {
	    if (rowNum >= keyboard.getRow()) continue;
	    final KeyRow row = keyboard.getRow(rowNum);
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

		if (key.mType == KeyType.ACTION) {
		    key.mStates[0] = android.R.attr.state_active;
		} else if (key.mType == KeyType.INPUT_METHOD || key.mType == KeyType.DELETE || key.mType == KeyType.SHIFT) {
		    key.mStates[0] = android.R.attr.state_single;
		} else {
		    key.mStates[0] = 0;
		}

		if (key.mType == KeyType.NORMAL ||
		    key.mType == KeyType.DOT ||
		    key.mType == KeyType.INPUT_METHOD ||
		    key.mType == KeyType.COMMA) {

		    key.mFontSize = 56;
		    if (key.mType == KeyType.INPUT_METHOD) {
			key.mFontSize = 48;
		    }
		    mPaint.setTextSize(key.mFontSize);
		    float mx = x + (keyWidth - mPaint.measureText(key.mKey, 0, key.mKeyLen)) / 2.0f;
		    int fontHeight = mFmi.bottom - mFmi.top;
		    float my = (keyHeight - fontHeight) / 2.0f;
		    my = y + keyYMargin + my - mFmi.top + mFmi.bottom / 1.5f;

		    key.mMainX = mx;
		    key.mMainY = my + 1;
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
		    Log.i("MicroIME", "XRP Attr " + tag);
		    if (tag.compareTo("keyboard") == 0) {
			name = getXmlString(xrp, "name", "");
			keyboard.setName(name);
		    } else if (tag.compareTo("row") == 0) {
			if (row != null) keyboard.addRow(row);
			row = new KeyRow();
			row.setOffset(getXmlSize(xrp, "offset", 0.0));
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

	    mKeyboard.put(name, keyboard);

	    Log.i("MicroIME", "Key " + mKeyboard);
	} catch (org.xmlpull.v1.XmlPullParserException ex) {
	    Log.i("MicroIME", "LoadKeyboardXml", ex);
	} catch (java.io.IOException ex) {
	    Log.i("MicroIME", "LoadKeyboardXml", ex);
	}
    }

    public String getXmlString(XmlResourceParser xrp, String name, String defValue) {
	int resId = xrp.getAttributeResourceValue(null, name, 0);
	if (resId == 0) {
	    Log.i("MicroIME", "getXmlString " + resId + " " + name + " " + xrp.getAttributeValue(null, name));
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

    public Keyboard getKeyboard(String name) {
	if (!mKeyboard.containsKey(name))
	    return null;

	return mKeyboard.get(name);
    }
    
    public int getWidth() {
    	return mKbWidth;
    }

    public int getHeight() {
    	return mKbHeight;
    }

}
