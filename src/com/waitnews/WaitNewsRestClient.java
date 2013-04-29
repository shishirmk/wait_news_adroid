package com.waitnews;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;


/**
 * WaitNewsRestClient allows one to send requests to the WaitNews server over HTTP.
 */
public class WaitNewsRestClient {
    private static final String WAIT_NEWS_HOST = "10.0.0.40";
    private static final int WAIT_NEWS_PORT = 3000;
    private static final HttpHost WAIT_NEWS_SERVER = new HttpHost(WAIT_NEWS_HOST, WAIT_NEWS_PORT);

    private HttpClient mClient; /* HTTP client used to send request */

    public WaitNewsRestClient() {
        mClient = new DefaultHttpClient();
    }
    
    /**
     * Creates and returns a HTTP request for a give WaitNewsService
     * request.
     * @param request one of the WaitNewsServiceRequest types
     * @return HTTPRequest object that can be sent to the wait news server
     *         over HTTP.
     */
    private HttpRequest createHttpRequest(WaitNewsServiceRequest request) {
        HttpRequest httpRequest = null;
        switch(request.getRequestType()) {
            case GET:
                String params = URLEncodedUtils.format(request.getParams(), "utf-8");
                String url = request.getRequestUrl() + "?" + params;
                httpRequest = new HttpGet(url);
                break;
            case POST:
                httpRequest = new HttpPost(request.getRequestUrl());
                break;
        }
        return httpRequest;
    }
    
    /**
     * Executes a WaitNews request and return the response
     * in JSON.
     * 
     * @param request A request of type WaitNewsServiceRequest
     * @return response from the server as a String
     */
    public String execute(WaitNewsServiceRequest request) {
        StringBuilder builder = new StringBuilder();
        try {
            Log.d("Fetching: ", request.getRequestUrl());
            HttpResponse response = mClient.execute(WAIT_NEWS_SERVER, createHttpRequest(request));
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            Log.d(WaitNewsService.class.toString(), statusLine.toString());
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
            } else {
                Log.d(WaitNewsRestClient.class.toString(), "Failed execute query!");
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }
}
