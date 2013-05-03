package com.waitnews;

import android.view.View;
import android.widget.TextView;

public class ResultHolder {
    public TextView place;
    public TextView address_line1;
    public TextView address_line2;
    public TextView avg_wait_time;
    
    public ResultHolder(View row){
        super();
        this.place = (TextView)row.findViewById(R.id.item_place);
        this.address_line1 = (TextView)row.findViewById(
                                            R.id.item_address_line1 );
        this.address_line2 = (TextView)row.findViewById(
                                            R.id.item_address_line2 );
        this.avg_wait_time = (TextView)row.findViewById(
                                            R.id.avg_wait_time );
    }
    
    public void populate(ResultRow result){
        this.place.setText(result.placeName);
        this.address_line1.setText(result.address.line1);
        this.address_line2.setText(result.address.city + ", " + result.address.state);
        this.avg_wait_time.setText(result.avgWaitTime.toString());
    }
    
    public ResultRow getResultRow(){
        ResultRow tempRow = new ResultRow();
        tempRow.placeName = this.place.getText().toString();
        Address tempAddr = new Address();
        tempAddr.line1 = this.address_line1.getText().toString();
        String line2 = this.address_line2.getText().toString();
        String[] tempArray = line2.split(",");
        tempAddr.city = tempArray[0];
        tempAddr.state = tempArray[1];
        tempRow.address = tempAddr;
        tempRow.avgWaitTime = Integer.getInteger(this.avg_wait_time.getText().toString());
        return tempRow;
    }
}
