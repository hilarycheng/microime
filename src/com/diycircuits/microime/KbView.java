package com.diycircuits.microime;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class KbView extends RelativeLayout {

    public KbView(Context context, AttributeSet attrs) {
    	super(context, attrs);
    }
    
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        widthMeasureSpec = MeasureSpec.makeMeasureSpec(1024,
                MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(600,
                MeasureSpec.EXACTLY);

    	super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

}
