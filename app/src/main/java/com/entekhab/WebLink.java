package com.entekhab;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

/**
 * This Activity handles what to do if app was opened using the AppLinker WordPress Plugin.
 * If you defined your own type in the plugin, define here what to do with the new type
 * 
 * @author Pixelart Web & App Development
 * @since 1.5
 *
 */
public class WebLink extends ActionBarActivity {
	
	String id;
	String title;
	String url;
	String author;
	String type;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Get the host to determine what to do (is it post, frontpage, tag,...)
		try {
			type = getIntent().getData().getHost();
		}catch (NullPointerException e) {}
		
		if(type != null) {
			if(type.equals("home")) {
				Intent home = new Intent(WebLink.this, LatestPosts.class);
				startActivity(home);
				finish();
			}else if(type.equals("post")) {
				id = getIntent().getData().getQueryParameter("id");
				title = getIntent().getData().getQueryParameter("title");
				url = getIntent().getData().getQueryParameter("url");
				author = getIntent().getData().getQueryParameter("author");
				Intent post = new Intent(WebLink.this, TagPosts.class);
				post.putExtra("post_id", id);
				post.putExtra("post_url", url);
				post.putExtra("post_title", title);
				post.putExtra("post_author", author);
				startActivity(post);
				finish();
			}else if(type.equals("tag")) {
				id = getIntent().getData().getQueryParameter("id");
				Intent tag = new Intent(WebLink.this, TagPosts.class);
				tag.putExtra("tag_id", id);
				startActivity(tag);
				finish();
			}else if(type.equals("category")) {
				id = getIntent().getData().getQueryParameter("id");
				title = getIntent().getData().getQueryParameter("title");
				Intent cat = new Intent(WebLink.this, CategoryPosts.class);
				cat.putExtra("category_id", id);
				cat.putExtra("category_title", title);
				startActivity(cat);
				finish();
			}
		}
	}
	
}
