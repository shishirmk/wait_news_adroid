package com.waitnews;

import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;

/**
 * The base class for all WaitNewsServiceRequest types.
 */
public abstract class WaitNewsServiceRequest {
    public enum WaitNewsRequestType { GET, POST }; /* Request type */
    
    /**
     * Returns the API end-point to which the request has to be sent.
     * @return API end-point
     */
    public abstract WaitNewsRequestType getRequestType();
    
    /**
     * Returns the type of request i.e. GET, POST.
     * @return one of the WaitNewsRequestType values
     */
    public abstract String getRequestUrl();
    
    /**
     * Returns a List<NameValuePair> of key value pairs to be sent with
     * the request.
     * @return a List<NameValuePair> of key value pairs.
     */
    public abstract List<NameValuePair> getParams();
}
