package com.diycircuits.microime;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.view.Display;
import android.view.WindowManager;

public class KbContainer extends RelativeLayout {

    public KbContainer(Context context, AttributeSet attrs) {
    	super(context, attrs);
    }
    
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    	int mKbWidth  = SystemParams.getInstance().getWidth();
    	int mKbHeight = SystemParams.getInstance().getHeight();
	
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(mKbWidth,
                MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(mKbHeight,
                MeasureSpec.EXACTLY);

    	super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

}
