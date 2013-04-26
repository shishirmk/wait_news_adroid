package com.waitnews;
import com.waitnews.WaitNewsService.WaitNewsServiceInt;
public class Request {
	private String query;
	private long requestID;
	private WaitNewsServiceInt callBack;
	public Request(String q, long id, WaitNewsServiceInt cb) {
		query = q;
		requestID = id;
		callBack = cb;
	}
	public String getQueryString() {
		return query;
	}
	public long getRequestID() {
		return requestID;
	}
	public WaitNewsServiceInt getCallBack() {
		return callBack;
	}
}
