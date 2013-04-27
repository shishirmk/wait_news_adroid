package com.waitnews;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

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

import com.waitnews.R;
import com.waitnews.WaitNewsService.WaitNewsServiceBinder;
import com.waitnews.WaitNewsService.WaitNewsServiceInt;

public class MainActivity extends Activity implements WaitNewsServiceInt {

    private static class MainActivityHandler extends Handler {
        private final WeakReference<MainActivity> mActivity;

        public MainActivityHandler(MainActivity activity) {
            mActivity = new WeakReference<MainActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = mActivity.get();
            if (activity == null) {
                return;
            }
            if (msg.what == 0) {
                activity.displayResults((String) msg.obj);
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

    private WaitNewsService mService; 
    private boolean mBound = false;
    private ResultListAdapter mResultAdapter = null;
    private ListView mResultList = null;
    private MainActivityHandler mHandler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHandler = new MainActivityHandler(this);

        mResultAdapter = new ResultListAdapter(this, R.layout.result_list_row, 
                                               new ArrayList<ResultRow>());
        mResultList = (ListView) findViewById(R.id.results_list);
        mResultList.setAdapter(mResultAdapter);

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
            mHandler.sendMessage(mHandler.obtainMessage(0, res));
        } catch (Exception e) {
            Log.d(MainActivity.class.toString(), e.toString());
        }
    }

    private void displayResults(String results_string) {
        try {
            SearchResults results = new SearchResults(results_string);
            mResultAdapter.clear();
            for (int i = 0; i < results.size(); i++) {
                mResultAdapter.add(results.getResult(i));
            }
            Log.d("Results: ", results_string);
        } catch (Exception e) {
            Log.d("Exception", e.toString());
        }
    }
}
