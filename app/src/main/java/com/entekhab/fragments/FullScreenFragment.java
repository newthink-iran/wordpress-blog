package com.entekhab.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.entekhab.R;
import com.entekhab.utils.ConnectionDetector;
import com.entekhab.utils.TouchImageLoader;
import com.entekhab.utils.TouchImageView;

/**
 * Shows a single Image from the image slider
 * 
 * @author Pixelart Web and App Development
 * @since 1.5
 *
 */
public class FullScreenFragment extends Fragment {
	
	TouchImageLoader imageLoader;
	String url;
	ConnectionDetector cd;
	View v;

    public FullScreenFragment(String url) {
        this.url = url;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	
    	v = inflater.inflate(R.layout.row_images, container, false);

        TouchImageView img = (TouchImageView) v.findViewById(R.id.imgGrid);
        imageLoader = new TouchImageLoader(getActivity());
        if(null != url && isCon() != false) {
		    imageLoader.DisplayImage(url, img);
		}else{
			cd.makeAlert();
		}
        
        img.setClickable(true); 
		img.setOnClickListener(new View.OnClickListener() {
	        @Override
	        public void onClick(View arg0) {
	        }
	    });

        return v;
    }
    
    private Boolean isCon() {
		cd = new ConnectionDetector(getActivity());
		return cd.isConnectingToInternet();
	}
}
