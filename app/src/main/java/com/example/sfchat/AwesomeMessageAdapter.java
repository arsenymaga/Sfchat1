package com.example.sfchat;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class AwesomeMessageAdapter extends ArrayAdapter<AwesomeMessage> {

    private List<AwesomeMessage> messages;
    private Activity activity;

    public AwesomeMessageAdapter(Activity context, int resource, List<AwesomeMessage> messages) {
        super(context, resource, messages);
        this.messages = messages;
        this.activity = context;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        AwesomeMessage message = getItem(position);
        int layoutResource = 0;
        int viewType = getItemViewType(position);

        if (viewType == 0) {
            layoutResource = R.layout.my_message_item;
        } else {
            layoutResource = R.layout.you_message_item;
        }
        if (convertView != null) {
            viewHolder = (ViewHolder) convertView.getTag();
        } else {
            convertView = layoutInflater.inflate(layoutResource, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }

        boolean isText = message.getImageUrl() == null;
        if (isText) {
            viewHolder.messageTextView.setVisibility(View.VISIBLE);
            viewHolder.photoImageView.setVisibility(View.GONE);
            viewHolder.messageTextView.setText(message.getText());

        } else {
            viewHolder.messageTextView.setVisibility(View.GONE);
            viewHolder.photoImageView.setVisibility(View.VISIBLE);
            Glide.with(viewHolder.photoImageView.getContext())
                    .load(message.getImageUrl()).into(viewHolder.photoImageView);
        }

//        if (convertView == null) {
//            convertView = ((Activity) getContext()).getLayoutInflater()
//                    .inflate(R.layout.text_activity, parent, false);
//        }
//
//        ImageView photoImageView = convertView.findViewById(R.id.image1);
//        TextView textView = convertView.findViewById(R.id.textMessage);
//        TextView nameView = convertView.findViewById(R.id.textName);
//
//
//
//        boolean isText = message.getImageUrl() == null;
//        if (isText) {
//            textView.setVisibility(View.VISIBLE);
//            photoImageView.setVisibility(View.GONE);
//            textView.setText(message.getText());
//
//        } else {
//            textView.setVisibility(View.GONE);
//            photoImageView.setVisibility(View.VISIBLE);
//
//            Glide.with(photoImageView.getContext()).load(message.getImageUrl()).into(photoImageView);
//        }
//        nameView.setText(message.getName());


        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        int flag;
        AwesomeMessage message = messages.get(position);
        if (message.isMine()) {
            flag = 0;
        } else {
            flag = 1;
        }
        return flag;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    private class ViewHolder {

        private TextView messageTextView;
        private ImageView photoImageView;

        public ViewHolder(View view) {
            messageTextView = view.findViewById(R.id.messageTextView);
            photoImageView = view.findViewById(R.id.photoImageView);

        }
    }
}


