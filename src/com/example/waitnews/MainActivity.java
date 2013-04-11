package com.example.waitnews;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

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
import android.widget.Toast;

public class MainActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Button searchButton = (Button) findViewById(R.id.search_button);
		
		searchButton.setOnClickListener( new OnClickListener() {
            @Override
            public void onClick(View v) {
            	final Toast toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_LONG);
        		Thread thread = new Thread(new Runnable(){
        		    @Override
        		    public void run() {
        		        try {
        		        	String searchString = (String) ((EditText)findViewById(R.id.search_box)).getText().toString();
        	            	try {
        	            		JSONArray results = new JSONArray(readWaitNewsFeed(searchString));
        	            		String names = displaySearchResults(results);
        	            		toast.setText(names);
        	            		toast.show();
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
	
	protected String displaySearchResults(JSONArray jsonArray) {
		String names = new String();
		for (int i = 0; i < jsonArray.length(); i++) {
	        JSONObject jsonObject;
			try {
				jsonObject = jsonArray.getJSONObject(i);
				names += jsonObject.optString("name") + " ";
				Log.d(MainActivity.class.getName(), jsonObject.optString("name"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
	    }
		return names;
	}
	
	public String readWaitNewsFeed(String query) {
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
}
