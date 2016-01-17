package com.entekhab.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

// Custom ScrollView with Listener implementation
public class ScrollViewX extends ScrollView {
	
	private ScrollViewListener scrollViewListener = null;

	public ScrollViewX(Context context) {
		super(context);
	}
	public ScrollViewX(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public ScrollViewX(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public interface ScrollViewListener {
	    void onScrollChanged(ScrollViewX scrollView, int x, int y, int oldx, int oldy);
	}
	
	public void setScrollViewListener(ScrollViewListener scrollViewListener) {
        this.scrollViewListener = scrollViewListener;
    }
	
	@Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        super.onScrollChanged(x, y, oldx, oldy);
        if(scrollViewListener != null) {
            scrollViewListener.onScrollChanged(this, x, y, oldx, oldy);
        }
    }
}
