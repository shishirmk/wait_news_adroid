package com.example.waitnews;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Button searchButton = (Button) findViewById(R.id.search_button);
		
		searchButton.setOnClickListener( new OnClickListener() {
            @Override
            public void onClick(View v) {
        		Thread thread = new Thread(new Runnable(){
        		    @Override
        		    public void run() {
        		        try {
        		        	String searchString = (String) ((EditText)findViewById(R.id.search_box)).getText().toString();
        	            	try {
        	            		JSONArray results = new JSONArray(readWaitNewsFeed());
        	            		displaySearchResults(results);
        	            	} catch (JSONException e) {
        	            		e.printStackTrace();
        	            	}
        		        } catch (Exception e) {
        		            e.printStackTrace();
        		        }
        		    }
        		});
        		thread.start();
            }
		});
	}
	
	protected void displaySearchResults(JSONArray jsonArray) {
		for (int i = 0; i < jsonArray.length(); i++) {
	        JSONObject jsonObject;
			try {
				jsonObject = jsonArray.getJSONObject(i);
				Log.d(MainActivity.class.getName(), jsonObject.toString());
			} catch (JSONException e) {
				e.printStackTrace();
			}
	    }
	}
	
	public String readWaitNewsFeed() {
		StringBuilder builder = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet("http://10.0.0.19:3000/addresses.json");
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
}
