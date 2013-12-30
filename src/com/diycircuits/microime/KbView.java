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
import android.widget.TextView;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.view.Display;
import android.view.WindowManager;
import android.view.Gravity;
import android.view.ViewTreeObserver;
import android.view.MotionEvent;
import android.util.Log;
import android.util.SparseArray;
import android.graphics.Typeface;
import java.util.ArrayList;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.Gravity;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.TypedValue;
import android.view.ViewGroup.LayoutParams;

public class KbView extends View {

    private final static int KEY_HOLD_ENOUGH = 1;
    
    private Drawable mNormal = null;
    private Drawable mSpacePreviewIcon = null;
    private Paint mPaint;
    private FontMetricsInt mFmi;
    private int mNumRows = 4;
    private int mNumCols = 10;
    private Typeface tf = Typeface.create("Droid Sans",Typeface.BOLD);
    private KeyListener mListener = null;
    private ArrayList<Key> mPressedKey = new ArrayList<Key>();
    private Rect mDirtyBound = new Rect();
    private PopupWindow mPopup = null;
    private TextView mPreviewText = null;
    private final Context mContext;
    private int originalScrollX, mPopupWidth, mPopupHeight, mPopupScroll, mPopupTotalScroll = 0;
    private SlidingLocaleDrawable mSliding = null;
    private int mSpacePreviewIconHeight = 0;
    private int mSpaceKeyWidth = 0;
    private boolean mOnTouchDown = false, mPopupShown = false;
    private KeyPressTimer mTimer = null;

    public KbView(Context context, AttributeSet attrs) {
    	super(context, attrs);

	mContext = context;

	mTimer = new KeyPressTimer();
    	mNormal = getResources().getDrawable(R.drawable.btn_keyboard_key_ics);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
	mPaint.setTypeface(tf);
        mFmi = mPaint.getFontMetricsInt();

	mPopup = new PopupWindow(context);
	LayoutInflater inflate = (LayoutInflater)
	    mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	mPreviewText = (TextView) inflate.inflate(R.layout.key_preview, null);
	mPopup.setContentView(mPreviewText);
	mPreviewText.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
			     MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
	mPopup.setBackgroundDrawable(null);
        mPopup.setTouchable(false);
	mSpacePreviewIcon = context.getResources().getDrawable(R.drawable.sym_keyboard_feedback_space);
	mSpacePreviewIconHeight = mSpacePreviewIcon.getIntrinsicHeight();
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

    private void addInputMethod(PopupWindow popup, String s) {
	LinearLayout view = (LinearLayout) popup.getContentView().findViewById(R.id.inputMethodView);
	TextView text = new TextView(view.getContext());
	text.setText(s);
	text.setTextColor(Color.WHITE);
	text.setWidth(mPopupWidth);
	text.setHeight(mPopupHeight);
	text.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
	view.addView(text);
    }

    class KeyPressTimer extends Handler implements Runnable {

	private static final int LONG_PRESS_TIMEOUT1 = 500;
	private int mCount = 0;
	
	public void start() {
            postAtTime(this, SystemClock.uptimeMillis() + LONG_PRESS_TIMEOUT1);
	    mCount = 0;
	}

	public void stop() {
            removeCallbacks(this);
	}
	
	public void run() {
	    if (mOnTouchDown) {
		mCount++;
		if (mCount == 1) {
		    synchronized(mPressedKey) {
			for (int count = 0; count < mPressedKey.size(); count++) {
			    final Key key = mPressedKey.get(count);
			    if (key.mType == KeyType.SPACE) Log.i("MicroIME", "Key " + key.mPressed + " " + key.getPressedDuration() + " " + key.isHold());
			    if (key.mType == KeyType.SPACE && key.mPressed && key.isHoldTimeReach() && !key.isHold()) {
				handleSpace(key, key.mX);
				break;
			    }
			}
		    }
		}
	    }
	}
    }
    
    private void handleSpace(final Key key, final float x) {
	key.setHold();

	mPopupShown = true;

	mPopupWidth = key.mBounds.width();
	mPopupHeight = mSpacePreviewIconHeight;
	mPopupScroll = key.mBounds.width() / 3;

	if (mSliding == null) {
	    mSliding = new SlidingLocaleDrawable(mContext, mContext.getResources(),
						 mSpacePreviewIcon,
						 mPopupWidth, mPopupHeight);
	}
	mPopup.setContentView(mPreviewText);
	mPreviewText.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
			     MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
	mSliding.setBounds(0, 0, mPopupWidth, mPopupHeight);
	mSliding.invalidateSelf();
	mPreviewText.setCompoundDrawables(null, null, null, mSliding);
			
	mPopupHeight =  mContext.getResources().getDimensionPixelSize(R.dimen.key_preview_height);
	mPopupWidth = Math.max(mPreviewText.getMeasuredWidth(), key.mBounds.width()
			       + mPreviewText.getPaddingLeft() + mPreviewText.getPaddingRight());
	mPopup.setWidth(mPopupWidth);
	mPopup.setHeight(mPopupHeight);
	LayoutParams lp = mPreviewText.getLayoutParams();
	if (lp != null) {
	    lp.width = mPopupWidth;
	    lp.height = mPopupHeight;
	}
	mPreviewText.setVisibility(VISIBLE);

	int px = (int) (key.mBounds.right - key.mBounds.left - mPopupWidth) / 2 + key.mBounds.left;
	int py = (int) key.mBounds.top - mPopupHeight + SystemParams.getInstance().getKeyboardOffset();

	key.mPopupShown = true;
	
	mPopup.showAtLocation((View) this, Gravity.NO_GRAVITY, px, py);
	originalScrollX = (int) x;
	mSpaceKeyWidth = mPopupWidth;
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
	super.onTouchEvent(event);

	// Log.i("MicroIME", "TouchEvent " + event);
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
		    key.mPopupShown = false;
		    mOnTouchDown = true;
		    key.mX = event.getX();
		    mPressedKey.add(key);
		    key.setPressed();
		    invalidate(key.mBounds);
		    mTimer.start();
		} else if (event.getAction() == MotionEvent.ACTION_MOVE && mListener != null) {
		    // if (key.mType == KeyType.SPACE) {
		    // 	if (key.isHoldTimeReach() && !key.isHold()) handleSpace(key, event.getX());

		    // boolean mHasPopup = false;
		    // synchronized(mPressedKey) {
		    // 	for (int i = 0; i < mPressedKey.size(); i++) {
		    // 	    if (mPressedKey.get(i).mPopupShown) {
		    // 		mHasPopup = true;
		    // 		break;
		    // 	    }
		    // 	}
		    // }
		    if (mPopupShown && mPopup != null) {
			mSliding.setDiff(((int) event.getX() - originalScrollX));
			mSliding.invalidateSelf();
		    }
		} else if (event.getAction() == MotionEvent.ACTION_UP && mListener != null) {
		    mOnTouchDown = false;
		    mTimer.stop();
		    /*
		    boolean isKeyHold = false;
		    if (key.isHold()) {
			isKeyHold = true;
			key.setRelease();
			mPopup.dismiss();
			invalidate();
			if (mSpaceKeyWidth > 0) {
			    Log.i("MicroIME", "Which Method " + Math.abs(((int) event.getX() - originalScrollX)) + " " + (mSpaceKeyWidth * 0.8));
			    if ((float) Math.abs(((int) event.getX() - originalScrollX)) >= (float) (mSpaceKeyWidth * 0.8)) {
				if (KeyboardState.getInstance().getKeyboardIndex() == 0)
				    KeyboardState.getInstance().setKeyboardIndex(1);
				else if (KeyboardState.getInstance().getKeyboardIndex() == 1)
				    KeyboardState.getInstance().setKeyboardIndex(0);
				invalidate();
			    }
			}
		    }
		    key.setRelease();
		    mDirtyBound.set(key.mBounds);
		    synchronized(mPressedKey) {
			for (int i = 0; i < mPressedKey.size(); i++) {
			    final Key pkey = mPressedKey.get(i);
			    mDirtyBound.union(pkey.mBounds);
			    if (pkey.isHold()) {
				pkey.setRelease();
				mPopup.dismiss();
				invalidate();
			    }
			    pkey.setRelease();
			}
			mPressedKey.clear();
		    }
		    */

		    boolean mHasPopup = false;
		    Key popupShown = null;
		    synchronized(mPressedKey) {
			for (int i = 0; i < mPressedKey.size(); i++) {
			    final Key pkey = mPressedKey.get(i);
			    mHasPopup = pkey.mPopupShown;
			    if (mHasPopup) popupShown = pkey;
			    pkey.setRelease();
			    mDirtyBound.union(pkey.mBounds);
			}
		    }

		    if (mHasPopup) {
			if (popupShown != null && popupShown.mType == KeyType.SPACE && mSpaceKeyWidth > 0) {
			    mPopup.dismiss();
			    mPopupShown = false;
			    Log.i("MicroIME", "Which Method " + Math.abs(((int) event.getX() - originalScrollX)) + " " + (mSpaceKeyWidth * 0.6));
			    if ((float) Math.abs(((int) event.getX() - originalScrollX)) >= (float) (mSpaceKeyWidth * 0.6)) {
				if (KeyboardState.getInstance().getKeyboardIndex() == 0)
				    KeyboardState.getInstance().setKeyboardIndex(1);
				else if (KeyboardState.getInstance().getKeyboardIndex() == 1)
				    KeyboardState.getInstance().setKeyboardIndex(0);
				invalidate();
			    }
			}
			invalidate();
		    } else {
			invalidate(mDirtyBound);
			if (KeyboardState.getInstance().getShift() == 1 && key.mShift[0] != 0) {
			    mListener.keyPressed(key.mShift[0], key.mType);
			} else {
			    mListener.keyPressed(key.mKey[0], key.mType);
			}
		    }
		}
		return true;
	    }
	}

	if (event.getAction() == MotionEvent.ACTION_UP) {
	    if (mPopup != null) {
		mPopup.dismiss();
	    }
	    synchronized(mPressedKey) {
		for (int i = 0; i < mPressedKey.size(); i++) {
		    final Key key = mPressedKey.get(i);
		    if (i == 0) 
			mDirtyBound.set(key.mBounds);
		    else mDirtyBound.union(key.mBounds);
		    if (key.isHold()) {
			key.setRelease();
			mPopup.dismiss();
			invalidate();
		    }
		    key.setRelease();
		}
		mPressedKey.clear();
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
		    key.mType == KeyType.SYMBOLS ||
		    key.mType == KeyType.MORESYMBOLS ||
		    key.mType == KeyType.COMMA) {

		    mPaint.setColor(Color.WHITE);
		    mPaint.setTextSize(key.mFontSize);
		    mPaint.setTextAlign(Paint.Align.CENTER);
		    if (KeyboardState.getInstance().getShift() == 1 && key.mShift[0] != 0) {
			canvas.drawText(key.mShift, 0, 1, key.mMainX, key.mMainY, mPaint);
		    } else {
			canvas.drawText(key.mKey, 0, key.mKeyLen, key.mMainX, key.mMainY, mPaint);
		    }

		    if (key.mAlt[0] != 0) {
			mPaint.setTextSize(key.mAltFontSize);
			// mPaint.setTextAlign(Paint.Align.RIGHT);
			canvas.drawText(key.mAlt, 0, 1, key.mAltX, key.mAltY, mPaint);
		    }
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
