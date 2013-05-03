package com.waitnews;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import android.os.Bundle;
import android.content.Intent;
import android.widget.TextView;

public class ResultDetailsActivity extends SherlockActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_details);
        Intent intent = getIntent();
        TextView nameView = (TextView) findViewById(R.id.name);
        nameView.setText(intent.getStringExtra("placeName"));
        TextView line1View = (TextView) findViewById(R.id.address_line1);
        line1View.setText(intent.getStringExtra("address_line1"));
        TextView line2View = (TextView) findViewById(R.id.address_line2);
        line2View.setText(intent.getStringExtra("address_line2"));
    }
    
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

}
