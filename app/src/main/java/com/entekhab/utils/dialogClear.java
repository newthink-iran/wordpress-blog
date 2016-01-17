package com.entekhab.utils;

import com.entekhab.R;
import com.entekhab.searchProvider;

import android.content.Context;
import android.preference.DialogPreference;
import android.provider.SearchRecentSuggestions;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * @author Pixelart Web and App Development
 *
 * This class clears, if selected, the search history
 */
public class dialogClear extends DialogPreference {

	private Context con;
	
	public dialogClear(Context oContext, AttributeSet attrs){
        super(oContext, attrs);  
        setDialogLayoutResource(R.layout.dialog_clear);
		con = oContext;
    }

	@Override
	protected void onBindDialogView(View view) {
	Button ok = (Button)view.findViewById(R.id.btn_ok);
		ok.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Clear();
					getDialog().dismiss();
				}
			});
		Button exit = (Button)view.findViewById(R.id.btn_exit);
		exit.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
				getDialog().dismiss();
				}
			});
		super.onBindDialogView(view);
	}
	
	private void Clear(){
		SearchRecentSuggestions suggestions = new SearchRecentSuggestions(con,
		       searchProvider.AUTHORITY, searchProvider.MODE);
		suggestions.clearHistory();
	}
}
