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
import android.view.Display;
import android.view.WindowManager;
import android.view.MotionEvent;
import android.util.Log;
import android.graphics.Typeface;

public class KbView extends View {

    private Drawable mNormal = null;
    private Paint mPaint;
    private FontMetricsInt mFmi;
    private int mNumRows = 4;
    private int mNumCols = 10;
    private Typeface tf = Typeface.create("Droid Sans",Typeface.BOLD);
    private KeyListener mListener = null;

    public KbView(Context context, AttributeSet attrs) {
    	super(context, attrs);
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

	// Log.i("MicroIME", "OnTouchEvent " + event);
	final Keyboard keyboard = KeyboardState.getInstance().getCurrentKeyboard();
	for (int row = 0; row < keyboard.getRow(); row++) {
	    boolean inter = keyboard.getRow(row).mBounds.contains((int) event.getX(), (int) event.getY());
	    if (!inter) continue;
	    final KeyRow keyRow = keyboard.getRow(row);
	    for (int col = 0; col < keyRow.getColumn(); col++) {
		final Key key = keyRow.getKey(col);
		inter = key.mBounds.contains((int) event.getX(), (int) event.getY());
		if (!inter) continue;
		// Log.i("MicroIME", "OnTouchEvent " + event + " " + row + " " + col + " " + inter + " " + key.mKey[0]);
		if (event.getAction() == MotionEvent.ACTION_UP && mListener != null) {
		    mListener.keyPressed(key.mKey[0], key.mType);
		}
		return true;
	    }
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
