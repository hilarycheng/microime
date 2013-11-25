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

    private int mNumColumns[] = {
	10, 9, 7, 10
    };
    private char mKeys[][] = {
	{ 'Q', 'W', 'E', 'R', 'T', 'Y', 'U', 'I', 'O', 'P' },
	{ 'A', 'S', 'D', 'F', 'G', 'H', 'J', 'K', 'L',   0 },
	{ 'Z', 'X', 'C', 'V', 'B', 'N', 'M',   0,   0,   0 },
	{   0,   0,   0,   0,   0,   0,   0,   0,   0,   0 },
    };
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

    private final Rect mTextBounds = new Rect();

    protected void onDraw(Canvas canvas) {
    	int mKbWidth  = SystemParams.getInstance().getWidth();
    	int mKbHeight = SystemParams.getInstance().getHeight();

	Keyboard keyboard = SystemParams.getInstance().getKeyboard("CANGJIE");
	
	final float keyHeight = (float) mKbHeight / (float) keyboard.getRow();
    	final float keyYMargin = (float) (keyHeight * 0.05);
    	float keyYStart = 0;
    	float keyXStart = 0;
	int rowNum = 0;
	int colNum = 0;
	char c[] = new char[4];

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

		mNormal.setBounds((int) (x + keyXMargin),
				  (int) (y + keyYMargin),
				  (int) (x + keyWidth - keyXMargin),
				  (int) (y + keyHeight - keyYMargin));
		mNormal.draw(canvas);
		if (key.mType == KeyType.NORMAL ||
		    key.mType == KeyType.DOT ||
		    key.mType == KeyType.INPUT_METHOD ||
		    key.mType == KeyType.COMMA) {

		    mPaint.setTextSize(56);
		    int len = 1;
		    if (key.mType == KeyType.INPUT_METHOD) {
			c[0] = '?';
			c[1] = '1';
			c[2] = '2';
			c[3] = '3';
			len = 4;
			mPaint.setTextSize(48);
		    } else if (key.mType == KeyType.COMMA) {
			c[0] = ',';
		    } else if (key.mType == KeyType.DOT) {
			c[0] = '.';
		    } else {
			c[0] = key.mKey;
		    }

		    mPaint.setColor(Color.WHITE);

		    float mx = x + (keyWidth - mPaint.measureText(c, 0, len)) / 2.0f;
		    int fontHeight = mFmi.bottom - mFmi.top;
		    float my = (keyHeight - fontHeight) / 2.0f;
		    my = y + keyYMargin + my - mFmi.top + mFmi.bottom / 1.5f;
		    canvas.drawText(c, 0, len, mx, my + 1, mPaint);
		} else {
		    final Drawable icon = SystemParams.getInstance().getIcon(key.mType);
		    if (icon != null) {
			int marginLeft   = (int) ((keyWidth - icon.getIntrinsicWidth()) / 2);
			int marginRight  = (int) (keyWidth - icon.getIntrinsicWidth() - marginLeft);
			int marginTop    = (int) ((keyHeight - icon.getIntrinsicHeight()) / 2);
			int marginBottom = (int) (keyHeight - icon.getIntrinsicHeight() - marginTop);
			icon.setBounds((int) (x + marginLeft), (int) (y + marginTop), (int) (x + keyWidth - marginRight), (int) (y + keyHeight - marginBottom));
			icon.draw(canvas);
		    }
		}
		x += keyWidth;
		colNum++;
	    }
	    rowNum++;
	}

	/*
    	float keyHeight = (float) mKbHeight / (float) mNumRows;
    	float keyWidth = (float) mKbWidth / (float) mNumCols;
    	float keyXMargin = (float) (keyWidth * 0.05);
    	float keyYMargin = (float) (keyHeight * 0.05);

    	int keyXStart = 0;
    	int keyYStart = 0;
    	int index = 0;
    	int colXMargin = 0;
    	int column = 0;
    	int row = 0;
    	char c[] = new char[1];

    	for (int y = keyYStart; y < mKbHeight; y += keyHeight) {
    		if (index < mNumColumns.length)
    			colXMargin = (int) (mKbWidth - mNumColumns[index] * keyWidth) / 2;
    		else
    			colXMargin = 0;
    		column = 0;
    		for (int x = keyXStart + colXMargin; x < mKbWidth; x += keyWidth) {
    			if (index < mNumColumns.length) {
    				if (column >= mNumColumns[index]) {
    					continue;
    				}
    			}
    			mNormal.setBounds((int) (x + keyXMargin),
    					(int) (y + keyYMargin),
    					(int) (x + keyWidth - keyXMargin),
    					(int) (y + keyHeight - keyYMargin));
    			mNormal.draw(canvas);
    			if (index < mNumColumns.length) {
    				c[0] = mKeys[row][column];
    				if (c[0] != 0) {
    					mPaint.setColor(Color.WHITE);
    					mPaint.setTextSize(40);

    					float mx = x + (keyWidth - mPaint.measureText(c, 0, 1)) / 2.0f;
    					int fontHeight = mFmi.bottom - mFmi.top;
    					float my = (keyHeight - fontHeight) / 2.0f;
    					my = y + keyYMargin + my - mFmi.top + mFmi.bottom / 1.5f;
    					canvas.drawText(c, 0, 1, mx, my + 1, mPaint);
    				}

    			}
    			column++;
    		}
    		index = index + 1;
    		row++;
    	}
	*/
    }

}
