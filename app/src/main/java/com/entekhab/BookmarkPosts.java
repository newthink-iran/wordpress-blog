package com.entekhab;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.gson.Gson;
import com.entekhab.adapter.PostsAdapter;
import com.entekhab.model.posts.AllPosts;
import com.entekhab.model.posts.Author;
import com.entekhab.model.posts.Posts;
import com.entekhab.utils.ConnectionDetector;

/**
 * This Activity displays the bookmarked posts
 * 
 * @author Pixelart Web and App Development
 * @since 1.0
 */
public class BookmarkPosts extends BaseActivity {
	
	List<Posts> postsList = null, finalList = null;
	AllPosts recentposts;
	PostsAdapter adapter;
	ListView postsListView = null;
	TextView no_bookmark;
	ConnectionDetector cd;
	InterstitialAd interstitial;
	SwipeRefreshLayout swipeLayout;
	Context con;
	String baseurl, api, dateformat, include, favs = null, url = null;
	String[] allfavs;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_bookmarks,3);
		
		// Admob
		boolean admob = getResources().getBoolean(R.bool.admob);
		if(admob == true) {
			interstitial = new InterstitialAd(BookmarkPosts.this);
			interstitial.setAdUnitId(getString(R.string.ad_unit_2));
			AdView adView = new AdView(this);
			adView.setAdSize(AdSize.BANNER);
			adView.setAdUnitId(getString(R.string.ad_unit_1));
			LinearLayout layout = (LinearLayout) findViewById(R.id.adLayout);
			layout.setVisibility(View.VISIBLE);
			layout.addView(adView);
	        AdRequest adRequest = new AdRequest.Builder().build();
	        adView.loadAd(adRequest);
	        interstitial.loadAd(adRequest);
	        interstitial.setAdListener(new AdListener() {
				public void onAdLoaded() {
					if (interstitial.isLoaded()) {
						interstitial.show();
					}
				}
			});
		}
		
		baseurl = getString(R.string.blogurl);
		api = getString(R.string.api);
		dateformat = getString(R.string.dateformat);
		include = getString(R.string.include);
		
		url = baseurl+ api +"/get_posts/?date_format="+dateformat+"&include="+include;
		
		getSupportActionBar().setTitle(getString(R.string.title_bookmark_posts));
		
		postsListView = (ListView) findViewById(R.id.posts_list);
		no_bookmark = (TextView) findViewById(R.id.no_bookmark);
		
		/* GetBookmarks and add them to the url */
		SharedPreferences pref = getSharedPreferences("PREFERENCE_APP", 0);
		favs = pref.getString("Favs" , null);
		if(favs != null) {
			allfavs = favs.split(",");
		
			for(int i=0; i<allfavs.length; i++){
				url = url+"&post__in[]="+ allfavs[i]+",";
				url = url.replaceAll(" ", "%20");
			}
		}
		
		// Swipe refresher settings
		swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
		swipeLayout.setColorSchemeResources(R.color.primary, R.color.flat_blue, R.color.flat_green, R.color.flat_red);
		swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				if(favs == null || favs.length() == 0) {
					no_bookmark.setVisibility(View.VISIBLE);
				}else {
				   if(isCon()) {
					  new DownloadPostsTask().execute(url);
				   }else {
					  cd.makeAlert();
				   }
				}
			}
		});
		
		postsListView.setOnScrollListener(new AbsListView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				if(firstVisibleItem == 0) {
					swipeLayout.setEnabled(true);
				}else {
					swipeLayout.setEnabled(false);
				}
			}
		});
		
		// download content
		if(isCon()) {
			swipeLayout.post(new Runnable() {
				@Override
				public void run() {	
					swipeLayout.setRefreshing(true);
					new DownloadPostsTask().execute(url);
				}
			});
		}else {
			cd.makeAlert();
		}
		
	}
	
	private Boolean isCon() {
		cd = new ConnectionDetector(this);
		return cd.isConnectingToInternet();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.posts_list, menu);
		
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}


	public void updateList() {
		finalList = postsList;
		adapter = new PostsAdapter(this, finalList);
		postsListView.setVisibility(View.VISIBLE);
		postsListView.setAdapter(adapter);
		postsListView.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> a, View v, int position,	long id) {
				Posts posts = (Posts) finalList.get(position);
				Author author = (Author) posts.getAuthor();
				
				Intent intent = new Intent(BookmarkPosts.this, PostDetails.class);
				intent.putExtra("post_url", posts.getUrl());
				intent.putExtra("post_title", posts.getTitle());
				intent.putExtra("post_id", String.valueOf(posts.getId()));
				intent.putExtra("post_com_status", posts.getCommentStatus());
				intent.putExtra("post_author", author.getName());
				startActivity(intent);
			}
		});
	}
	

	protected class DownloadPostsTask extends AsyncTask<String, Integer, Void> {

		@Override
        protected void onPreExecute() {
			swipeLayout.setEnabled(true);
			swipeLayout.setRefreshing(true);
        }

		@Override
		protected void onPostExecute(Void result) {
			swipeLayout.setRefreshing(false);
			if (postsList != null){
					updateList();
			}
		}

		@Override
		protected Void doInBackground(String... params) {
			String uri = params[0];
	           try {
	        	 InputStream source = retrieveStream(uri);
	 	         Gson gson = new Gson();
	        	 Reader reader = new InputStreamReader(source);  
	        	 recentposts = gson.fromJson(reader, AllPosts.class);
	        	 postsList = new ArrayList<Posts>();
	        	   for (Posts posts : recentposts.getPosts()) {
	                     postsList.add(posts);
	               }
                 reader.close();
             } catch (Exception e) {
            	 e.printStackTrace();
             }
	        return null;
	    }
	    
	    private InputStream retrieveStream(String url) {
			DefaultHttpClient client = new DefaultHttpClient(); 
			HttpGet getRequest = new HttpGet(url);
			try {
				HttpResponse getResponse = client.execute(getRequest);
				final int statusCode = getResponse.getStatusLine().getStatusCode();
				if(statusCode != HttpStatus.SC_OK) { 
					Log.w("WPBA", "Error " + statusCode + " for URL " + url); 
					return null;
				}
				HttpEntity getResponseEntity = getResponse.getEntity();
				return getResponseEntity.getContent();
			} 
			catch (IOException e) {
				getRequest.abort();
				Log.w("WPBA", "Error for URL " + url, e);
			}
		return null;
	    }
	}
}
