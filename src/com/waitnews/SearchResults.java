package com.waitnews;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

final class SearchResultParser {
    public static ResultRow parseJSONResult(JSONObject resObj) {
        ResultRow result = new ResultRow(resObj);
        return result;
    }
}

public class SearchResults {
    ArrayList<ResultRow> results = new ArrayList<ResultRow>();
    public SearchResults(String jsonString) {
        try {
            JSONObject places = new JSONObject(jsonString);
            JSONArray jsonResults = places.getJSONArray("places");
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
