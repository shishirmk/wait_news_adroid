package com.waitnews;
/**
 * The base class for all WaitNewsServiceRequest types.
 * It exposes a function getRequestUrl that returns the
 * wait news API end-point for the corresponding request.
 */
public abstract class WaitNewsServiceRequest {
    /**
     * Returns the API end-point to which the request has to be sent.
     * @return API end-point
     */
    public abstract String getRequestUrl();
}
