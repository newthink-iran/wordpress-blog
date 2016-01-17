package com.entekhab.utils;

import com.entekhab.R;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebView;

/**
 * Shows the Open Source Licenses dialog
 * 
 * @author Pixelart Web and App Development
 * @since 1.0
 */
public class dialogLicenses extends DialogPreference {

	private WebView wv;  
	
	public dialogLicenses(Context oContext, AttributeSet attrs){
        super(oContext, attrs);  
        setDialogLayoutResource(R.layout.dialog);
    }
	
	@Override
	protected void onBindDialogView(View view) {		
	    wv = (WebView) view.findViewById(R.id.dialogView);
	    wv.loadUrl("file:///android_asset/licenses.html");
	    super.onBindDialogView(view);
	}
}
