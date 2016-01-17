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
import com.entekhab.model.singlepost.PostCategory;

public class PostCategoriesAdapter extends BaseAdapter {

	private List<PostCategory> listData;
	private LayoutInflater layoutInflater;
	PostCategory category;
	Context context;
	
	
	public PostCategoriesAdapter(Context context, List<PostCategory> listData) {
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
			convertView = layoutInflater.inflate(R.layout.row_post_categories, null);
			holder = new ViewHolder();
			holder.categoryView = (TextView) convertView.findViewById(R.id.categoryGrid);
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder) convertView.getTag();
		}

		category = (PostCategory) listData.get(position);
        if(category != null) {
			
			holder.categoryView.setText(Html.fromHtml(category.getTitle()));
	  }
		return convertView;
	}
	

	static class ViewHolder {
		TextView categoryView;
	}
}
