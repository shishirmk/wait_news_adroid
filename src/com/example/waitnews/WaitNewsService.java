package com.example.waitnews;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.concurrent.LinkedBlockingQueue;

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
	private static final int REQUEST_QUEUE_CAPACITY = 1000;
    private long requestID = 0;
    private LinkedBlockingQueue<Request> requestQueue = 
    			new LinkedBlockingQueue<Request>(REQUEST_QUEUE_CAPACITY);
	
	public interface WaitNewsServiceInt {
		/* Callback interface to be implemented by WaitNewsService 
		 * clients to process the results retrieved. */
		public void processSearchResults(long requestID, String results);
	}

	public class WaitNewsServiceBinder extends Binder {
	    WaitNewsService getService() {
	        return WaitNewsService.this;
	    }
	}
	
    private final IBinder mBinder = new WaitNewsServiceBinder();
    Thread serviceThread = null;
    
    @Override
    public void onCreate() {
    	serviceThread = new Thread(new Runnable(){
		    @Override
		    public void run() {
		    	while (true) {
		    		try {
		    			Request rq = requestQueue.take();
		    			rq.getCallBack()
		    			  .processSearchResults(
		    				   rq.getRequestID(), 
		    				   readWaitNewsFeed(rq.getQueryString())
		    			   );
		    		} catch (Exception e) {
		    			// Do nothing
		    		}
		    	}
		    }
		});
		serviceThread.start();
    }

    @Override
    public void onDestroy() {
    	// Nothing needed as of now.
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
		} catch (Exception e1) {
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
    
    public long getSearchResults(String query, WaitNewsServiceInt callBackObj) {
    	Log.d("main activity: ", callBackObj.toString());
		try {
			requestQueue.put(new Request(query, requestID++, callBackObj));
		} catch (InterruptedException e) {
			e.printStackTrace();
			return -1;
		}
    	return requestID;
    }
}
