package com.diycircuits.microime;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.graphics.Rect;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.widget.PopupWindow;
import android.view.Display;
import android.view.WindowManager;
import android.view.MotionEvent;
import android.util.Log;
import android.util.SparseArray;
import android.graphics.Typeface;
import java.util.ArrayList;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.Gravity;

public class KbView extends View {

    private Drawable mNormal = null;
    private Paint mPaint;
    private FontMetricsInt mFmi;
    private int mNumRows = 4;
    private int mNumCols = 10;
    private Typeface tf = Typeface.create("Droid Sans",Typeface.BOLD);
    private KeyListener mListener = null;
    private ArrayList<Key> mPressedKey = new ArrayList<Key>();
    private Rect mDirtyBound = new Rect();
    private PopupWindow mPopup = new PopupWindow();
    private final Context mContext;

    public KbView(Context context, AttributeSet attrs) {
    	super(context, attrs);

	mContext = context;
    	mNormal = getResources().getDrawable(R.drawable.btn_keyboard_key_ics);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
	mPaint.setTypeface(tf);
        mFmi = mPaint.getFontMetricsInt();
    }

    public void setKeyListener(KeyListener listen) {
	mListener = listen;
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    	final int mKbWidth  = SystemParams.getInstance().getWidth();
    	final int mKbHeight = SystemParams.getInstance().getHeight();
        setMeasuredDimension(mKbWidth, mKbHeight);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
	super.onTouchEvent(event);

	Log.i("MicroIME", "TouchEvent " + event);
	final Keyboard keyboard = KeyboardState.getInstance().getCurrentKeyboard();
	for (int row = 0; row < keyboard.getRow(); row++) {
	    boolean inter = keyboard.getRow(row).mBounds.contains((int) event.getX(), (int) event.getY());
	    if (!inter) continue;
	    final KeyRow keyRow = keyboard.getRow(row);
	    for (int col = 0; col < keyRow.getColumn(); col++) {
		final Key key = keyRow.getKey(col);
		inter = key.mBounds.contains((int) event.getX(), (int) event.getY());
		if (!inter) continue;
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
		    mPressedKey.add(key);
		    key.setPressed();
		    invalidate(key.mBounds);
		    if (key.mType == KeyType.SPACE) {
			Log.i("MicroIME", "Key Type Popup");
			LayoutInflater inflate = (LayoutInflater)
			    mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			mPopup = new PopupWindow();
			mPopup.setContentView(inflate.inflate(R.layout.inputmethod, null));
			mPopup.setWidth(100);
			mPopup.setHeight(100);
			mPopup.setSoftInputMode(PopupWindow.INPUT_METHOD_NOT_NEEDED);
			mPopup.showAtLocation((View) this, Gravity.NO_GRAVITY, (int) (key.mBounds.right - key.mBounds.left - 100) / 2 + key.mBounds.left, (int) key.mBounds.top - 100);
		    }
		} else if (event.getAction() == MotionEvent.ACTION_UP && mListener != null) {
		    if (mPopup != null) {
			mPopup.dismiss();
			mPopup = null;
		    }
		    key.setRelease();
		    mDirtyBound.set(key.mBounds);
		    for (int i = 0; i < mPressedKey.size(); i++) {
			mDirtyBound.union(mPressedKey.get(i).mBounds);
			mPressedKey.get(i).setRelease();
		    }
		    invalidate(mDirtyBound);
		    mListener.keyPressed(key.mKey[0], key.mType);
		}
		return true;
	    }
	}

	if (event.getAction() == MotionEvent.ACTION_UP) {
	    if (mPopup != null) {
		mPopup.dismiss();
		mPopup = null;
	    }
	    for (int i = 0; i < mPressedKey.size(); i++) {
		if (i == 0) 
		    mDirtyBound.set(mPressedKey.get(i).mBounds);
		else mDirtyBound.union(mPressedKey.get(i).mBounds);
		mPressedKey.get(i).setRelease();
	    }
	    invalidate(mDirtyBound);
	}
	
	return false;
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
	super.onDraw(canvas);

	final Keyboard keyboard = KeyboardState.getInstance().getCurrentKeyboard();

	for (int row = 0; row < keyboard.getRow(); row++) {
	    final KeyRow keyRow = keyboard.getRow(row);
	    for (int col = 0; col < keyRow.getColumn(); col++) {
		final Key key = keyRow.getKey(col);

		mNormal.setState(key.mStates);
		mNormal.setBounds(key.mBounds);
		mNormal.draw(canvas);

		if (key.mType == KeyType.NORMAL ||
		    key.mType == KeyType.DOT ||
		    key.mType == KeyType.INPUT_METHOD ||
		    key.mType == KeyType.COMMA) {

		    mPaint.setColor(Color.WHITE);
		    mPaint.setTextSize(key.mFontSize);
		    canvas.drawText(key.mKey, 0, key.mKeyLen, key.mMainX, key.mMainY, mPaint);
		} else {
		    final Drawable icon = SystemParams.getInstance().getIcon(key.mType);
		    if (icon != null) {
			icon.setBounds(key.mIconBounds);
			icon.draw(canvas);
		    }
		}
	    }
	}

    }

}
