package com.waitnews;

import android.view.View;
import android.widget.TextView;

public class ResultHolder {
    public TextView place;
    public TextView address_line1;
    public TextView address_line2;
    
    public ResultHolder(View row){
        super();
        this.place = (TextView)row.findViewById(R.id.item_place);
        this.address_line1 = (TextView)row.findViewById(
                                            R.id.item_address_line1 );
        this.address_line2 = (TextView)row.findViewById(
                                            R.id.item_address_line2 );
    }
    
    public void populate(ResultRow result){
        this.place.setText(result.placeName);
        this.address_line1.setText(result.address.line1);
        this.address_line2.setText(result.address.city + ", " + result.address.state);
    }

}
