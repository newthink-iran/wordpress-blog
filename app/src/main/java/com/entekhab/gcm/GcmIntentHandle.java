package com.entekhab.gcm;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.entekhab.LatestPosts;
import com.entekhab.PostDetails;
import com.entekhab.R;

/**
 * This class Handles what to do when the user presses the NotificationFragment
 */
public class GcmIntentHandle extends ActionBarActivity {
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		
        String todo = getIntent().getStringExtra("todo");
        
        if(todo.equals("newPost")) {
        	newPost();
        }else if(todo.equals("updatePost")) {
        	updatePost();
        }else if(todo.equals("message")) {
        	message();
        }
    }
    
    private void newPost() {
    	String title = getIntent().getStringExtra("post_title");
    	String url = getIntent().getStringExtra("post_url");
    	String id = getIntent().getStringExtra("post_id");
    	String author = getIntent().getStringExtra("post_author");
    	
    	Intent intent = new Intent(GcmIntentHandle.this, PostDetails.class);
 		intent.putExtra("post_title", title);
 		intent.putExtra("post_url", url);
 		intent.putExtra("post_id", id);
 		intent.putExtra("post_author", author);
 		startActivity(intent);
 		finish();
    }
    
    private void updatePost() {
    	String title = getIntent().getStringExtra("post_title");
    	String url = getIntent().getStringExtra("post_url");
    	String id = getIntent().getStringExtra("post_id");
    	String author = getIntent().getStringExtra("post_author");
    	
    	Intent intent = new Intent(GcmIntentHandle.this, PostDetails.class);
 		intent.putExtra("post_title", title);
 		intent.putExtra("post_url", url);
 		intent.putExtra("post_id", id);
 		intent.putExtra("post_author", author);
 		startActivity(intent);
 		finish();
    }
    
    private void message() {
    	
    	String ms = getIntent().getStringExtra("msg");
    	AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setTitle(getString(R.string.title_gcm_alert));
		alertDialogBuilder
			.setMessage(ms)
			.setCancelable(false)
			.setPositiveButton(getString(R.string.btn_ok),new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					dialog.cancel();
					Intent i = new Intent(GcmIntentHandle.this, LatestPosts.class);
					startActivity(i);
					finish();
				}});
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
    }
}
