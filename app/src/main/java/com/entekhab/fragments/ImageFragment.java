package com.entekhab.fragments;

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

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.gson.Gson;
import com.entekhab.PostDetails;
import com.entekhab.R;
import com.entekhab.adapter.ImgSliderAdapter;
import com.entekhab.model.page.Page;
import com.entekhab.model.page.SinglePage;
import com.entekhab.model.singlepost.Attachment;
import com.entekhab.model.singlepost.Post;
import com.entekhab.model.singlepost.SinglePost;
import com.entekhab.utils.ConnectionDetector;
import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.PageIndicator;

/**
 * This Fragment loads and shows the user all the image attachments.
 * When the user clicks on an image, it opens the FullScreenImage class 
 * to show the image in fullscreen.
 * 
 * @author Pixelart Web and App Development
 * @since 1.0
 */
public class ImageFragment extends Fragment {
    
	ProgressDialog pd;
	ConnectionDetector cd;
	ViewPager imgPager;
	ImgSliderAdapter imgAdapter;
	PageIndicator indicator;
	InterstitialAd interstitial;
	String param = "?json=1&include=attachments";
	String url;
	String postType;
	GridView imgListView;
	TextView nopic;
	View v;
	SinglePost spo;
	Post po;
	SinglePage spa;
	Page pa;
	ArrayList<String> fulls;
	ArrayList<Attachment> imgList= null;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		getActivity().setTheme(R.style.BaseTheme);
		
		ColorDrawable cd = new ColorDrawable(Color.parseColor(getString(R.color.primary)));
		((PostDetails)getActivity()).getSupportActionBar().setBackgroundDrawable(cd);
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		
		v = inflater.inflate(R.layout.fragment_img, null);        
        
		url = getActivity().getIntent().getStringExtra("post_url");
		postType = getActivity().getIntent().getStringExtra("post_type");
		
		// Admob
		boolean admob = getResources().getBoolean(R.bool.admob);
		if(admob == true) {
			interstitial = new InterstitialAd(getActivity());
			interstitial.setAdUnitId(getString(R.string.ad_unit_1));
			AdView adView = new AdView(getActivity());
			adView.setAdSize(AdSize.BANNER);
			adView.setAdUnitId(getString(R.string.ad_unit_1));
			LinearLayout layout = (LinearLayout) v.findViewById(R.id.adLayout);
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
		
		
		// chek internet connection
		cd = new ConnectionDetector(getActivity());
		if(isCon() == true){
			checkAutoLoad();
		}else{
			cd.makeAlert();
		}
		
        return v;
    }
	
	// Cheks if user wants to download images also on mobile connection
	private void checkAutoLoad() {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
		if(pref.getBoolean("isAutoloadImage", true) == false && cd.isMobile() == true) {
			// Do nothing, because user set no auto loading media on mobile connection
		}else{
     		new DownloadImgTask().execute(url+param);
		}
	}
	
	private Boolean isCon() {
		cd = new ConnectionDetector(getActivity());
		return cd.isConnectingToInternet();
	}
	
	
	// update the gridview with the images
	public void updateGrid() {
		nopic = (TextView) v.findViewById(R.id.tv_no_pic);
		
		if(imgList != null && imgList.size() > 0) {
			nopic.setVisibility(View.GONE);
			
			imgAdapter = new ImgSliderAdapter(getActivity(), imgList, getActivity().getSupportFragmentManager());
			imgPager = (ViewPager) v.findViewById(R.id.img_pager);
			imgPager.setAdapter(imgAdapter);
			indicator = (CirclePageIndicator) v.findViewById(R.id.indicator);
	        indicator.setViewPager(imgPager);
	        ((CirclePageIndicator) indicator).setSnap(true);
		
		}else {
			nopic.setText(getString(R.string.no_image));
		}
	}
	
	// download the images from your blog and save them
	private class DownloadImgTask extends AsyncTask<String, Integer, Void> {

		@Override
        protected void onPreExecute() {
            pd = ProgressDialog.show(getActivity(), "", getString(R.string.wait));
        }

		@Override
		protected void onPostExecute(Void result) {
				updateGrid();
				pd.dismiss();
		}

		@Override
		protected Void doInBackground(String... params) {
			String url = params[0];
			try {
				InputStream source = retrieveStream(url);
		        Gson gson = new Gson();        
		        Reader reader = new InputStreamReader(source);        
		        if(postType == "page") {
		        	spa  = gson.fromJson(reader, SinglePage.class);
		        	pa = spa.getPage();
		        	List<Attachment> atts = pa.getAttachments();
		        	fulls = new ArrayList<String>();
		        	for(int i = 0; i < atts.size(); i++) {
		        		Attachment att = (Attachment) atts.get(i);
		     	        fulls.add(att.getUrl());
		        	}
		        	imgList = new ArrayList<Attachment>();
		        	for(Attachment att: pa.getAttachments()) {
		        		imgList.add(att);
		        	}
		        }else {
		        	spo  = gson.fromJson(reader, SinglePost.class);
	        		po = spo.getPost();
		        	List<Attachment> atts = po.getAttachments();
		        	fulls = new ArrayList<String>();
			        for(int i = 0; i < atts.size(); i++) {
			        	Attachment att = (Attachment) atts.get(i);
			     	    fulls.add(att.getUrl());
			        }
			        	imgList = new ArrayList<Attachment>();
			        	for(Attachment att: po.getAttachments()) {
			        		imgList.add(att);
			        	}
		        }
	            reader.close();
			}catch (Exception e) { }
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
			}catch (IOException e) {
				getRequest.abort();
				Log.w("WPBA", "Error for URL " + url, e);
			}
		return null;
	   }
	}
}
