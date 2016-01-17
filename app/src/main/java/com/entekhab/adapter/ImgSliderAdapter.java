package com.entekhab.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.entekhab.fragments.FullScreenFragment;
import com.entekhab.model.singlepost.Attachment;
import com.entekhab.utils.ImageLoader;

/**
 * Responsible for the Fullscreen images slider
 * 
 * @author Pixelart Web and App Development
 * @since 1.5
 */
public class ImgSliderAdapter extends FragmentPagerAdapter {
	
    private ArrayList<Attachment> listData;
    public ImageLoader imageLoader;
    Context context;
    Bitmap bm;
    
	
	public ImgSliderAdapter(Context context, ArrayList<Attachment> listData, FragmentManager fm) {
		super(fm);
		this.listData = listData;
		this.context = context;
		imageLoader = new ImageLoader(context);
	}
	
	@Override
    public Fragment getItem(int position) {
		String url = null;
		Attachment foto = (Attachment) listData.get(position);
		if(foto != null) {	
		   if(foto.getMimeType().contains("image")) {
			   url = foto.getUrl();
		   }
		}
		
        return new FullScreenFragment(url);
    }

    @Override
    public int getCount() {
    	return listData.size();
    }
    
    @Override
	public long getItemId(int position) {
		return position;
	}

}
