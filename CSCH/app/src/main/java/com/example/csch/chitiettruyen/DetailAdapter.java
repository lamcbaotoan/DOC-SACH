package com.example.csch.chitiettruyen;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.csch.R;

import java.util.ArrayList;

public class DetailAdapter extends BaseAdapter {
    private final Context context;
    private final ArrayList<Chapter> chapterList;

    // Constructor
    public DetailAdapter(Context context, ArrayList<Chapter> chapterList) {
        this.context = context;
        this.chapterList = chapterList;
    }

    @Override
    public int getCount() {
        return chapterList.size();
    }

    @Override
    public Object getItem(int position) {
        return chapterList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_chapter, parent, false);
            holder = new ViewHolder();
            holder.tvTitle = convertView.findViewById(R.id.tvTitle);
            holder.tvIndex = convertView.findViewById(R.id.tvIndex);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Set data to views
        Chapter chapter = chapterList.get(position);
        holder.tvTitle.setText(chapter.getTitle());
        holder.tvIndex.setText("Chapter " + chapter.getIndex());

        return convertView;
    }

    static class ViewHolder {
        TextView tvTitle;
        TextView tvIndex;
    }




}
