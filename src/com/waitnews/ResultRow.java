package com.waitnews;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class ResultRow {
    public String placeName;
    public Address address;
    public Integer avgWaitTime;
    public WaitTime lastestWait;
    public ResultRow() {
        super();
    }
    
    public ResultRow(JSONObject resObj){
        super();
        try {
            this.placeName = resObj.getString("name");
            JSONObject addrObj = resObj.getJSONObject("address");
            this.address = new Address(addrObj);
            this.avgWaitTime = (int) Math.floor(Double.parseDouble(resObj.getString("average_wait_time")));
        } catch (JSONException e) {
            Log.d("Exception:", ResultRow.class.toString());
            e.printStackTrace();
        }
    }
}
