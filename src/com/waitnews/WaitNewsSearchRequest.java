package com.waitnews;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

/**
 * Represents the wait news request to the endpoint /places/search?query=
 * See WaitNews API for more details.
 */
public class WaitNewsSearchRequest extends WaitNewsServiceRequest {
    private static final String API_ENDPOINT = "/places/search";
    
    private String query; /* Search term */
    private List<NameValuePair> params; /* Params associated with the request */
    
    public WaitNewsSearchRequest(String query) {
        this.query = query;
        params = new ArrayList<NameValuePair>();
    }
    
    public String getRequestUrl() {
        return API_ENDPOINT;
    }
    
    public List<NameValuePair> getParams() {
        params.add(new BasicNameValuePair("query", query));
        return params;
    }
    
    public WaitNewsRequestType getRequestType() {
        return WaitNewsRequestType.GET;
    }
}
