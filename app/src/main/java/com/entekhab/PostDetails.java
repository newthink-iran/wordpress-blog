package com.entekhab;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.entekhab.fragments.DetailsFragment;

/**
 * PostDetails is the mother of all the Post related Views and Fragments
 * 
 * @author Pixelart Web and App Development
 * @since 1.0
 */
public class PostDetails extends ActionBarActivity {
	
	String url = null;
	String title;
	String id;
	boolean isPostFav = false;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post);
		
		getSupportFragmentManager().beginTransaction()
        	.add(R.id.postframe, new DetailsFragment())
        	.commit();
		
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle("");
		getSupportActionBar().setHomeButtonEnabled(true);
		
		// get the strings from the previous activity via intent
		url = getIntent().getStringExtra("post_url");
		title = getIntent().getStringExtra("post_title");
		id = getIntent().getStringExtra("post_id");
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.details, menu);
		
		SharedPreferences pref = getSharedPreferences("PREFERENCE_APP", 0);
		String favs = pref.getString("Favs" , "-1");
		if (favs.contains(id)){
			isPostFav = true;
			MenuItem nofav = menu.findItem(R.id.menu_not_fav);
			nofav.setVisible(false);
		}else{
			isPostFav = false;
			MenuItem fav = menu.findItem(R.id.menu_fav);
			fav.setVisible(false);
		}
		return true;
	}
	
	// Menu items onClick
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		 switch (item.getItemId()) {
			case android.R.id.home:
				onBackPressed();
				return true;
	        case R.id.menu_share:
	        	shareContent();
	            return true;
	        case R.id.menu_web:
	        	Intent i = new Intent(Intent.ACTION_VIEW);
	        	i.setData(Uri.parse(url));
	        	startActivity(i);
	        	return true;
	        case R.id.menu_fav:
	        	isPostFav = false;
	        	removeFav();
	        	supportInvalidateOptionsMenu();
	        	return true;
	        case R.id.menu_not_fav:
	        	isPostFav = true;
	        	addFav();
	        	supportInvalidateOptionsMenu();
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	// add post id to favlist
	private void addFav() {
		SharedPreferences pref = getSharedPreferences("PREFERENCE_APP", 0);
		String favs = pref.getString("Favs" , null);
		if (favs != null){
		String newfav = favs+id+",";
		SharedPreferences.Editor editor = pref.edit();
		editor.putString("Favs", newfav);
		editor.commit();
		}else{
			String newfav = id+",";
			SharedPreferences.Editor editor = pref.edit();
			editor.putString("Favs", newfav);
			editor.commit();
		}
		Toast.makeText(this, getString(R.string.msg_fav_added) ,Toast.LENGTH_SHORT).show();
	}
	
	// Remove the Id of the post from the favlist
	private void removeFav(){
		SharedPreferences pref = getSharedPreferences("PREFERENCE_APP", 0);
		String favs = pref.getString("Favs" , null);
		String actfav =id+ ",";
		String editfav = favs.replace(actfav, "");
		SharedPreferences.Editor editor = pref.edit();
		editor.putString("Favs", editfav);
		editor.commit();
		Toast.makeText(this, getString(R.string.msg_fav_removed) ,Toast.LENGTH_SHORT).show();
	}	

	// Prepare the Intent for sharing a post
	private void shareContent() {
		Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(title) + "\n" + url);
		sendIntent.setType("text/plain");
		startActivity(Intent.createChooser(sendIntent, getString(R.string.action_share)));

	}
}
