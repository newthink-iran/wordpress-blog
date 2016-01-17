package com.entekhab;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Random;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.entekhab.utils.ShakeDetector;
import com.entekhab.utils.ShakeDetector.OnShakeListener;

/** Extend from this Activity to display the navigation drawer on the left side
 * 
 * @author Pixelart Web and App Development
 */
public class BaseActivity extends ActionBarActivity{
	
	DrawerLayout mDrawerLayout;
	View devi;
    ListView mDrawerList;
    ActionBarDrawerToggle mDrawerToggle;
    protected FrameLayout contentLayout;
    int id = 0;
    ArrayList<String> idList;
    AlertDialog ad;
    boolean isTablet = false;
	
    /** Set The ContentView and the position in the menu list
     * 
     * @param layoutResID
     * @param position
     */
    public void setContentView(final int layoutResID, int id) {
    	this.id = id;
    	devi = (View) getLayoutInflater().inflate(R.layout.activity_base, null);
    	
    	Toolbar toolbar = (Toolbar) devi.findViewById(R.id.toolbar);
    	setSupportActionBar(toolbar);
    	
    	mDrawerLayout= (DrawerLayout) devi.findViewById(R.id.drawer_layout);
    	contentLayout= (FrameLayout) devi.findViewById(R.id.content_frame);
    	getLayoutInflater().inflate(layoutResID, contentLayout, true);
    	super.setContentView(devi);
    	
    	// Set this to true to display an icon or logo
    	getSupportActionBar().setDisplayShowHomeEnabled(true);
    	// Set this to false to hide text title
    	getSupportActionBar().setDisplayShowTitleEnabled(true);
    	
    	if(((ViewGroup.MarginLayoutParams)contentLayout.getLayoutParams()).leftMargin == 240) {
			isTablet = true;
			supportInvalidateOptionsMenu();
		}else {
			isTablet = false;
			supportInvalidateOptionsMenu();
		}
		
		if(isTablet) {
			mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
			mDrawerLayout.setScrimColor(getResources().getColor(R.color.transparent));
			getSupportActionBar().setDisplayHomeAsUpEnabled(false);
			getSupportActionBar().setHomeButtonEnabled(false);
			
		}else if(!isTablet) {
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	        getSupportActionBar().setHomeButtonEnabled(true);
	        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
			mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.drawer_open,
					R.string.drawer_close) {

				public void onDrawerClosed(View view) {
					supportInvalidateOptionsMenu();
				}

				public void onDrawerOpened(View drawerView) {
					supportInvalidateOptionsMenu();
				}
			};

	        mDrawerLayout.setDrawerListener(mDrawerToggle);
	        mDrawerToggle.setDrawerIndicatorEnabled(true);
		}
    	
    	mDrawerList = (ListView) findViewById(R.id.left_drawer);
        String[] menu = getResources().getStringArray(R.array.menu);
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.row_drawer_menu, menu));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        mDrawerList.setItemChecked(id, true);
        
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(BaseActivity.this);
        if(pref.getBoolean("isDeviceShake", true) == true){
			ShakeDetector.create(this, new OnShakeListener() {
	       	    public void OnShake() {
	       	        	randomPost();
	       	    }
	       	 });
        }
	
    }
    
    public void setItemChecked() {
    	mDrawerList.setItemChecked(id, true);  
    }
	
	@Override
	public void onResume(){
		super.onResume();
		ShakeDetector.start();
		mDrawerList.setItemChecked(id, true);
	}
	
	@Override
	protected void onStop() {
         super.onStop();
         ShakeDetector.stop();
     }
	
	@Override
     protected void onDestroy() {
         super.onDestroy();
         ShakeDetector.destroy();
     }
	
	public void displayDrawerToggle(boolean ddt) {
		if(ddt) {
			mDrawerToggle.setDrawerIndicatorEnabled(true);
		}else if(!ddt) {
			mDrawerToggle.setDrawerIndicatorEnabled(false);
		}
	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
	    super.onPostCreate(savedInstanceState);
	    if(!isTablet) {
			mDrawerToggle.syncState();
	    }
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);
	    if(!isTablet) {
			mDrawerToggle.onConfigurationChanged(newConfig);
	    }
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    if (!isTablet && mDrawerToggle.onOptionsItemSelected(item)) {
	       return true;
	    }
	    return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return true;
	}	
	
	private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        	switch (position) {

        	case 0:
        		Intent latest = new Intent(BaseActivity.this, LatestPosts.class);
        		startActivity(latest);
        		if(!isTablet) {
					mDrawerLayout.closeDrawer(mDrawerList);
        		}
				break;
        	case 1:
        		Intent categories = new Intent(BaseActivity.this, CategoriesIndex.class);
        		startActivity(categories);
        		if(!isTablet) {
					mDrawerLayout.closeDrawer(mDrawerList);
        		}
        		break;
        	case 2:
        		Intent page = new Intent(BaseActivity.this, PageActivity.class);
        		startActivity(page);
        		if(!isTablet) {
					mDrawerLayout.closeDrawer(mDrawerList);
        		}
        		break;
        	case 3:
        		Intent bookmarks = new Intent(BaseActivity.this, BookmarkPosts.class);
        		startActivity(bookmarks);
        		if(!isTablet) {
					mDrawerLayout.closeDrawer(mDrawerList);
        		}
        		break;
			case 4:
				Intent settings = new Intent(BaseActivity.this, SettingsActivity.class);
				startActivity(settings);
				if(!isTablet) {
					mDrawerLayout.closeDrawer(mDrawerList);
				}
				break;
        	}
        } };	  
        
        
    /**
     * RANDOM POST FUNCTIONS
     */
    private void randomPost() {
    	LayoutInflater inflater = getLayoutInflater();
    	View dialoglayout = inflater.inflate(R.layout.dialog_shake, null);
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setView(dialoglayout);
    	ad = builder.create();
    	ad.show();
    	
    	String param = "/get_posts/?include=id&count=999999";
    	String api = getString(R.string.api);
    	String url = getString(R.string.blogurl);
    	
    	url = url+api+param;
    	new GetIdsTask().execute(url);
    }
    
    private class GetIdsTask extends AsyncTask<String, Integer, Void> {

		@Override
		protected void onProgressUpdate(Integer... values) {
		}

		@Override
		protected void onPostExecute(Void result) {
			if (null != idList) {
				Random r = new Random();
				int index = r.nextInt(idList.size());
				String pId = idList.get(index);
				fetchPost(pId);
			}
		}

		@Override
		protected Void doInBackground(String... params) {
			String url = params[0];
			JSONObject json = getJSONFromUrl(url);
			
			try {
				
		        if (json.getString("status").equalsIgnoreCase("ok")) {
		            JSONArray posts = json.getJSONArray("posts");

		            idList = new ArrayList<String>();

		            for (int i = 0; i < posts.length(); i++) {
		                JSONObject post = (JSONObject) posts.getJSONObject(i);
		                String id = post.getString("id");

		                idList.add(id);
		            }
		        }
		        } catch (JSONException e) {
		        e.printStackTrace();
		    }
	
			return null;
		}
	}
    
    public JSONObject getJSONFromUrl(String url) {
		InputStream is = null;
		JSONObject jObj = null;
		String json = null;

		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);

			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();
			is = httpEntity.getContent();

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			json = sb.toString();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			jObj = new JSONObject(json);
		} catch (JSONException e) {}

		return jObj;
	}
    
    private void fetchPost(String id) {
    	String url = getString(R.string.blogurl);
    	String api = getString(R.string.api);
    	String include = getString(R.string.include);
    	String param = "/get_post/?id="+id+"&include="+include;
    	
    	url = url+api+param;
    	new GetPostTask().execute(url);
    }
    
    private class GetPostTask extends AsyncTask<String, Integer, Void> {
    	
    	String[] postData;

		@Override
		protected void onProgressUpdate(Integer... values) {
		}

		@Override
		protected void onPostExecute(Void result) {			
			Intent intent = new Intent(BaseActivity.this, PostDetails.class);
			intent.putExtra("post_url", postData[0]);
			intent.putExtra("post_title", postData[1]);
			intent.putExtra("post_id", postData[2]);
			intent.putExtra("post_com_status", postData[3]);
			intent.putExtra("post_author", postData[4]);
			startActivity(intent);
			
			ad.cancel();	
		}

		@Override
		protected Void doInBackground(String... params) {
			String url = params[0];
			JSONObject json = getJSONFromUrl(url);
			
			try {
				
		        if (json.getString("status").equalsIgnoreCase("ok")) {
		            JSONObject post = json.getJSONObject("post");
		                String id = post.getString("id");
		                String title = post.getString("title");
		                String uri = post.getString("url");
		                String com_status = post.getString("comment_status");
		                
		                JSONObject aut = post.getJSONObject("author");
		                String author = aut.getString("name");
		                
		              String[]  postData = { uri, title, id, com_status, author, };
		              this.postData = postData;
		        }
	        } catch (JSONException e) {
		        e.printStackTrace();
		    }
			return null;
		}
	}   
}
