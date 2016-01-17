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

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.Button;
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
 * Handles the results of a search request
 * 
 * @author Pixelart Web and App Development
 * @since 1.0
 */
public class SearchResultsActivity extends ActionBarActivity {

	List<Posts> postsList = null, finalList = null;
	AllPosts recentposts;
	PostsAdapter adapter;
	ListView postsListView = null;
	ConnectionDetector cd;
	SwipeRefreshLayout swipeLayout;
	InterstitialAd interstitial;
	View footer;
	TextView tv, num;
	Button more;
	Context con;
	int page = 1, total_pages = 2;
	boolean isMore = false;
	String baseurl, api, dateformat, include, url, query, title;
	String urlparam = "/get_search_results/?post_type=post&search=";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.list_posts_2);
	
		// Enabled ToolBar and set it as ActionBar
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        
        dateformat = getString(R.string.dateformat);
    	include = getString(R.string.include);
    	baseurl = getString(R.string.blogurl);
    	api = getString(R.string.api);
    	
    	// Admob
    	boolean admob = getResources().getBoolean(R.bool.admob);
		if(admob == true) {
			interstitial = new InterstitialAd(SearchResultsActivity.this);
			interstitial.setAdUnitId(getString(R.string.ad_unit_1));
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
        
    	// Add header and footer to the ListView
        postsListView = (ListView) findViewById(R.id.posts_list);
		footer =  ((LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.list_footer, null, false);
		more = (Button) footer.findViewById(R.id.btn_more);
		postsListView.addFooterView(footer);
		
		// Set the font from assets as the title font, to get it look like google play
		LinearLayout header = new LinearLayout(this);
		Typeface tf = Typeface.createFromAsset(getAssets(),"italic.ttf");
		num = new TextView(this);
		num.setId(2);
		num.setTypeface(tf,Typeface.NORMAL);
		num.setTextSize(20);
		header.addView(num);
		postsListView.addHeaderView(header);
        
		handleIntent(getIntent());
    }
    
    private Boolean isCon() {
		cd = new ConnectionDetector(this);
		return cd.isConnectingToInternet();
	}

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.posts_list, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				onBackPressed();
				return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

	// handles the search intent
    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
			getSupportActionBar().setTitle(getString(R.string.title_search)+" "+query);
            url = baseurl+api+urlparam+query+"&date_format="+dateformat+"&include="+include;
            url.replaceAll(" ", "%20");
			
			SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,																  
			searchProvider.AUTHORITY, searchProvider.MODE);
			suggestions.saveRecentQuery(query, null);
			
			// Swipe refresher settings
			swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
			swipeLayout.setColorSchemeResources(R.color.primary, R.color.flat_blue, R.color.flat_green, R.color.flat_red);
			swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
				@Override
				public void onRefresh() {
					if(isCon()) {
						new DownloadPostsTask().execute(url);
					}else {
						cd.makeAlert();
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
    }
	
    // update the list with the results
	public void updateList() {
		if(title.equals("0")){
		  num.setText(getString(R.string.no_search_result));
		  more.setVisibility(View.GONE);
		}else {
		  num.setText(getString(R.string.search_result_1)+" "+title+" "+getString(R.string.search_result_2));
		}
				
		finalList = postsList;
		adapter = new PostsAdapter(this, finalList);
		postsListView.setVisibility(View.VISIBLE);
		postsListView.setAdapter(adapter);
		postsListView.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> a, View v, int position,	long id) {
				Posts posts = (Posts) postsListView.getItemAtPosition(position);
				Author author = (Author) posts.getAuthor();

					Intent intent = new Intent(SearchResultsActivity.this, PostDetails.class);
					intent.putExtra("post_url", posts.getUrl());
					intent.putExtra("post_title", posts.getTitle());
					intent.putExtra("post_id", String.valueOf(posts.getId()));
					intent.putExtra("post_com_status", posts.getCommentStatus());
					intent.putExtra("post_author", author.getName());
					intent.putExtra("post_type", posts.getType());
					startActivity(intent);
			}
		});
		
		more.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				page++;
				isMore = true;
				new DownloadPostsTask().execute(url+"&page="+page);
			}
		});
	}
	
	private void additems() {
	    for(Posts posts : postsList) {
				adapter.add(posts);
		}
	}
	
	// donwloads the post results from search
	private class DownloadPostsTask extends AsyncTask<String, Integer, Void> {

		@Override
        protected void onPreExecute() {
			swipeLayout.setEnabled(true);
			swipeLayout.setRefreshing(true);
        }

		@Override
		protected void onPostExecute(Void result) {
			swipeLayout.setRefreshing(false);
			
			if(page == total_pages){
				more.setEnabled(false);
			    more.setText(getString(R.string.msg_last_page));
			}
			
			if (postsList != null) {			
				if(isMore == true){
					additems();
				}else{
					updateList();
				}
			}
		}

		@Override
		protected Void doInBackground(String... params) {
			String uri = params[0];   
			uri = uri.replaceAll(" ", "%20");
	        try {
	        	 InputStream source = retrieveStream(uri);
	 	         Gson gson = new Gson();
	        	 Reader reader = new InputStreamReader(source);  
	        	 recentposts = gson.fromJson(reader, AllPosts.class);
	        	 total_pages = recentposts.getPages();
	        	 title = String.valueOf(recentposts.getCountTotal());
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
