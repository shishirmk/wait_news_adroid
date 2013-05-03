package com.waitnews;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.waitnews.WaitNewsService.WaitNewsServiceBinder;
import com.waitnews.WaitNewsService.WaitNewsServiceInt;

public class MainActivity extends SherlockActivity implements WaitNewsServiceInt {

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
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("Search").setIcon(R.drawable.abs__ic_search)
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getTitle() == "Search"){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            return true;
        }
        return true;
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if (Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH){
            this.setTheme(R.style.Theme_Sherlock_Light);
        }
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Waitnews");
        setContentView(R.layout.activity_main);

        mHandler = new MainActivityHandler(this);
        mResultAdapter = new ResultListAdapter(this, R.layout.result_list_row, 
                                               new ArrayList<ResultRow>());
        mResultList = (ListView) findViewById(R.id.results_list);
        mResultList.setAdapter(mResultAdapter);
        mResultList.setOnItemClickListener(new OnItemClickListener(){
            
            @Override
            public void onItemClick(AdapterView<?> item, View row, int position, long id) {
                // TODO Auto-generated method stub
                ResultHolder holder = new ResultHolder(row);
                ResultRow result = holder.getResultRow();
                Intent intent = new Intent( MainActivity.this , ResultDetailsActivity.class);
                intent.putExtra("placeName", result.placeName.toString());
                intent.putExtra("address_line1", holder.address_line1.getText().toString());
                intent.putExtra("address_line2", holder.address_line2.getText().toString());
                intent.putExtra("avg_wait_time", result.avgWaitTime);
                startActivity(intent);
                //Toast.makeText(getApplicationContext(), result.placeName, Toast.LENGTH_LONG).show();   
            }
            
        });

        Button searchButton = (Button) findViewById(R.id.search_button);	
        searchButton.setOnClickListener( new OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = 
                        (String) ((EditText)findViewById(R.id.search_box))
                        .getText().toString();
                if (mBound) {
                    try {
                        long ID = mService.sendRequest(new WaitNewsSearchRequest(query), 
                                                       MainActivity.this);
                        Log.d("Rid: ", "ID" + ID);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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

    public void handleResponse(long requestId, String res) {
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
