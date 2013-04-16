package com.example.waitnews;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.example.waitnews.WaitNewsService.WaitNewsServiceBinder;
import com.example.waitnews.WaitNewsService.WaitNewsServiceInt;

public class MainActivity extends Activity implements WaitNewsServiceInt {
	private WaitNewsService mService;
	private boolean mBound = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Button searchButton = (Button) findViewById(R.id.search_button);	
		searchButton.setOnClickListener( new OnClickListener() {
            @Override
            public void onClick(View v) {
	        	String searchString = (String) ((EditText)findViewById(R.id.search_box)).getText().toString();
	        	if (mBound) {
	        		mService.getSearchResults(searchString, MainActivity.this);
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
    
    public void processSearchResults(long requestID, String res) {
    	Log.d("Results: ", res);
    }
}
