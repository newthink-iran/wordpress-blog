package com.entekhab.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.text.Html;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.entekhab.R;
import com.entekhab.model.posts.Author;
import com.entekhab.model.posts.Full;
import com.entekhab.model.posts.Posts;
import com.entekhab.model.posts.ThumbnailImages;
import com.entekhab.utils.ImageLoader;

/**
 * Whenever you see a list of post, this adapter is responsible for that.
 * 
 * @author Pixelart Web and App Development
 * @since 1.0
 */
public class PostsAdapter extends BaseAdapter {

	private List<Posts> listData;
	private LayoutInflater layoutInflater;
	public ImageLoader imageLoader;
	Posts posts;
	ThumbnailImages thimg;
	Full img;
	Author author;
	Context context;
	
	public PostsAdapter(Context context, List<Posts> listData) {
		this.listData = listData;
		this.context = context;
		layoutInflater = LayoutInflater.from(context);
		imageLoader = new ImageLoader(context);
	}

	@Override
	public int getCount() {
		return listData.size();
	}
	
	public void add(Posts posts) {
	    listData.add(posts);
	    notifyDataSetChanged();
	}

	@Override
	public Object getItem(int position) {
		return listData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.row_posts, null);
			holder = new ViewHolder();
			holder.headlineView = (TextView) convertView.findViewById(R.id.posts_title);
			holder.commentView = (TextView) convertView.findViewById(R.id.posts_comment);
			holder.dateView = (TextView) convertView.findViewById(R.id.posts_date);
			holder.authorView = (TextView) convertView.findViewById(R.id.posts_author);
			holder.imageView = (ImageView) convertView.findViewById(R.id.thumbImage);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		posts = (Posts) listData.get(position);
        if(posts != null){	
        	author = (Author) posts.getAuthor();
 			if(posts.getThumbnailImages() != null){
 				thimg = posts.getThumbnailImages();
 				if(thimg.getFull() != null){
 					img = thimg.getFull();
 				}else{
 					thimg.setFull(null);
 					img.setUrl(null);
 				}
 			}else{
 				posts.setThumbnailImages(null);
 			}
 			
 			String date = posts.getDate();
 			
 			// "x time ago" like Date calculation
 			boolean timeago = context.getResources().getBoolean(R.bool.showTimeAgo);
 			if(timeago == true) {
	 			SimpleDateFormat formatter = new SimpleDateFormat("dd. MMM yyyy, H:mm", Locale.US);
	 			try{
	 				Date ddate = formatter.parse(date);
	 				long millis = ddate.getTime();
	 				date = DateUtils.getRelativeTimeSpanString(millis, System.currentTimeMillis(),
	 	                    DateUtils.SECOND_IN_MILLIS).toString();
	 				
	 			}catch(ParseException e) {
	 				e.printStackTrace();
	 			}
 			}
 			
			holder.headlineView.setText(Html.fromHtml(posts.getTitle()));
			holder.dateView.setText(Html.fromHtml("&nbsp;<b>"+date+"</b>"));
			holder.authorView.setText(Html.fromHtml("&nbsp;<b>"+author.getName()+"</b>"));
			holder.commentView.setText(Html.fromHtml("<b>"+posts.getCommentCount()+"</b>&nbsp;"));
			
		    ImageView image = holder.imageView;		    
		    try {
				if(!img.getUrl().isEmpty() && posts.getThumbnailImages() != null && thimg.getFull() != null) {
				    image.setVisibility(View.VISIBLE);
					imageLoader.DisplayImage(img.getUrl(), image);
				}else {
					image.setVisibility(View.GONE);
				}
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
	  }		
		return convertView;
	}
	

	static class ViewHolder {
		TextView headlineView;
		TextView commentView;
		TextView dateView;
		TextView authorView;
		ImageView imageView;
	}
}
