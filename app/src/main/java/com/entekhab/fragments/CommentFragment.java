package com.entekhab.fragments;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.entekhab.PostDetails;
import com.entekhab.R;
import com.entekhab.adapter.CommentAdapter;
import com.entekhab.model.page.Page;
import com.entekhab.model.page.SinglePage;
import com.entekhab.model.singlepost.Comment;
import com.entekhab.model.singlepost.Post;
import com.entekhab.model.singlepost.SinglePost;
import com.entekhab.utils.ConnectionDetector;
import com.entekhab.utils.FloatingActionButton;

/**
 * This Fragment shows the user all the comments if available of a post
 * 
 * @author Pixelart Web and App Development
 * @since 1.0
 */
public class CommentFragment extends Fragment{
	
	ConnectionDetector cd;
	SinglePost spo;
	Post po;
	SinglePage spa;
	Page pa;
	ProgressDialog pd;
	SwipeRefreshLayout swipeLayout;
	ListView commentListView;
	TextView nocom;
	ArrayList<Comment> commentList= null;
	String baseurl;
	String param = "?json=1&include=comments";
	String url;
	String author;
	String date;
	String postType;
	ImageButton btn_com;
	
	
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
		
		View v = inflater.inflate(R.layout.fragment_com, null);
		nocom = (TextView) v.findViewById(R.id.tv_no_com);
		commentListView = (ListView) v.findViewById(R.id.comment_list);
		
		FloatingActionButton button = (FloatingActionButton) v.findViewById(R.id.btn_new_comment);
	    button.setSize(FloatingActionButton.SIZE_NORMAL);
	    button.setColorNormalResId(R.color.primary);
	    button.setColorPressedResId(R.color.primaryDark);
	    button.setIcon(R.drawable.ic_action_new_comment);
	    button.setStrokeVisible(false);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getFragmentManager().beginTransaction()
				.replace(R.id.postframe, new CommentWriteFragment())
				.addToBackStack(null)
				.commit();				
			}
		});
		
		url = getActivity().getIntent().getStringExtra("post_url");
		postType = getActivity().getIntent().getStringExtra("post_type");
	/*	
		// Swipe refresher settings
		swipeLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_container);
		swipeLayout.setColorSchemeResources(R.color.primary, R.color.flat_blue, R.color.flat_green, R.color.flat_red);
		swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				if(isCon() == true){
					new DownloadComTask().execute(url+param);
				}else {
					cd.makeAlert();
				}
			}
		});
		
		commentListView.setOnScrollListener(new AbsListView.OnScrollListener() {
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
		}); */
		
		if(isCon() == true) {
	     	new DownloadComTask().execute(url+param);
		}else {
			cd.makeAlert();
		}
		
		return v;
	}
	
	private Boolean isCon() {
		cd = new ConnectionDetector(getActivity());
		return cd.isConnectingToInternet();
	}
	
	public void updateList() {
		if(commentList != null && commentList.size() >= 1){
		   nocom.setVisibility(View.GONE);
		   commentListView.setVisibility(View.VISIBLE);
		   commentListView.setAdapter(new CommentAdapter(getActivity(), commentList));
		
		}else{
			nocom.setText(getString(R.string.no_comment));
		}
	}
	
	// Downloads the Comments
	private class DownloadComTask extends AsyncTask<String, Integer, Void> {

		@Override
        protected void onPreExecute() {
            pd = ProgressDialog.show(getActivity(), "", getString(R.string.wait));
	//		swipeLayout.setEnabled(true);
	//		swipeLayout.setRefreshing(true);
        }

		@Override
		protected void onPostExecute(Void result) {
				updateList();
				pd.dismiss();
			//	swipeLayout.setRefreshing(false);
		}

		@Override
		protected Void doInBackground(String... params) {
			String url = params[0];
			try {
				InputStream source = retrieveStream(url);
		        Gson gson = new Gson();        
		        Reader reader = new InputStreamReader(source);        
		        
		        if(postType == "page") {
		        	spa = gson.fromJson(reader, SinglePage.class);
					pa = spa.getPage();
					commentList = new ArrayList<Comment>();
					for (Comment com : pa.getComments()) {
						commentList.add(com);
					}
				}else {
					spo = gson.fromJson(reader, SinglePost.class);
					po = spo.getPost();
					commentList = new ArrayList<Comment>();
					for (Comment com : po.getComments()) {
						commentList.add(com);
					}
				}
				
		        reader.close();
           } catch (Exception e) { }
	        
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
