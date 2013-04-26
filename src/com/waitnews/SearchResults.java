package com.waitnews;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

final class SearchResultParser {
	public static ResultRow parseJSONResult(JSONObject resObj) {
		ResultRow result = null;
		try {
			String name = resObj.getString("name");
			JSONObject addrObj = resObj.getJSONObject("address");
			String address = new String();
			if ( addrObj.getString("line1") != "null" ) { 
				address += addrObj.getString("line1") + " ";
			} 
			if ( addrObj.getString("line2") != "null" ){
				address += addrObj.getString("line2") + " ";
			}
			if ( addrObj.getString("city") != "null" ){
				address += addrObj.getString("city");
			}
			result = new ResultRow(name, address);
		} catch (Exception e) {
			Log.d(SearchResultParser.class.toString(), e.toString());
		}
		return result;
	}
}

public class SearchResults {
	ArrayList<ResultRow> results = new ArrayList<ResultRow>();
	public SearchResults(String jsonString) {
		try {
			JSONArray jsonResults = new JSONArray(jsonString);
	    	for (int i = 0; i < jsonResults.length(); i++) {
				results.add(
					SearchResultParser.parseJSONResult(
						jsonResults.getJSONObject(i)
					)
				);
	    	}
		} catch (Exception e) {
			Log.d(SearchResults.class.toString(), e.toString());
		}
	}
	
	public int size() {
		return results.size();
	}
	
	public ResultRow getResult(int position) {
		ResultRow res = null;
		try {
			res = results.get(position);
		} catch (Exception e) {
			Log.d(SearchResults.class.toString(), "Index out of bounds");
		}
		return res;
	}
}
