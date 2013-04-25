package com.example.waitnews;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ResultListAdapter extends ArrayAdapter<ResultRow>{

    Context context; 
    int layoutResourceId;    
    ArrayList<ResultRow> data = null;
    
    public ResultListAdapter(Context context, int layoutResourceId, ResultRow[] data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = new ArrayList<ResultRow>();
        for (int i = 0; i < data.length; i++) {
        	this.data.add(data[i]);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ResultHolder holder = null;
        
        if(row == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            
            holder = new ResultHolder();
            holder.place = (TextView)row.findViewById(R.id.item_place);
            holder.address = (TextView)row.findViewById(R.id.item_address);
            
            row.setTag(holder);
        } else {
            holder = (ResultHolder)row.getTag();
        }
        
        ResultRow result = data.get(position);
        holder.place.setText(result.place);
        holder.address.setText(result.address);
        
        return row;
    }
    
    public void add(ResultRow obj) {
    	data.add(obj);
    }
    
    static class ResultHolder {
        TextView place;
        TextView address;
    }
}