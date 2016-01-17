package com.entekhab.utils;

import com.entekhab.R;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebView;

/**
 * This class displays the full changelog
 * 
 * @author Pixelart Web and App Development
 * @since 1.0
 */
public class dialogChangelog extends DialogPreference {

	private WebView wv;  
	
	public dialogChangelog(Context oContext, AttributeSet attrs){
        super(oContext, attrs);  
        setDialogLayoutResource(R.layout.dialog);
    }
	
	@Override
	protected void onBindDialogView(View view) {		
	    wv = (WebView) view.findViewById(R.id.dialogView);
	    wv.loadUrl("file:///android_asset/changelog.html");
	    super.onBindDialogView(view);
	}
}
