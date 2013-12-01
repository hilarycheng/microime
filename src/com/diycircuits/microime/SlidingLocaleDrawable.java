package com.diycircuits.microime;

import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import java.util.Locale;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.view.ViewConfiguration;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;

public class SlidingLocaleDrawable extends Drawable {

    private final Context mContext;
    private final Resources mRes;
    private final int mWidth;
    private final int mHeight;
    private final Drawable mBackground;
    private final TextPaint mTextPaint;
    private final int mMiddleX;
    private final Drawable mLeftDrawable;
    private final Drawable mRightDrawable;
    private final int mThreshold;
    
    private int mDiff;
    private boolean mHitThreshold;
    private String mCurrentLanguage;
    private String mNextLanguage;
    private String mPrevLanguage;

    private static final int OPACITY_FULLY_OPAQUE = 255;
    // Height in space key the language name will be drawn. (proportional to space key height)
    private static final float SPACEBAR_LANGUAGE_BASELINE = 0.6f;

    public SlidingLocaleDrawable(Context context, Resources res, Drawable background, int width, int height) {

	mContext = context;
	mRes = res;
	
	mBackground = background;
	setDefaultBounds(mBackground);
	mWidth = width;
	mHeight = height;
	mTextPaint = new TextPaint();
	mTextPaint.setTextSize(getTextSizeFromTheme(android.R.style.TextAppearance_Medium, 18));
	mTextPaint.setColor(R.color.latinkeyboard_transparent);
	mTextPaint.setTextAlign(Align.CENTER);
	mTextPaint.setAlpha(OPACITY_FULLY_OPAQUE);
	mTextPaint.setAntiAlias(true);
	mMiddleX = (mWidth - mBackground.getIntrinsicWidth()) / 2;
	mLeftDrawable =
	    mRes.getDrawable(R.drawable.sym_keyboard_feedback_language_arrows_left);
	mRightDrawable =
	    mRes.getDrawable(R.drawable.sym_keyboard_feedback_language_arrows_right);
	mThreshold = ViewConfiguration.get(mContext).getScaledTouchSlop();
    }

    private void setDefaultBounds(Drawable drawable) {
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
    }

    private int getTextSizeFromTheme(int style, int defValue) {
        TypedArray array = mContext.getTheme().obtainStyledAttributes(
                style, new int[] { android.R.attr.textSize });
        int textSize = array.getDimensionPixelSize(array.getResourceId(0, 0), defValue);
        return textSize;
    }

    public void setDiff(int diff) {
	if (diff == Integer.MAX_VALUE) {
	    mHitThreshold = false;
	    mCurrentLanguage = null;
	    return;
	}
	mDiff = diff;
	if (mDiff > mWidth) mDiff = mWidth;
	if (mDiff < -mWidth) mDiff = -mWidth;
	if (Math.abs(mDiff) > mThreshold) mHitThreshold = true;
	invalidateSelf();
    }

    // private String getLanguageName(Locale locale) {
    // 	return LanguageSwitcher.toTitleCase(locale.getDisplayLanguage(locale), locale);
    // }

    @Override
    public void draw(Canvas canvas) {
	canvas.save();
	if (mHitThreshold) {
	    Paint paint = mTextPaint;
	    final int width = mWidth;
	    final int height = mHeight;
	    final int diff = mDiff;
	    final Drawable lArrow = mLeftDrawable;
	    final Drawable rArrow = mRightDrawable;
	    canvas.clipRect(0, 0, width, height);
	    // if (mCurrentLanguage == null) {
	    // 	final LanguageSwitcher languageSwitcher = mLanguageSwitcher;
	    // 	mCurrentLanguage = getLanguageName(languageSwitcher.getInputLocale());
	    // 	mNextLanguage = getLanguageName(languageSwitcher.getNextInputLocale());
	    // 	mPrevLanguage = getLanguageName(languageSwitcher.getPrevInputLocale());
	    // }
	    mCurrentLanguage = mContext.getString(R.string.qwerty);
	    mNextLanguage    = mContext.getString(R.string.cangjie);
	    mPrevLanguage    = mContext.getString(R.string.cangjie);
	    // mCurrentLanguage = "English";

	    
	    // Draw language text with shadow
	    final float baseline = mHeight * SPACEBAR_LANGUAGE_BASELINE - paint.descent();
	    paint.setColor(mRes.getColor(R.color.latinkeyboard_feedback_language_text));
	    canvas.drawText(mCurrentLanguage, width / 2 + diff, baseline, paint);
	    canvas.drawText(mNextLanguage, diff - width / 2, baseline, paint);
	    canvas.drawText(mPrevLanguage, diff + width + width / 2, baseline, paint);

	    setDefaultBounds(lArrow);
	    rArrow.setBounds(width - rArrow.getIntrinsicWidth(), 0, width,
			     rArrow.getIntrinsicHeight());
	    lArrow.draw(canvas);
	    rArrow.draw(canvas);
	}
	if (mBackground != null) {
	    canvas.translate(mMiddleX, 0);
	    mBackground.draw(canvas);
	}
	canvas.restore();
    }

    @Override
    public int getOpacity() {
	return PixelFormat.TRANSLUCENT;
    }

    @Override
    public void setAlpha(int alpha) {
	// Ignore
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
	// Ignore
    }

    @Override
    public int getIntrinsicWidth() {
	return mWidth;
    }

    @Override
    public int getIntrinsicHeight() {
	return mHeight;
    }
}
