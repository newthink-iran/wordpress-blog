package com.entekhab.adapter;

import java.util.List;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.entekhab.R;
import com.entekhab.model.singlepost.Tag;

public class PostTagsAdapter extends BaseAdapter {

	private List<Tag> listData;
	private LayoutInflater layoutInflater;
	Tag tags;
	Context context;
	
	
	public PostTagsAdapter(Context context, List<Tag> listData) {
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
			convertView = layoutInflater.inflate(R.layout.row_post_tags, null);
			holder = new ViewHolder();
			holder.tagView = (TextView) convertView.findViewById(R.id.tagGrid);
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder) convertView.getTag();
		}

		tags = (Tag) listData.get(position);
        if(tags != null) {
			
			holder.tagView.setText(Html.fromHtml(tags.getTitle()));
	  }
		return convertView;
	}
	

	static class ViewHolder {
		TextView tagView;
	}
}
