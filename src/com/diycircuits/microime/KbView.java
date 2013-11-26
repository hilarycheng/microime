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
import android.util.Log;
import android.graphics.Typeface;

public class KbView extends View {

    private Drawable mNormal = null;
    private Paint mPaint;
    private FontMetricsInt mFmi;
    private int mNumRows = 4;
    private int mNumCols = 10;
    private Typeface tf = Typeface.create("Droid Sans",Typeface.BOLD);

    public KbView(Context context, AttributeSet attrs) {
    	super(context, attrs);
    	mNormal = getResources().getDrawable(R.drawable.btn_keyboard_key_ics);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
	mPaint.setTypeface(tf);
        mFmi = mPaint.getFontMetricsInt();
    }
   
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    	int mKbWidth  = SystemParams.getInstance().getWidth();
    	int mKbHeight = SystemParams.getInstance().getHeight();
        setMeasuredDimension(mKbWidth, mKbHeight);
    }

    protected void onDraw(Canvas canvas) {
	Keyboard keyboard = SystemParams.getInstance().getKeyboard("CANGJIE");

	for (int row = 0; row < keyboard.getRow(); row++) {
	    KeyRow keyRow = keyboard.getRow(row);
	    for (int col = 0; col < keyRow.getColumn(); col++) {
		Key key = keyRow.getKey(col);

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
