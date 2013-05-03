package com.waitnews;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class ResultListAdapter extends ArrayAdapter<ResultRow>{

    Context context; 
    int layoutResourceId;    
    ArrayList<ResultRow> data = new ArrayList<ResultRow>();

    public ResultListAdapter(Context context, int layoutResourceId, ArrayList<ResultRow> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ResultHolder holder = null;
        ResultRow result = null;

        if(row == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ResultHolder(row);
            row.setTag(holder);
        } else {
            holder = (ResultHolder)row.getTag();
        }

        try {
            result = data.get(position);
        } catch (Exception e) {
            Log.d(ResultListAdapter.class.toString(), e.toString());
        }
        if (result != null) {
            holder.populate(result);
        }

        return row;
    }

    public void add(ResultRow obj) {
        this.data.add(obj);
        this.notifyDataSetChanged();
    }

    public void clear(){
        this.data.clear();
        this.notifyDataSetChanged();
    }
}