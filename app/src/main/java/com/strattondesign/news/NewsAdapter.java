package com.strattondesign.news;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class NewsAdapter extends ArrayAdapter<News> {

    private final static String DATE_FORMAT = "dd/MM/yyyy HH:mm";

    /**
     * Constructor
     * @param context   Context
     * @param news      News
     */
    public NewsAdapter(Context context, ArrayList<News> news) {
        super(context, 0, news);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        News news = getItem(position);
        ViewHolder holder;

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
            holder = new ViewHolder();
            holder.tvTitle = (TextView) convertView.findViewById(R.id.title);
            holder.tvDescription = (TextView) convertView.findViewById(R.id.description);
            holder.tvCategory = (TextView) convertView.findViewById(R.id.category);
            holder.tvAuthor = (TextView) convertView.findViewById(R.id.author);
            holder.tvPublishedDate = (TextView) convertView.findViewById(R.id.published_date);
            // Associate the holder with the view for later lookup
            convertView.setTag(holder);
        } else {
            // view already exists, get the holder instance from the view
            holder = (ViewHolder) convertView.getTag();
        }


        holder.tvTitle.setText(news.getTitle());
        holder.tvDescription.setText(news.getDescription());
        holder.tvCategory.setText(news.getCategory());
        holder.tvAuthor.setText(news.getAuthor());

        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
        if (news.getPublishedDate() != null) {
            holder.tvPublishedDate.setText(formatter.format(news.getPublishedDate()));
        } else {
            holder.tvPublishedDate.setText(R.string.no_date);
        }

        return convertView;
    }

    static class ViewHolder {
        TextView tvTitle;
        TextView tvDescription;
        TextView tvCategory;
        TextView tvAuthor;
        TextView tvPublishedDate;
    }
}
