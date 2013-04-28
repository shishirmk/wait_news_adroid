package com.waitnews;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class ResultRow {
    public String placeName;
    public String line1;
    public String line2;
    public String city;
    public String state;
    public String country;
    public String zipcode;
    public ResultRow() {
        super();
    }
    
    public ResultRow(JSONObject resObj){
        super();
        try {
            this.placeName = resObj.getString("name");
            JSONObject addrObj = resObj.getJSONObject("address");
            this.line1 = addrObj.getString("line1");
            this.line2 = addrObj.getString("line2");
            this.city = addrObj.getString("city");
            this.state = addrObj.getString("state");
            this.country = addrObj.getString("country");
            this.zipcode = addrObj.getString("zipcode");
        } catch (JSONException e) {
            Log.d("Exception:", ResultRow.class.toString());
            e.printStackTrace();
        }
    }
}
