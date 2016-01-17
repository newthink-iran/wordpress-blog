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

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.gson.Gson;
import com.entekhab.adapter.CategoriesAdapter;
import com.entekhab.model.category.Categories;
import com.entekhab.model.category.Category;
import com.entekhab.utils.ConnectionDetector;

/**
 * This class shows a list with all the categories from your blog
 * 
 * @author Pixelart Web and App Development
 * @since 1.0
 */
public class CategoriesIndex extends BaseActivity {
	
	List<Category> catList = null, finalList = null;
	Categories categories;
	CategoriesAdapter adapter;
	ListView catListView = null;
	ConnectionDetector cd;
	SwipeRefreshLayout swipeLayout;
	InterstitialAd interstitial;
	String baseurl, api, url;
		
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_category,1);
		
		// Admob
		boolean admob = getResources().getBoolean(R.bool.admob);
		if(admob == true) {
			interstitial = new InterstitialAd(CategoriesIndex.this);
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
		
		getSupportActionBar().setTitle(R.string.title_category_list);
	
		baseurl = getString(R.string.blogurl);
		api = getString(R.string.api);
		url = baseurl + api+"/get_category_index";
		
		// Swipe refresher settings
		swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
		swipeLayout.setColorSchemeResources(R.color.primary, R.color.flat_blue, R.color.flat_green, R.color.flat_red);
		swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				if(isCon()) {
					new DownloadCategoriesTask().execute(url);
				}else {
					cd.makeAlert();
				}
			}
		});
		
		// download content
		if(isCon()) {
			swipeLayout.post(new Runnable() {
				@Override
				public void run() {	
					swipeLayout.setRefreshing(true);
					new DownloadCategoriesTask().execute(url);
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
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	public void updateList() {		
		finalList = catList;
		adapter = new CategoriesAdapter(this, finalList);
		catListView= (ListView) findViewById(R.id.cat_list);
		catListView.setVisibility(View.VISIBLE);
		catListView.setAdapter(adapter);
		catListView.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> a, View v, int position,	long id) {
				Category category = (Category) finalList.get(position);
				
				Intent intent = new Intent(CategoriesIndex.this, CategoryPosts.class);
				intent.putExtra("category_title", category.getTitle());
				intent.putExtra("category_id", String.valueOf(category.getId()));
				startActivity(intent);
			}
		});
		
		catListView.setOnScrollListener(new AbsListView.OnScrollListener() {
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
	}

	private class DownloadCategoriesTask extends AsyncTask<String, Integer, Void> {

		@Override
        protected void onPreExecute() {
			swipeLayout.setEnabled(true);
			swipeLayout.setRefreshing(true);
        }

		@Override
		protected void onPostExecute(Void result) {
			if (null != catList) {
				updateList();
				swipeLayout.setRefreshing(false);
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
	        	 categories = gson.fromJson(reader, Categories.class);
	        	 catList = new ArrayList<Category>();
	        	   for (Category category : categories.getCategories()) {
	        		   catList.add(category);
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
