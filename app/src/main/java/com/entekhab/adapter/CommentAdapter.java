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
import android.widget.TextView;

import com.entekhab.R;
import com.entekhab.model.singlepost.Comment;

/**
 * Parses the complete CommentList comment per comment to a viewable view.
 * 
 * @author Pixelart Web and App Development
 * @since 1.0
 */
public class CommentAdapter extends BaseAdapter {

	private List<Comment> listData;
	private LayoutInflater layoutInflater;
	Comment coms;
	Context context;
	
	
	public CommentAdapter(Context context, List<Comment> listData) {
		this.listData = listData;
		this.context = context;
		layoutInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return listData.size();
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
		if(convertView == null) {
			convertView = layoutInflater.inflate(R.layout.row_comments, null);
			holder = new ViewHolder();
			holder.contentView = (TextView) convertView.findViewById(R.id.com_content);
			holder.dateView = (TextView) convertView.findViewById(R.id.com_date);
			holder.authorView = (TextView) convertView.findViewById(R.id.com_author);
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder) convertView.getTag();
		}

		coms = (Comment) listData.get(position);
        if(coms != null) {
			
        	String date = coms.getDate();
        	
        	// "x time ago" like Date calculation
 			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd H:mm:ss", Locale.US);
 			try{
 				Date ddate = formatter.parse(date);
 				long millis = ddate.getTime();
 				date = DateUtils.getRelativeTimeSpanString(millis, System.currentTimeMillis(),
 	                    DateUtils.SECOND_IN_MILLIS).toString();
 				
 			}catch(ParseException e) {
 				e.printStackTrace();
 			}
 			
 			
			holder.dateView.setText(Html.fromHtml("&nbsp;<b>"+date+"</b>"));
			holder.authorView.setText(Html.fromHtml("<b>"+coms.getName()+"</b>"));
			holder.contentView.setText(Html.fromHtml(coms.getContent()));
	  }
		return convertView;
	}
	

	static class ViewHolder {
		TextView contentView;
		TextView dateView;
		TextView authorView;
	}
}
