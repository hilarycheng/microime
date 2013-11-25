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

public class SystemParams {

    private static SystemParams mInstance = new SystemParams();
    private int mKbWidth  = 0;
    private int mKbHeight = 0;
    private boolean mKeyboardLoaded = false;
    private HashMap<String, Keyboard> mKeyboard = new HashMap<String, Keyboard>();
    private HashMap<KeyType, Drawable> mKeyIcon = new HashMap<KeyType, Drawable>();

    private SystemParams() {
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
