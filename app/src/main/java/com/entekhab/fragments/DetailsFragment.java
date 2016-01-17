package com.entekhab.fragments;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.text.Html;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebChromeClient.CustomViewCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.gson.Gson;
import com.entekhab.CategoryPosts;
import com.entekhab.PostDetails;
import com.entekhab.R;
import com.entekhab.TagPosts;
import com.entekhab.adapter.PostCategoriesAdapter;
import com.entekhab.adapter.PostTagsAdapter;
import com.entekhab.model.page.Page;
import com.entekhab.model.page.SinglePage;
import com.entekhab.model.singlepost.Post;
import com.entekhab.model.singlepost.PostCategory;
import com.entekhab.model.singlepost.SingleFull;
import com.entekhab.model.singlepost.SinglePost;
import com.entekhab.model.singlepost.SingleThumbnailImages;
import com.entekhab.model.singlepost.Tag;
import com.entekhab.utils.ConnectionDetector;
import com.entekhab.utils.ExpandableHeightGridView;
import com.entekhab.utils.ImageLoader;
import com.entekhab.utils.ScrollViewX;
import com.entekhab.utils.ScrollViewX.ScrollViewListener;

/**
 *  DetailsFragment new designed to match the latest design guidelines
 * 
 * @author Pixelart Web and App Development
 * @since 1.5
 */
public class DetailsFragment extends Fragment {
	
	View v, tempView, spacer;
	WebView content;
	ExpandableHeightGridView tagsGridView, categoriesGridView;
	FrameLayout fullVideo;
	ScrollViewX mainView;
	MyChromeClient mClient;
	CustomViewCallback mCustomViewCallback;
    ProgressDialog pd;
	ConnectionDetector cd;
	InterstitialAd interstitial;
	String api = "?json=1&include=content,date,tags,categories,thumbnail";
	String url, newContent, title, date, dateformat, author, thumburl;
	String postType = null;
	int img_height, img_width;
	TextView tv_date, tv_author, tv_head;
	Button btn_comments, btn_imgs;
	ImageView topimg;
	SinglePost spo;
	Post po;
	SinglePage spa;
	Page pa;
	ArrayList<Tag> tagsList= null;
	ArrayList<PostCategory> categoriesList= null;
	Bundle saveState;
	boolean isColor = true;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		getActivity().setTheme(R.style.ThemeFading);		
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		saveState = savedInstanceState;
		
		v = inflater.inflate(R.layout.fragment_post, null);
		spacer = (View) v.findViewById(R.id.spacer);
		setAbColor();
		
		dateformat = getString(R.string.dateformat);
		
		url = getActivity().getIntent().getStringExtra("post_url");
		title = getActivity().getIntent().getStringExtra("post_title");
		author = getActivity().getIntent().getStringExtra("post_author");
		postType = getActivity().getIntent().getStringExtra("post_type");
		
		url = url+api+"&date_format="+dateformat;
		url = url.replaceAll(" ","%20");
		
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
		
		// Check for Internet Connection
		if(isCon()) {
		   new DownloadPostTask().execute();
		}else {
		   cd.makeAlert();
		}
		
        return v;
    }
	
	private Boolean isCon() {
		cd = new ConnectionDetector(getActivity());
		return cd.isConnectingToInternet();
	}
	
	View.OnClickListener btnListener = new View.OnClickListener() {
		public void onClick(View v) {
		      switch(v.getId()) {
		      case R.id.btn_imgs:
		    	  getFragmentManager().beginTransaction()
					.replace(R.id.postframe, new ImageFragment())
					.addToBackStack(null)
					.commit();
		    	  break;
		      case R.id.btn_comments:
		    	  getFragmentManager().beginTransaction()
					.replace(R.id.postframe, new CommentFragment())
					.addToBackStack(null)
					.commit();
		    	  break;
		      }
		}
	};
	
	// Fading Actionbar
	private void setAbColor() {
		ActionBar ab = ((PostDetails)getActivity()).getSupportActionBar();
		final ColorDrawable cd = new ColorDrawable(Color.parseColor(getString(R.color.primary)));
		ab.setBackgroundDrawable(cd);
		try {			
			if(!isColor) {
				ab.setElevation(0);
				cd.setAlpha(0);
				mainView.setScrollViewListener(new ScrollViewListener() {
					public void onScrollChanged(ScrollViewX v, int l, int t, int oldl, int oldt) {
						cd.setAlpha(getAlphaforActionBar(v.getScrollY()));
					}
					
					private int getAlphaforActionBar(int scrollY) {
						int minDist = 0, maxDist = topimg.getMeasuredHeightAndState();
						if(scrollY>maxDist) { 
							return 255;
						}else if(scrollY<minDist) {
							return 0;
						}else {
							int alpha = 0;
							alpha = (int)  ((255.0/maxDist)*scrollY);
							return alpha;
						}
					}
				});
			}
		}catch(NullPointerException ex) {}
	}
	
	// Updates the View with the new content
	private void updateContent() {
		mainView = (ScrollViewX) v.findViewById(R.id.main_view);
		fullVideo = (FrameLayout) v.findViewById(R.id.fullvideo_view);
		tagsGridView = (ExpandableHeightGridView) v.findViewById(R.id.grid_tags);
		categoriesGridView = (ExpandableHeightGridView) v.findViewById(R.id.grid_categories);
		content = (WebView) v.findViewById(R.id.post_content);
		topimg = (ImageView) v.findViewById(R.id.topimg);
		tv_head = (TextView) v.findViewById(R.id.post_headline);
		tv_date = (TextView) v.findViewById(R.id.post_date);
		tv_author = (TextView) v.findViewById(R.id.post_author);
		btn_comments = (Button) v.findViewById(R.id.btn_comments);
		btn_imgs = (Button) v.findViewById(R.id.btn_imgs);
		
		btn_imgs.setOnClickListener(btnListener);
		btn_comments.setOnClickListener(btnListener);
		
		// If no thumbnail image is available 
		if(thumburl != null) {
			isColor = false;
			setAbColor();
			
			ImageLoader imageLoader = new ImageLoader(getActivity());
			imageLoader.DisplayImage(thumburl, topimg);
		}else {
			isColor = true;
			topimg.setVisibility(View.GONE);
			spacer.setVisibility(View.VISIBLE);
		}
		
		// The list of Tags
		if(tagsList != null && tagsList.size() >= 1){
			tagsGridView.setVisibility(View.VISIBLE);
			tagsGridView.setAdapter(new PostTagsAdapter(getActivity(), tagsList));
			tagsGridView.setExpanded(true);
			tagsGridView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> a, View v, int position,	long id) {
					Tag tag = (Tag) tagsList.get(position);
					String tagId = String.valueOf(tag.getId());
					String tagTitle = tag.getTitle();
					Intent intent = new Intent(getActivity(), TagPosts.class);
					intent.putExtra("tag_id",  tagId);
					intent.putExtra("tag_title",  tagTitle);
					startActivity(intent);
				}
			});
			
			}else {
				tagsGridView.setVisibility(View.GONE);
				TextView tagsTitle = (TextView) v.findViewById(R.id.post_title_tags);
				tagsTitle.setVisibility(View.GONE);
			}
		
		// The list of Categories
		if(categoriesList != null && categoriesList.size() >= 1) {
			categoriesGridView.setVisibility(View.VISIBLE);
			categoriesGridView.setAdapter(new PostCategoriesAdapter(getActivity(), categoriesList));
			categoriesGridView.setExpanded(true);
			categoriesGridView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> a, View v, int position,	long id) {
					PostCategory category = (PostCategory) categoriesList.get(position);
					String categoryId = String.valueOf(category.getId());
					String categoryTitle = category.getTitle();
					Intent intent = new Intent(getActivity(), CategoryPosts.class);
					intent.putExtra("category_id",  categoryId);
					intent.putExtra("category_title",  categoryTitle);
					startActivity(intent);
				}
			});
			
		}else {
			categoriesGridView.setVisibility(View.GONE);
			TextView categoriesTitle = (TextView) v.findViewById(R.id.post_title_categories);
			categoriesTitle.setVisibility(View.GONE);
		}
		
		
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
		
		// "x time ago" like Date calculation
		boolean timeago = getResources().getBoolean(R.bool.showTimeAgo);
		if(timeago == true) {
			SimpleDateFormat formatter = new SimpleDateFormat("dd. MMM yyyy, H:mm", Locale.US);
			try {
				Date ddate = formatter.parse(date);
				long millis = ddate.getTime();
				date = DateUtils.getRelativeTimeSpanString(millis, System.currentTimeMillis(),
	                    DateUtils.SECOND_IN_MILLIS).toString();
				
			}catch(ParseException e) {
				e.printStackTrace();
			}
		}
		
		tv_date.setText(Html.fromHtml("<b>"+author+"</b>"));
		tv_author.setText(Html.fromHtml("<b>"+date+"</b>"));
		tv_head.setText(Html.fromHtml(title));
	}
	
	
	// Downloads all the content from your blog
	protected class DownloadPostTask extends AsyncTask<String, Integer, Void> {

		@Override
        protected void onPreExecute() {
            pd = ProgressDialog.show(getActivity(), "", getString(R.string.wait));
        }

		@Override
		protected void onPostExecute(Void result) {
				updateContent();
				pd.dismiss();
		}

		@Override
		protected Void doInBackground(String... params) {
			try {
				InputStream source = retrieveStream(url);
				Gson gson = new Gson();
				Reader reader = new InputStreamReader(source);
		       	if(postType != null && postType.equals("page")) {
		       		spa = gson.fromJson(reader, SinglePage.class);
					pa = spa.getPage();
					newContent = pa.getContent();
			       	tagsList = new ArrayList<Tag>();
			        for(Tag tag: pa.getTags()) {
			        	 tagsList.add(tag);
			        }
				}else{
					spo = gson.fromJson(reader, SinglePost.class);
					po = spo.getPost();
					date = po.getDate();
					newContent = po.getContent();
					try {
						SingleThumbnailImages sti = po.getThumbnailImages();
						SingleFull sf = sti.getFull();
						thumburl = sf.getUrl();
						img_height = sf.getHeight();
						img_width = sf.getWidth();
					}catch(NullPointerException ex) {}
			       	tagsList = new ArrayList<Tag>();
			        for(Tag tag: po.getTags()) {
			        	tagsList.add(tag);
			        }
			        categoriesList = new ArrayList<PostCategory>();
			        for(PostCategory pcategory: po.getCategories()) {
			        	categoriesList.add(pcategory);
			        }
				}
	            reader.close();
          }catch(Exception e) {
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
			}catch (IOException e) {
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
	
	public void onRestoreInstanceState(Bundle savedInstanceState) {
	    content.restoreState(savedInstanceState);
	    isColor = savedInstanceState.getBoolean("isColor");
	}
	
	public void onSavedInstanceState(Bundle sid) {
		sid.putBoolean("isColor", isColor);
		super.onSaveInstanceState(sid);
	}
}
