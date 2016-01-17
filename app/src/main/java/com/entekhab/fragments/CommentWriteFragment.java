package com.entekhab.fragments;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.entekhab.PostDetails;
import com.entekhab.R;
import com.entekhab.utils.ConnectionDetector;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * Fragment for writing and submiting a comment.
 * 
 * @author Pixelart Web and App Development
 * @since 1.0
 */
public class CommentWriteFragment extends Fragment {
	
	Context context;
	EditText etName;
	EditText etEmail;
	EditText etComment;
	Button btnSend;
	TextView tv_com_status;
	ProgressDialog pd;
	ConnectionDetector cd;
	String responseBody = null;
	String error;
	String urlName;
	String urlMail;
	String urlContent;
	String urlId;
	String com_status;
	
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
		
		View v = inflater.inflate(R.layout.fragment_write_com, null);
		
		etName = (EditText) v.findViewById(R.id.etName);
		etEmail = (EditText) v.findViewById(R.id.etEmail);
		etComment = (EditText) v.findViewById(R.id.etContent);
		btnSend = (Button) v.findViewById(R.id.btn_sendit);
		tv_com_status = (TextView) v.findViewById(R.id.tv_coms_closed);
		
		SharedPreferences prefs = getActivity().getSharedPreferences("PREFERENCE_APP", 0);
		etName.setText(prefs.getString("Comment_Name", ""));
		etEmail.setText(prefs.getString("Comment_Mail", ""));

		com_status = getActivity().getIntent().getStringExtra("post_com_status");	 
		if(com_status == "closed"){
			btnSend.setEnabled(false);
			tv_com_status.setVisibility(View.VISIBLE);
		} else {
			tv_com_status.setVisibility(View.GONE);
		}
		
		btnSend.setOnClickListener(new OnClickListener() {
		
	// Checks the user input for validity
	@SuppressWarnings("unchecked")
	@Override
	public void onClick(View vi) {
      if (etName.getText().length() <= 1) {
    	   etName.setError(getString(R.string.comNameError));
      }else if (!(etEmail.getText().toString().contains("@"))) {
	       etEmail.setError(getString(R.string.comMailError));
      }else if (etComment.getText().length() <= 2) {
	       etComment.setError(getString(R.string.comContentError));
      }else {
	       urlName = etName.getText().toString();
	       urlMail = etEmail.getText().toString();
	       urlContent = etComment.getText().toString();
	       
	       SharedPreferences prefs = getActivity().getSharedPreferences("PREFERENCE_APP", 0);
		   SharedPreferences.Editor editor = prefs.edit();
		   editor.putString("Comment_Name", urlName);
		   editor.putString("Comment_Mail", urlMail);
		   editor.commit();
	       
	       urlId = getActivity().getIntent().getStringExtra("post_id");	       
	       btnSend.setEnabled(true);
	       
	       cd = new ConnectionDetector(getActivity());
			if(isCon() == true){
				new PostCommentTask().execute();
			}else{
				cd.makeAlert();
			}
	   }
     }});
     return v;
   }
	
	private Boolean isCon() {
		cd = new ConnectionDetector(getActivity());
		return cd.isConnectingToInternet();
	}
	
	/**
	 * This Task uploads the Comment by the User to your Blog, 
	 * and checks if everything related to the Comment is ok
	 */
	private class PostCommentTask extends AsyncTask <ArrayList<NameValuePair>, Void, Void> {

		@Override
        protected void onPreExecute() {
            pd = ProgressDialog.show(getActivity(), "", getString(R.string.wait));
        }

		@Override
		protected void onPostExecute(Void result) {
			statusReader();
			pd.dismiss();
		}

		@SuppressWarnings("unchecked")
		@Override
		protected Void doInBackground(ArrayList<NameValuePair>... nameValuePairs) {
			postData();
			return null;
		}
	}
	
	// Send the comment
	public void postData() {
	   String url = getString(R.string.blogurl)+getString(R.string.api)+"/submit_comment/";
       HttpClient httpclient = new DefaultHttpClient();
       HttpPost httppost = new HttpPost(url);

       try {
          List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(8);
		  nameValuePairs.add(new BasicNameValuePair("post_id", urlId));
          nameValuePairs.add(new BasicNameValuePair("name", urlName));
          nameValuePairs.add(new BasicNameValuePair("email", urlMail));
		  nameValuePairs.add(new BasicNameValuePair("content", urlContent));
          httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

          HttpResponse response = httpclient.execute(httppost);	
	      responseBody = EntityUtils.toString(response.getEntity());
		
         } catch (IOException e) {
         }
    } 
	
	// Read the response and notify the user
	private void statusReader() {
		try {
             JSONObject appObject = new JSONObject(responseBody);
             error = appObject.getString("error");
        } catch(Exception ex){
        }
		
		if(responseBody.contains("pending")) {
		    Crouton.makeText(getActivity(), getString(R.string.comSendPending), Style.CONFIRM).show();
		}else if(responseBody.contains("ok")) {
		    Crouton.makeText(getActivity(), getString(R.string.comSended), Style.CONFIRM).show();
		}else if(responseBody.contains("Post is closed for comments.")) {
		    Crouton.makeText(getActivity(), getString(R.string.comClosedComment), Style.ALERT).show();
		}else if(responseBody.contains("Please enter a valid email")) {
		    Crouton.makeText(getActivity(), getString(R.string.comMailError), Style.ALERT).show();
		}
	}
}
