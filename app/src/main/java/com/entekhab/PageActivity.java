package com.entekhab;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebChromeClient.CustomViewCallback;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.gson.Gson;
import com.entekhab.model.page.Page;
import com.entekhab.model.page.SinglePage;
import com.entekhab.utils.ConnectionDetector;
import com.entekhab.utils.ExpandableHeightGridView;

/** Example of a Page Activity
 * 
 * @author Deniz
 * @since 1.5
 */
public class PageActivity extends BaseActivity {
	
	SwipeRefreshLayout swipeLayout;
	ConnectionDetector cd;
	InterstitialAd interstitial;
	FrameLayout fullVideo;
	ScrollView mainView;
	MyChromeClient mClient;
	CustomViewCallback mCustomViewCallback;
	ExpandableHeightGridView tagsGridView, categoriesGridView;
	View tempView;
	SinglePage spa;
	Page pa;
	Bundle saveState;
	WebView content;
	TextView head;
	String newContent, url, baseurl, title;
	String api = "?json=1&include=content,title,tags,categories";
	
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_page, 2);
		
		saveState = savedInstanceState;
		
		baseurl = getString(R.string.blogurl);
		url = baseurl+"demopage"+api;
		
		// Admob
		boolean admob = getResources().getBoolean(R.bool.admob);
		if(admob == true) {
			interstitial = new InterstitialAd(this);
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
		
		// Swipe refresher settings
		swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
		swipeLayout.setColorSchemeResources(R.color.primary, R.color.flat_blue, R.color.flat_green, R.color.flat_red);
		swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				if(isCon()) {
					new DownloadPageTask().execute(url);
				}else {
					cd.makeAlert();
				}
			}
		});
		
		if(isCon()) {
			swipeLayout.post(new Runnable() {
				@Override
				public void run() {	
					swipeLayout.setRefreshing(true);
					new DownloadPageTask().execute(url);
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
	
	private void updatePage() {
		mainView = (ScrollView) findViewById(R.id.main_view);
		fullVideo = (FrameLayout) findViewById(R.id.fullvideo_view);
		head = (TextView) findViewById(R.id.page_headline);
		content = (WebView) findViewById(R.id.page_content);
		tagsGridView = (ExpandableHeightGridView) findViewById(R.id.grid_tags);
		categoriesGridView = (ExpandableHeightGridView) findViewById(R.id.grid_categories);
		
		head.setText(Html.fromHtml(title));
		
		// delete these if you want to show images in post content
		boolean sif = getResources().getBoolean(R.bool.showImageFragment);
		if(newContent != null && sif) {
	    	Document document = Jsoup.parse(newContent);
		    document.select("dl").remove();
		    document.select("br").remove();
		    document.select("img").remove();
	    	newContent = document.toString();
		}		
		
		content.loadDataWithBaseURL("http://", newContent, "text/html", "UTF-8", null);
		WebSettings ws = content.getSettings();
		ws.setJavaScriptEnabled(true);
		ws.setLoadWithOverviewMode(true);
		ws.setUseWideViewPort(false);
		mClient = new MyChromeClient();
		content.setWebChromeClient(mClient);
		content.saveState(saveState);
		
		
		// hide tags and categories
		categoriesGridView.setVisibility(View.GONE);
		TextView categoriesTitle = (TextView) findViewById(R.id.page_title_categories);
		categoriesTitle.setVisibility(View.GONE);
		
		tagsGridView.setVisibility(View.GONE);
		TextView tagsTitle = (TextView) findViewById(R.id.page_title_tags);
		tagsTitle.setVisibility(View.GONE);
	}
	
	
	
	
	protected class DownloadPageTask extends AsyncTask<String, Integer, Void> {

		@Override
        protected void onPreExecute() {
			swipeLayout.setEnabled(true);
			swipeLayout.setRefreshing(true);
        }

		@Override
		protected void onPostExecute(Void result) {
			swipeLayout.setRefreshing(false);
			updatePage();
		}

		@Override
		protected Void doInBackground(String... params) {
			String uri = params[0];   
			uri = uri.replaceAll(" ", "%20");
	         try {
	        	 InputStream source = retrieveStream(uri);
	 	         Gson gson = new Gson();
	        	 Reader reader = new InputStreamReader(source);  
	        	 spa = gson.fromJson(reader, SinglePage.class);
				 pa = spa.getPage();
				 newContent = pa.getContent();
				 title = pa.getTitle();
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
	
	// Custom ChromeClient for html5 video support
	class MyChromeClient extends WebChromeClient {
		@Override
	    public void onShowCustomView(View view, CustomViewCallback callback) {
	        mCustomViewCallback = callback;
	        fullVideo.addView(view);
	        tempView = view;
	        mainView.setVisibility(View.GONE);
	        fullVideo.setVisibility(View.VISIBLE);
	        fullVideo.bringToFront();
	    }
	 
	    @Override
	    public void onHideCustomView() {
	        if (tempView == null) {
	            return;
	        }
	 
	        tempView.setVisibility(View.GONE);
	        fullVideo.removeView(tempView);
	        tempView = null;
	        fullVideo.setVisibility(View.GONE);
	        mCustomViewCallback.onCustomViewHidden();
	        mainView.setVisibility(View.VISIBLE);
	    }
	}
	
	public void onBackPressed(){
	    if (tempView != null){
	        mClient.onHideCustomView();
	    }
	}
}
