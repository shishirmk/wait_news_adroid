package com.example.waitnews;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class WaitNewsService extends Service {

	public interface WaitNewsServiceInt {
		/* Callback interface to be implemented by WaitNewsService 
		 * clients to process the results retrieved. */
		public void processSearchResults(long requestID, String res);
	}

	public class WaitNewsServiceBinder extends Binder {
	    WaitNewsService getService() {
	        return WaitNewsService.this;
	    }
	}
	
    private final IBinder mBinder = new WaitNewsServiceBinder();
    private long requestID = 0;
    private long lastRequestServed = 0;
    private Semaphore mutex = new Semaphore(1, true);
    private ArrayList<WaitNewsServiceInt> requests = new ArrayList<WaitNewsServiceInt>();
    private ArrayList<String> requestUrls = new ArrayList<String>();
    
    @Override
    public void onCreate() {
    	// TODO
    	Thread serviceThread = new Thread(new Runnable(){
		    @Override
		    public void run() {
		    	while (true) {
		    		try {
						mutex.acquire();
					} catch (InterruptedException e) {
						e.printStackTrace();
						continue;
					}
		    		if (lastRequestServed < requests.size()) {
		    			String query = requestUrls.get((int)lastRequestServed);
		    			requests.get((int)lastRequestServed).processSearchResults(lastRequestServed,
		    																	  WaitNewsService.readWaitNewsFeed(query));
		    			lastRequestServed++;
		    		}
		    		mutex.release();
		    	}
		    }
		});
		serviceThread.start();
    }

    @Override
    public void onDestroy() {
    	// TODO
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
    
	public static String readWaitNewsFeed(String query) {
		StringBuilder builder = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		String hostname = "10.0.0.40";
		String port = "3000";
		String url = new String();
		try {
			url = "http://" + hostname + ":" + port + "/places/search?query=" + URLEncoder.encode(query, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		HttpGet httpGet = new HttpGet(url);
		try {
			HttpResponse response = client.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			Log.d(MainActivity.class.toString(), statusLine.toString());
			if (statusCode == 200) {
				HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(content));
				String line;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}
			} else {
				Log.d(MainActivity.class.toString(), "Failed to download file");
			}
		} catch (ClientProtocolException e) {
				e.printStackTrace();
		} catch (IOException e) {
				e.printStackTrace();
		}
		return builder.toString();
	}
    
    public long getSearchResults(String url, WaitNewsServiceInt callBackObj) {
    	Log.d("main activity: ", callBackObj.toString());
		try {
			mutex.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
			return -1;
		}
		requests.add(callBackObj);
		requestUrls.add(url);
		requestID++;
		mutex.release();
    	return requestID;
    }
}
