package com.waitnews;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Represents the wait news request to the endpoint /places/search?query=
 * See WaitNews API for more details.
 */
public class WaitNewsSearchRequest extends WaitNewsServiceRequest {
    String query; /* Search term */
    private static final String API_ENDPOINT = "/places/search?query=";
    
    public WaitNewsSearchRequest(String query) throws UnsupportedEncodingException {
        this.query = URLEncoder.encode(query, "UTF-8");
    }
    
    public String getRequestUrl() {
        return API_ENDPOINT + query;
    }
}
