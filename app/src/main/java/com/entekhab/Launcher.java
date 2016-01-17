package com.entekhab;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.entekhab.utils.ConnectionDetector;

/**
 * This is the Launcher Activity<br>
 * It is responsible for all the startup stuff, like checking for an update,or register for gcm and so on<br>
 * In this class it is also defined which first visible Activity to StartFragment.<br> By default <b>LatestPosts</b>
 * 
 * @author Pixelart Web and App Development
 * @since 1.0
 */
public class Launcher extends ActionBarActivity {

	public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    String SENDER_ID;
    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    ConnectionDetector cd;
    SharedPreferences prefs;
    Context context;
    String regid;
	String responseBody;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		context = getApplicationContext();
		
		SENDER_ID  = getString(R.string.gcm_senderId);
		cd = new ConnectionDetector(this);
		
		// check if play services are present
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            regid = getRegistrationId(context);
            if (regid.isEmpty()) {
            	if(isCon()) {
            		new registerInBackground().execute();
            	}else {
            		cd.makeAlert();
            	}
        	}
        }
        doUpdateDone();
    }
    
    private Boolean isCon() {
		cd = new ConnectionDetector(this);
		return cd.isConnectingToInternet();
	}
    
    /**
     * 
	 * Checks if an update was installed since the last use <br>
	 * <br>
	 * If true, you can do something with it.<br>
	 * By default, the short changelog is shown.
	 * 
	 */
	private void doUpdateDone() {
		final int currentVersion = R.string.version;
		SharedPreferences prefs = getSharedPreferences("PREFERENCE_APP", 0);
		int lastVersion = prefs.getInt("LastVersion", -1);
		if(currentVersion > lastVersion) {
			boolean changelog = getResources().getBoolean(R.bool.showChangelog);
			if(changelog == true) {
				AlertDialog.Builder alert = new AlertDialog.Builder(Launcher.this);
				WebView wv = new WebView(Launcher.this);
				wv.loadUrl("file:///android_asset/short_changelog.html");
				wv.setWebViewClient(new WebViewClient() {
						@Override
						public boolean shouldOverrideUrlLoading(WebView view, String url){
							view.loadUrl(url);
							return true;
						}});
				alert.setView(wv);
				alert.setNegativeButton(getString(R.string.btn_close), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int id){
							SharedPreferences prefs = getSharedPreferences("PREFERENCE_APP", 0);
							SharedPreferences.Editor editor = prefs.edit();
							editor.putInt("LastVersion", currentVersion);
							editor.commit();
							launchFinished();
							dialog.dismiss();
						}});
				alert.show();
			}			
		}else{
			launchFinished();
		}
	}
	
	
	
	/**
	 * 
	 * GCM related stuff
	 * 
	 */
	private boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode, this,
													  PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else {
				finish();
			}
			return false;
		}
		return true;
	}
	
	private String getRegistrationId(Context context) {
		final SharedPreferences prefs = getGCMPreferences(context);
		String registrationId = prefs.getString(PROPERTY_REG_ID, "");
		if (registrationId.isEmpty()) {
			return "";
		}
	
		int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
		int currentVersion = getAppVersion(context);
		if (registeredVersion != currentVersion) {
			return "";
		}
		return registrationId;
	}
	
	private SharedPreferences getGCMPreferences(Context context) {
		return getSharedPreferences(Launcher.class.getSimpleName(), Context.MODE_PRIVATE);
	}
	
	private static int getAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (PackageManager.NameNotFoundException e) {
			throw new RuntimeException("Could not get package name: " + e);
		}
	}
	
	private class registerInBackground extends AsyncTask<Void, Integer, String> {
		@Override
        protected String doInBackground(Void... params) {
            String msg = "";
            try {
                if (gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(context);
                }
                regid = gcm.register(SENDER_ID);
                msg = "Device registered \n registration ID=" + regid;
                sendRegistrationIdToBackend();
				
                storeRegistrationId(context, regid);
            } catch (IOException ex) {
                msg = "Error :" + ex.getMessage();
            }
            return msg;
        }

        @Override
        protected void onPostExecute(String msg) {
        }
	}
	
	/**
	 * This Function registers your devices for GCM on your WordPress Blog
	 */
	private void sendRegistrationIdToBackend() {
		String os = android.os.Build.VERSION.RELEASE;
		String model = getDeviceName();
		os = os.replaceAll(" ", "%20");
		model = model.replaceAll(" ", "%20");
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(getString(R.string.blogurl)+
				"?regId="+regid+
				"&os=Android%20"+os+
				"&model="+model);
		try {
			HttpResponse response = httpclient.execute(httppost);	
			responseBody = EntityUtils.toString(response.getEntity());
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		}
	}
	
	// Get the device model name with manufacturer
	public String getDeviceName() {
	    String manufacturer = Build.MANUFACTURER;
	    String model = Build.MODEL;
	    if (model.startsWith(manufacturer)) {
	        return capitalize(model);
	    } else {
	        return capitalize(manufacturer) + " " + model;
	    }
	}

	private String capitalize(String s) {
	    if (s == null || s.length() == 0) {
	        return "";
	    }
	    char first = s.charAt(0);
	    if (Character.isUpperCase(first)) {
	        return s;
	    } else {
	        return Character.toUpperCase(first) + s.substring(1);
	    }
	}
	
	private void storeRegistrationId(Context context, String regId) {
		final SharedPreferences prefs = getGCMPreferences(context);
		int appVersion = getAppVersion(context);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(PROPERTY_REG_ID, regId);
		editor.putInt(PROPERTY_APP_VERSION, appVersion);
		editor.commit();
	}
	
	/**
	 *  this function is called when all the Start up processes is finished
	 */
	private void launchFinished() {
		Intent start = new Intent(Launcher.this, LatestPosts.class);
		startActivity(start);
		this.finish();
		
	}
}
