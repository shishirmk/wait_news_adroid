package com.waitnews;

import org.json.JSONException;
import org.json.JSONObject;

public class Address {
    public String line1;
    public String line2;
    public String city;
    public String state;
    public String country;
    public String zipcode;

    public Address() {
        super();
    }
    
    public Address(JSONObject addrObj){
        try {
            this.line1 = addrObj.getString("line1");
            this.line2 = addrObj.getString("line2");
            this.city = addrObj.getString("city");
            this.state = addrObj.getString("state");
            this.country = addrObj.getString("country");
            this.zipcode = addrObj.getString("zipcode");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
