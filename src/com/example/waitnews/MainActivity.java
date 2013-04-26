package com.example.waitnews;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.waitnews.WaitNewsService.WaitNewsServiceBinder;
import com.example.waitnews.WaitNewsService.WaitNewsServiceInt;

public class MainActivity extends Activity implements WaitNewsServiceInt {
	private WaitNewsService mService; 
	private boolean mBound = false;
	private ResultListAdapter resultAdapter = null;
	private ListView resultList = null;
	
	private Handler resultHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
        	if (msg.what == 0) {
        		displayResults((String) msg.obj);
        	}
        }
	};
	
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, 
        							   IBinder service) {
            WaitNewsServiceBinder binder = (WaitNewsServiceBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
	    ArrayList<ResultRow> results = new ArrayList<ResultRow>();
			        
		resultAdapter = new ResultListAdapter(this, R.layout.result_list_row, 
				  							  results);
		resultList = (ListView) findViewById(R.id.results_list);
		resultList.setAdapter(resultAdapter);
		
		Button searchButton = (Button) findViewById(R.id.search_button);	
		searchButton.setOnClickListener( new OnClickListener() {
            @Override
            public void onClick(View v) {
	        	String searchString = 
	        			(String) ((EditText)findViewById(R.id.search_box))
	        						.getText().toString();
	        	if (mBound) {
	        		long ID = mService.getSearchResults(searchString, MainActivity.this);
	        		Log.d("Rid: ", "ID" + ID);
	        	}
            }
		});
	}
	
    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, WaitNewsService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    public void processSearchResults(long requestID, String res) {
    	try {
    		resultHandler.sendMessage(resultHandler.obtainMessage(0, res));
    	} catch (Exception e) {
    		// Do nothing
    	}
    }
    
    private void displayResults(String results_string) {
    	JSONArray results_json_array;
    	Log.d("display", results_string);
		try {
			results_json_array = new JSONArray(results_string);
			resultAdapter.clear();
	    	for (int i = 0; i < results_json_array.length(); i++) {
				JSONObject result = results_json_array.getJSONObject(i);
				String name = result.getString("name");
				JSONObject address = result.getJSONObject("address");
				String address_string = new String();
				if ( address.getString("line1") != "null" ){ 
					address_string += address.getString("line1") + " ";
				}
				if ( address.getString("line2") != "null" ){
					address_string += address.getString("line2") + " ";
				}
				if ( address.getString("city") != "null" ){
					address_string += address.getString("city");
				}
				Log.d("J: ", name);
				resultAdapter.add(new ResultRow(name, address_string));
	    	}
			// Toast.makeText(getApplicationContext(), results_string, Toast.LENGTH_LONG).show();
			Log.d("Results: ", results_string);
		} catch (Exception e) {
			Log.d("Exception", e.toString());
		}
    }
}
